package org.uci.iot.smarthome.dustSensor.webSockets;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.uci.iot.smarthome.dustSensor.service.DataService;
import org.uci.iot.smarthome.dustSensor.vo.Dust;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.eventhubs.ConnectionStringBuilder;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubException;
import com.microsoft.azure.eventhubs.EventHubRuntimeInformation;
import com.microsoft.azure.eventhubs.EventPosition;
import com.microsoft.azure.eventhubs.PartitionReceiver;

@Component
public class MessageHandler extends TextWebSocketHandler{
	
	 @Autowired
	 private DataService dataService;
	
	// az iot hub show --query properties.eventHubEndpoints.events.endpoint --name {your IoT Hub name}
	  private static final String eventHubsCompatibleEndpoint = "sb://ihsuprodbyres016dednamespace.servicebus.windows.net/";

	  // az iot hub show --query properties.eventHubEndpoints.events.path --name {your IoT Hub name}
	  private static final String eventHubsCompatiblePath = "iothub-ehub-parkwest-961171-acb28f19c5";

	  // az iot hub policy show --name iothubowner --query primaryKey --hub-name {your IoT Hub name}
	  private static final String iotHubSasKey = "jFTlEAi0B65dU962Qq0FNtCMWlUnjWugTKQ0LBh3MWE=";
	  private static final String iotHubSasKeyName = "iothubowner";

	  // Track all the PartitionReciever instances created.
	  private static ArrayList<PartitionReceiver> receivers = new ArrayList<PartitionReceiver>();

	  // Asynchronously create a PartitionReceiver for a partition and then start 
	  // reading any messages sent from the simulated client.
	  private  void receiveMessages(WebSocketSession session, EventHubClient ehClient, String partitionId)
	      throws EventHubException, ExecutionException, InterruptedException {

	    final ExecutorService executorService = Executors.newSingleThreadExecutor();

	    // Create the receiver using the default consumer group.
	    // For the purposes of this sample, read only messages sent since 
	    // the time the receiver is created. Typically, you don't want to skip any messages.
	    ehClient.createReceiver(EventHubClient.DEFAULT_CONSUMER_GROUP_NAME, partitionId,
	        EventPosition.fromEnqueuedTime(Instant.now())).thenAcceptAsync(receiver -> {
	          System.out.println(String.format("Starting receive loop on partition: %s", partitionId));
	          System.out.println(String.format("Reading messages sent since: %s", Instant.now().toString()));

	          receivers.add(receiver);

	          while (true) {
	            try {
	              // Check for EventData - this methods times out if there is nothing to retrieve.
	              Iterable<EventData> receivedEvents = receiver.receiveSync(100);

	              // If there is data in the batch, process it.
	              if (receivedEvents != null) {
	                for (EventData receivedEvent : receivedEvents) {
	                  String s=new String(receivedEvent.getBytes(), Charset.defaultCharset());
	                  JSONObject jsonObj = new JSONObject(s);
	                  Date date=new Date();
	                  DateFormat df = new SimpleDateFormat("HH:mm:ss");
	                  String dateString = df.format(date);
	                  jsonObj.append("date", dateString);
	                  session.sendMessage(new TextMessage(jsonObj.toString()));
	                  System.out.println(jsonObj.toString());
	                  final String uri = "http://api.aerisapi.com/observations/irvine,ca?client_id=l4sr2Uscvpn7C0taxFo5s&client_secret=VFcJTPiSHkqawecCxBghOZ1zOJX91WJe4ycg1Zaj";
	                  RestTemplate restTemplate = new RestTemplate();
	                  String result = restTemplate.getForObject(uri, String.class);
	                  System.out.println(result);
	                  float wind= 0;
	                  System.out.println(wind);
	                  dataService.pushData(new Dust(date,Float.parseFloat(jsonObj.get("dustDensity").toString()),
	                		  Float.parseFloat(jsonObj.get("humidity").toString()),
	                  			Float.parseFloat(jsonObj.get("temperature").toString()),
	                  			wind));
	                  
	                  
	                  System.out.println("dustDensity "+jsonObj.get("dustDensity"));
	                  System.out.println(String.format("Application properties (set by device):\n%s",receivedEvent.getProperties().toString()));
	                  System.out.println(String.format("System properties (set by IoT Hub):\n%s\n",receivedEvent.getSystemProperties().toString()));
	                }
	              }
	            } catch (EventHubException e) {
	              System.out.println("Error reading EventData");
	            } catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	          }
	        }, executorService);
	  }

	@Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // The WebSocket has been closed
    }
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // The WebSocket has been opened
        // I might save this session object so that I can send messages to it outside of this method
        // Let's send the first message
        //session.sendMessage(new TextMessage("You are now connected to the server. This is the first message."));
        getDataFromDeviceConnectionString(session);
    }
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        // A message has been received
        System.out.println("Message received: " + textMessage.getPayload());
        getDataFromDeviceConnectionString(session);
        //session.sendMessage(new TextMessage("Next message."));
        
    }
    
    private void getDataFromDeviceConnectionString(WebSocketSession session) throws URISyntaxException, EventHubException, IOException, InterruptedException, ExecutionException {
    	
    	final ConnectionStringBuilder connStr = new ConnectionStringBuilder()
		        .setEndpoint(new URI(eventHubsCompatibleEndpoint))
		        .setEventHubName(eventHubsCompatiblePath)
		        .setSasKeyName(iotHubSasKeyName)
		        .setSasKey(iotHubSasKey);

		    // Create an EventHubClient instance to connect to the
		    // IoT Hub Event Hubs-compatible endpoint.
		    final ExecutorService executorService = Executors.newSingleThreadExecutor();
		    final EventHubClient ehClient = EventHubClient.createSync(connStr.toString(), executorService);

		    // Use the EventHubRunTimeInformation to find out how many partitions 
		    // there are on the hub.
		    final EventHubRuntimeInformation eventHubInfo = ehClient.getRuntimeInformation().get();

		    // Create a PartitionReciever for each partition on the hub.
		    for (String partitionId : eventHubInfo.getPartitionIds()) {
		      receiveMessages(session, ehClient, partitionId);
		    }

		    // Shut down cleanly.
		    System.out.println("Press ENTER to exit.");
		    System.in.read();
		    System.out.println("Shutting down...");
		    for (PartitionReceiver receiver : receivers) {
		      receiver.closeSync();
		    }
		    ehClient.closeSync();
		    executorService.shutdown();
    	
    }
    
    float roundTwoDecimals(float d) {
	  DecimalFormat twoDForm = new DecimalFormat("#.##");
	  return Float.valueOf(twoDForm.format(d));
	}
    
}
