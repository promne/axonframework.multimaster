package georgeh.test.axonframework.multimaster.query;

import javax.enterprise.inject.spi.CDI;

import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import georgeh.test.axonframework.multimaster.domain.api.CounterCreatedEvent;
import georgeh.test.axonframework.multimaster.domain.api.CounterValueSetEvent;

@ProcessingGroupTarget(Counter.class)
public class CounterEventHandler {
    
    private static final Logger LOG = LoggerFactory.getLogger(CounterEventHandler.class);

    private CounterRepository repo = CDI.current().select(CounterRepository.class).get(); //because axon has no CDI enabled
    
	@EventHandler
	public void handle(CounterCreatedEvent event) {
		LOG.debug("Handling CounterCreatedEvent event for: {}", event.getId());
		repo.save(new Counter(event.getId()));
	}

	@EventHandler
	public void handle(CounterValueSetEvent event) {
		LOG.debug("Handling CounterValueSetEvent event for: {} with value: {}", event.getId(), event.getValue());
		Counter counter = repo.findOne(event.getId());	
		counter.setValue(event.getValue());
		repo.save(counter);
	}
	
}
