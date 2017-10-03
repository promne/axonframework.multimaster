package georgeh.test.axonframework.multimaster;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import javax.inject.Inject;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.impl.SimpleLogger;

import georgeh.test.axonframework.multimaster.domain.api.CounterCreateCommand;

public class Main {

    @Inject
    private Logger log;
	
    @Inject
    private CommandGateway commandGateway;
    
    public static void main(String[] args) throws Exception {
        System.setProperty(SimpleLogger.LOG_KEY_PREFIX + Main.class.getPackage().getName(), "DEBUG");
//        System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");
//        System.setProperty("org.slf4j.simpleLogger.logFile", "server.log");
        System.setProperty(SimpleLogger.DATE_TIME_FORMAT_KEY, "yyyy-MM-dd HH:mm:ss:SSS Z");
        System.setProperty(SimpleLogger.SHOW_DATE_TIME_KEY, "true");
        try (SeContainer container = SeContainerInitializer.newInstance().initialize()) {
            // start CDI
            container.select(Main.class).get().work();
        }
    }
    
	public void work() throws Exception {
		List<String> counterIds = IntStream.range(0, 5).mapToObj(i -> UUID.randomUUID().toString()).collect(Collectors.toList());

		counterIds.forEach(id -> {
			log.debug("Sending create command to {}", id);
			commandGateway.sendAndWait(new CounterCreateCommand(id));
		});
		
//		for (int i=0; i<100_000; i++) {
//			counterIds.forEach(id -> {
//				log.debug("Sending increase command to {}", id);
//				configuration.commandGateway().sendAndWait(new CounterIncreaseCommand(id));
//			});			
//		}
		
		Thread.sleep(1000);
//		Main.log("Event store dump");
//		configuration.eventStore().readEvents("0").asStream().forEachOrdered(e -> Main.log("%s %s %s %s", e.getAggregateIdentifier(), e.getType(), e.getSequenceNumber(), e.getPayloadType()));
		
	}

	
	
}
