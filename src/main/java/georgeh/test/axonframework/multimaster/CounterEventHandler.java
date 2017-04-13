package georgeh.test.axonframework.multimaster;

import org.axonframework.eventhandling.EventHandler;

public class CounterEventHandler {

	@EventHandler
	public void handle(CounterCreatedEvent event) {
		Main.log("* Handling CounterCreatedEvent event for: " + event.getId());
	}

	@EventHandler
	public void handle(CounterValueSetEvent event) {
		Main.log("* Handling CounterValueSetEvent event for: " + event.getId() + " with value: " + event.getValue());
	}
	
}
