package georgeh.test.axonframework.multimaster;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

public class CounterIncreaseCommand {

	@TargetAggregateIdentifier
	private final String id;

	public CounterIncreaseCommand(String id) {
		super();
		this.id = id;
	}

	public String getId() {
		return id;
	}
	
}
