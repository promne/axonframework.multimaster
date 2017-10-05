package georgeh.test.axonframework.multimaster.query;

import org.springframework.data.annotation.Id;

import georgeh.test.axonframework.multimaster.mongo.DocumentNameParts;

@DocumentNameParts(documentType = Counter.class, handlerType=CounterEventHandler.class)
public class Counter {

    @Id
    private String id;

    private long value;
    
    public Counter() {
        super();
    }

    public Counter(String id) {
        super();
        this.id = id;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
        
}
