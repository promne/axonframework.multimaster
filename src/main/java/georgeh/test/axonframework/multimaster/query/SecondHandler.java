package georgeh.test.axonframework.multimaster.query;

import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import georgeh.test.axonframework.multimaster.domain.api.CounterCreatedEvent;
import georgeh.test.axonframework.multimaster.domain.api.CounterValueSetEvent;

public class SecondHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CounterEventHandler.class);

    @EventHandler
    public void handle(CounterCreatedEvent event) {
        LOG.debug("SH: Handling CounterCreatedEvent event for: {}", event.getId());
    }

    @EventHandler
    public void handle(CounterValueSetEvent event) {
        LOG.debug("SH Handling CounterValueSetEvent event for: {} with value: {}", event.getId(), event.getValue());
    }
    
}
