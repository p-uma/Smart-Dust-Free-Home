package org.uci.iot.smarthome.dustSensor.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.uci.iot.smarthome.dustSensor.service.DataService;
import org.uci.iot.smarthome.dustSensor.service.EmailService;
import org.uci.iot.smarthome.dustSensor.service.UserService;
import org.uci.iot.smarthome.dustSensor.vo.Dust;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.device.IotHubEventCallback;
import com.microsoft.azure.sdk.iot.device.IotHubStatusCode;
import com.microsoft.azure.sdk.iot.device.Message;

@EnableScheduling
@Controller
public class HomeController {

	@Autowired
	private EmailService emailService;

	@Autowired
	private DataService dataService;

	@Autowired
	private UserService userService;

	private static String connString = "HostName=parkwest.azure-devices.net;DeviceId=dustSensor;SharedAccessKey=XwthdHgF7gGM2AW82ngKrHHGTuicpv/Pn4VBqUZtvgY=";

	// Using the MQTT protocol to connect to IoT Hub
	private static IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;
	private static DeviceClient client;

	// Specify the telemetry to send to your IoT hub.
	private static class TelemetryDataPoint {
		public double dustDensity;
		public double temperature;
		public double humidity;

		// Serialize object to JSON format.
		public String serialize() {

			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String json = null;
			try {
				json = ow.writeValueAsString(this);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			return json;
		}
	}

	// Print the acknowledgement received from IoT Hub for the telemetry message
	// sent.
	private static class EventCallback implements IotHubEventCallback {
		public void execute(IotHubStatusCode status, Object context) {
			System.out.println("IoT Hub responded to message with status: " + status.name());

			if (context != null) {
				synchronized (context) {
					context.notify();
				}
			}
		}
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView home(ModelMap model) {
		Date start = new Date();
		Date end = new Date();

		Date dt = DateUtils.addDays(new Date(), -7);
		List<Dust> dust = dataService.getData(start, end);
		System.out.println(dust.size());
		return new ModelAndView("index");

	}

	@RequestMapping(value = "/liveData", method = RequestMethod.GET)
	public ModelAndView liveData(ModelMap model) {

		return new ModelAndView("live");

	}

	@RequestMapping(value = "/pastData", method = RequestMethod.GET)
	public ModelAndView pastData(ModelMap model) {

		return new ModelAndView("past");

	}

	@RequestMapping(value = "/futureData", method = RequestMethod.GET)
	public ModelAndView futureData(ModelMap model) {

		return new ModelAndView("future");

	}

	@RequestMapping(value = "/sendData", method = RequestMethod.POST)
	@ResponseBody
	public String sendData(@RequestBody String s) {

		try {
			client = new DeviceClient(connString, protocol);
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		try {
			client.open();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		HashMap<String, Object> result = null;
		System.out.println(s);
		try {
			result = new ObjectMapper().readValue(s, HashMap.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(result.get("dustDensity"));
		System.out.println(result.get("date"));
		TelemetryDataPoint telemetryDataPoint = new TelemetryDataPoint();
		double dustDensity = Double.parseDouble((String) result.get("dustDensity"));
		double temperature = Double.parseDouble((String) result.get("temperature"));
		double humidity = Double.parseDouble((String) result.get("humidity"));
		telemetryDataPoint.dustDensity = dustDensity;
		telemetryDataPoint.temperature = temperature;
		telemetryDataPoint.humidity = humidity;

		// Add the telemetry to the message body as JSON.
		String msgStr = telemetryDataPoint.serialize();
		Message msg = new Message(msgStr);

		// Add a custom application property to the message.
		// An IoT hub can filter on these properties without access to the message body.
		msg.setProperty("temperatureAlert", (dustDensity > 0) ? "true" : "false");

		System.out.println("Sending message: " + msgStr);

		Object lockobj = new Object();

		// Send the message.
		EventCallback callback = new EventCallback();
		client.sendEventAsync(msg, callback, lockobj);

		synchronized (lockobj) {
			try {
				lockobj.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			client.closeNow();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";

	}

	@RequestMapping(value = "/getPastData", method = RequestMethod.GET)
	@ResponseBody
	public List<Dust> getPastData(@RequestParam(value = "date") String dateString) {

		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		Date date = null;
		try {
			date = df.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		System.out.println(date);
		System.out.println(date);
		return dataService.getData(date, new Date());
	}

	@RequestMapping(value = "/getFutureData", method = RequestMethod.GET)
	@ResponseBody
	public List<Dust> getFutureData(@RequestParam(value = "date") String dateString) {

		List<Dust> dustData = new ArrayList<>();
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		Date date = null;
		try {
			date = df.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		final String uri = "http://api.aerisapi.com/forecasts/irvine,ca?from=" + dateString
				+ "&client_id=l4sr2Uscvpn7C0taxFo5s&client_secret=VFcJTPiSHkqawecCxBghOZ1zOJX91WJe4ycg1Zaj";
		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject(uri, String.class);
		System.out.println(result);
		for (int i = 0; i < 48; i++) {
			System.out.println(date);
			date = DateUtils.addMinutes(date, 30);
			DateFormat df1 = new SimpleDateFormat("MM/dd/yyyy ");
			float temperature = 0;
			float humidity = 0;
			float wind = 0;

			// http://www.xuru.org/rt/MLR.asp used for Regression
			float dustDensity = roundTwoDecimals(
					(float) ((float) (0.000477) * temperature - 0.000155 * humidity - 0.00704 * wind + 0.1945));
			Dust dust = new Dust(date, humidity, temperature, wind);
			// System.out.println(dustDensity);
			dustData.add(dust);
		}
		return dustData;
	}

	@RequestMapping(value = "/notification", method = RequestMethod.GET)
	@ResponseBody
	public String notification(@RequestParam(value = "type") String type) {
		String air;
		if (type.equals("1")) {
			air = "ON";
		} else {
			air = "OFF";
		}
		emailService.sendMail("xxxxx@gmail.com", "Smart Home", "Air Purifier is turned " + air);
		emailService.sendMail("949xxxxxxx@tmomail.net", "Smart Home", "Air Purifier is turned " + air);
		emailService.sendMail("949xxxxxxx@tmomail.net", "Smart Home", "Air Purifier is turned " + air);

		return "";
	}


	float roundTwoDecimals(float d) {
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Float.valueOf(twoDForm.format(d));
	}
}
