package org.uci.iot.smarthome.dustSensor;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.ibatis.type.MappedTypes;
import org.json.JSONObject;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.uci.iot.smarthome.dustSensor.vo.User;

import com.microsoft.azure.eventhubs.ConnectionStringBuilder;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubException;
import com.microsoft.azure.eventhubs.EventHubRuntimeInformation;
import com.microsoft.azure.eventhubs.EventPosition;
import com.microsoft.azure.eventhubs.PartitionReceiver;

import jdk.nashorn.internal.parser.JSONParser;

@MappedTypes(User.class )
@MapperScan("org.uci.iot.smarthome.dustSensor.dao")
@SpringBootApplication
public class DustSensorApplication extends SpringBootServletInitializer{
	
	  /*// az iot hub show --query properties.eventHubEndpoints.events.endpoint --name {your IoT Hub name}
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
	  private static void receiveMessages(EventHubClient ehClient, String partitionId)
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
	                  System.out.println(String.format("Telemetry received:\n %s",
	                      new String(receivedEvent.getBytes(), Charset.defaultCharset())));
	                  String s=new String(receivedEvent.getBytes(), Charset.defaultCharset());
	                  JSONObject jsonObj = new JSONObject(s);
	                  System.out.println("temperature "+jsonObj.get("temperature"));
	                  System.out.println("humidity "+jsonObj.get("humidity"));
	                  System.out.println(String.format("Application properties (set by device):\n%s",receivedEvent.getProperties().toString()));
	                  System.out.println(String.format("System properties (set by IoT Hub):\n%s\n",receivedEvent.getSystemProperties().toString()));
	                }
	              }
	            } catch (EventHubException e) {
	              System.out.println("Error reading EventData");
	            }
	          }
	        }, executorService);
	  }*/
	 
	public static void main(String[] args) throws URISyntaxException, InterruptedException, ExecutionException, EventHubException, IOException {
		
		SpringApplication.run(DustSensorApplication.class, args);
		
		/*final ConnectionStringBuilder connStr = new ConnectionStringBuilder()
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
		      receiveMessages(ehClient, partitionId);
		    }

		    // Shut down cleanly.
		    System.out.println("Press ENTER to exit.");
		    System.in.read();
		    System.out.println("Shutting down...");
		    for (PartitionReceiver receiver : receivers) {
		      receiver.closeSync();
		    }
		    ehClient.closeSync();
		    executorService.shutdown();*/
		    
	}
}
