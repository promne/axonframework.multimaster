package georgeh.test.axonframework.multimaster;

public class CounterValueSetEvent {

	private final String id;
	private final int value;

	public CounterValueSetEvent(String id, int value) {
		this.id = id;
		this.value = value;
	}

	public String getId() {
		return id;
	}

	public int getValue() {
		return value;
	}

}
