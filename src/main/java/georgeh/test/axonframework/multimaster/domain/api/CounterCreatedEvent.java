package georgeh.test.axonframework.multimaster.domain.api;

public class CounterCreatedEvent {

	private final String id;

	public CounterCreatedEvent(String id) {
		super();
		this.id = id;
	}

	public String getId() {
		return id;
	}

}
