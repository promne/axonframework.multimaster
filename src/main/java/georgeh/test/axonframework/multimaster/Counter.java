package georgeh.test.axonframework.multimaster;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.commandhandling.model.AggregateLifecycle;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.messaging.MetaData;

public class Counter {

	@AggregateIdentifier
	private String id;
	
	private int counter;
	
	public Counter() {
		super();
		Main.log("Creating " + System.identityHashCode(Counter.this));
	}

	@CommandHandler
	public Counter(CounterCreateCommand command, MetaData metaData) {
		super();
		Main.log("+++ CounterCreateCommand received for " + command.getId() + " created " + System.identityHashCode(Counter.this));
		AggregateLifecycle.apply(new CounterCreatedEvent(command.getId()), metaData);
	}

	@EventSourcingHandler
	public void on(CounterCreatedEvent e) {
		this.id = e.getId();
		this.counter = 0;
		Main.log("+ counter %s consuming create : %s", id, System.identityHashCode(Counter.this));
	}
	
	@CommandHandler
	public void increase(CounterIncreaseCommand cmd, MetaData metaData) {
		Main.log("+++ CounterIncreaseCommand received for " + cmd.getId());
		counter++;
		AggregateLifecycle.apply(new CounterValueSetEvent(cmd.getId(), counter), metaData);
	}
	
	@EventSourcingHandler
	public void on(CounterValueSetEvent e) {
		Main.log("+ counter %s consuming counter set to %s : %s", id, e.getValue(), System.identityHashCode(Counter.this));
		counter = e.getValue();
	}
	
}
