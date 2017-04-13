package georgeh.test.axonframework.multimaster;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.commandhandling.distributed.DistributedCommandBus;
import org.axonframework.config.Configuration;
import org.axonframework.config.Configurer;
import org.axonframework.config.DefaultConfigurer;
import org.axonframework.config.EventHandlingConfiguration;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.jgroups.commandhandling.JGroupsConnector;
import org.axonframework.messaging.MetaData;
import org.axonframework.mongo.eventsourcing.eventstore.DefaultMongoTemplate;
import org.axonframework.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.axonframework.mongo.eventsourcing.eventstore.MongoTemplate;
import org.axonframework.serialization.xml.XStreamSerializer;
import org.jgroups.JChannel;

import com.mongodb.MongoClient;

public class Main {
	
	public static void log(String format, Object... params) {
		System.out.println(String.format("%s [%s]", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME), Thread.currentThread().getName()) + String.format(format, params));
	}
	
	public static void main(String[] args) throws Exception {		
		Configurer configurer = DefaultConfigurer
				.defaultConfiguration()
				.configureEventStore(c -> new EmbeddedEventStore(defaultMongoEventStore()));
//				.configureEventStore(c -> new EmbeddedEventStore(new InMemoryEventStorageEngine()));
		
		
		configurer.configureCommandBus(createDistributedCommandBus());

		
		configurer.configureAggregate(Counter.class);
		
		
		EventHandlingConfiguration ehConfiguration = new EventHandlingConfiguration()
				.registerEventHandler(c -> new CounterEventHandler());
		configurer.registerModule(ehConfiguration);
		
		Configuration configuration = configurer.buildConfiguration();
		configuration.start();

		
		
		List<String> counterIds = IntStream.range(0, 1).mapToObj(Integer::toString).collect(Collectors.toList());

		counterIds.forEach(id -> {
			log("Sending create command to " + id);
			configuration.commandGateway().send(new CounterCreateCommand(id));
		});
		counterIds.forEach(id -> {
			log("Sending create command to " + id);
			configuration.commandGateway().send(new CounterCreateCommand(id));
		});
		
		for (int i=0; i<0; i++) {
			counterIds.forEach(id -> {
				log("Sending increase command to " + id);
				configuration.commandGateway().send(new CounterIncreaseCommand(id));
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});			
		}
		
	}

	private static Function<Configuration, CommandBus> createDistributedCommandBus() throws Exception {
		SimpleCommandBus localSegment = new SimpleCommandBus();
		JGroupsConnector connector = new JGroupsConnector(localSegment, new JChannel(), "myCommandBus", new XStreamSerializer());
		
		localSegment.registerDispatchInterceptor(messages -> (t,m) -> m.andMetaData(MetaData.with("processor", connector.getNodeName())));
		
		connector.connect();
		
		DistributedCommandBus distributedCommandBus = new DistributedCommandBus(connector, connector);
		distributedCommandBus.registerDispatchInterceptor(messages -> (t,m) -> m.andMetaData(MetaData.with("requestor", connector.getNodeName())));
		
		return c -> distributedCommandBus;
	}
	
	public static MongoEventStorageEngine defaultMongoEventStore() {
		// MongoClient mongoClient = new
		// MongoFactory().setMongoAddresses(Arrays.asList(new
		// ServerAddress("localhost")));
		char[] password = null;
		String userName = null;
		MongoClient mongoClient = new MongoClient("10.128.75.101");
		
		
		
		String snapshotEventsCollectionName = "snapshotevents";
		String domainEventsCollectionName = "domainevents";
		String databaseName = "exchange";
		MongoTemplate mongoTemplate = new DefaultMongoTemplate(mongoClient, databaseName, domainEventsCollectionName,
				snapshotEventsCollectionName);
		return new MongoEventStorageEngine(mongoTemplate);

		// return new MongoEventStore(mongoTemplate);
	}	
	
//	public static void main(String[] args) throws Exception {
//		final CdiContainer container = CdiContainerLoader.getCdiContainer();
//		try {
//			container.boot();
//
//			CounterRunner quickstart = CDI.current().select(CounterRunner.class).get();
//			quickstart.run();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			container.shutdown();
//			System.out.println("Shutting down...");
//			System.exit(0);
//		}
//	}
}
