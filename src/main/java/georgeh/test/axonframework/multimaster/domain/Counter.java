package georgeh.test.axonframework.multimaster.domain;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.commandhandling.model.AggregateLifecycle;
import org.axonframework.commandhandling.model.AggregateRoot;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.messaging.MetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import georgeh.test.axonframework.multimaster.domain.api.CounterCreateCommand;
import georgeh.test.axonframework.multimaster.domain.api.CounterCreatedEvent;
import georgeh.test.axonframework.multimaster.domain.api.CounterIncreaseCommand;
import georgeh.test.axonframework.multimaster.domain.api.CounterValueSetEvent;
import georgeh.test.axonframework.multimaster.query.CounterEventHandler;

@AggregateRoot
public class Counter {

    private static final Logger LOG = LoggerFactory.getLogger(CounterEventHandler.class);
    
	@AggregateIdentifier
	private String id;
	
	private int counter;
	
	public Counter() {
		super();
		LOG.debug("Creating {}", System.identityHashCode(Counter.this));
	}

	@CommandHandler
	public Counter(CounterCreateCommand command, MetaData metaData) {
		super();
		LOG.debug("CounterCreateCommand received for {} created {}", command.getId(), System.identityHashCode(Counter.this));
		AggregateLifecycle.apply(new CounterCreatedEvent(command.getId()), metaData);
	}

	@EventSourcingHandler
	public void on(CounterCreatedEvent e) {
		this.id = e.getId();
		this.counter = 0;
		LOG.debug("counter {} consuming create : {}", id, System.identityHashCode(Counter.this));
	}
	
	@CommandHandler
	public int increase(CounterIncreaseCommand cmd, MetaData metaData) {
		LOG.debug("CounterIncreaseCommand received for {}", cmd.getId());
		counter++;
		AggregateLifecycle.apply(new CounterValueSetEvent(cmd.getId(), counter), metaData);
		return counter;
	}
	
	@EventSourcingHandler
	public void on(CounterValueSetEvent e) {
		LOG.debug("counter {} consuming counter set to {} : {}", id, e.getValue(), System.identityHashCode(Counter.this));
		counter = e.getValue();
	}
	
}
