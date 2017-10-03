package georgeh.test.axonframework.multimaster.domain.api;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

public class CounterCreateCommand {

	@TargetAggregateIdentifier
	private final String id;

	public CounterCreateCommand(String id) {
		super();
		this.id = id;
	}

	public String getId() {
		return id;
	}
	
}
