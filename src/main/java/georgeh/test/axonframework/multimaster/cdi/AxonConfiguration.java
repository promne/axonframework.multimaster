package georgeh.test.axonframework.multimaster.cdi;

import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.commandhandling.distributed.DistributedCommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.Configuration;
import org.axonframework.config.Configurer;
import org.axonframework.config.DefaultConfigurer;
import org.axonframework.config.EventHandlingConfiguration;
import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.eventhandling.tokenstore.inmemory.InMemoryTokenStore;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.jgroups.commandhandling.JGroupsConnector;
import org.axonframework.messaging.MetaData;
import org.axonframework.mongo.eventsourcing.eventstore.DefaultMongoTemplate;
import org.axonframework.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.axonframework.mongo.eventsourcing.eventstore.MongoTemplate;
import org.axonframework.mongo.eventsourcing.tokenstore.MongoTokenStore;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.xml.XStreamSerializer;
import org.jgroups.JChannel;
import org.slf4j.Logger;

import com.mongodb.MongoClient;

import georgeh.test.axonframework.multimaster.domain.Counter;
import georgeh.test.axonframework.multimaster.query.CounterEventHandler;

@ApplicationScoped
public class AxonConfiguration {

    @Inject
    private Logger log;
    
    private String DATABASE_NAME = "axonframework";
    private Serializer SERIALIZER = new XStreamSerializer();
    
    public Configurer getConfigurer() {
        Configurer result = DefaultConfigurer.defaultConfiguration();
    
//        result.configureCommandBus(c -> createDistributedCommandBus());
        
        
        result.configureEventStore(c -> defaultMongoEventStore());
        result.registerComponent(TokenStore.class, c -> defaultMongoTokenStore());

//        result.configureEventStore(c -> new EmbeddedEventStore(new InMemoryEventStorageEngine()));
//        result.registerComponent(TokenStore.class, c -> new InMemoryTokenStore());
        
        EventHandlingConfiguration ehConfiguration = new EventHandlingConfiguration()
            .usingTrackingProcessors()
            .registerEventHandler(c -> new CounterEventHandler());
        
        result.registerModule(ehConfiguration);

        result.configureAggregate(Counter.class);
        
        return result;
        
    }
    
    @Produces
    @ApplicationScoped
    public Configuration getConfiguration() {
        Configuration configuration = getConfigurer().buildConfiguration();
        configuration.start();
        return configuration;
    }
    
    @Produces
    public TokenStore getTokenStore(Configuration c) {
        return c.getComponent(TokenStore.class);
    }

    @Produces
    public EventStore getEventStore(Configuration c) {
        return c.eventStore();
    }

    @Produces
    public CommandGateway getCommandGateway(Configuration c) {
        return c.commandGateway();
    }

    
    public TokenStore defaultMongoTokenStore() {
        return new MongoTokenStore(new org.axonframework.mongo.eventsourcing.tokenstore.DefaultMongoTemplate(new MongoClient("10.128.75.101"), DATABASE_NAME, "trackingTokens"), SERIALIZER);
    }
    
    
    public EventStore defaultMongoEventStore() {
        MongoClient mongoClient = new MongoClient("10.128.75.101");
        
        String snapshotEventsCollectionName = "snapshotevents";
        String domainEventsCollectionName = "domainevents";
        
        MongoTemplate mongoTemplate = new DefaultMongoTemplate(mongoClient, DATABASE_NAME, domainEventsCollectionName,
                snapshotEventsCollectionName);
        return new EmbeddedEventStore((new MongoEventStorageEngine(mongoTemplate)));
    }       
    
    
    private CommandBus createDistributedCommandBus() {
        SimpleCommandBus localSegment = new SimpleCommandBus();
        
        try (JChannel channel = new JChannel()) {
            JGroupsConnector connector = new JGroupsConnector(localSegment, channel, "myCommandBus", new XStreamSerializer());
            
            localSegment.registerDispatchInterceptor(messages -> (t,m) -> m.andMetaData(MetaData.with("processor", connector.getNodeName())));
            
            connector.connect();
            
            DistributedCommandBus distributedCommandBus = new DistributedCommandBus(connector, connector);
            distributedCommandBus.registerDispatchInterceptor(messages -> (t,m) -> m.andMetaData(MetaData.with("requestor", connector.getNodeName())));
            
            return distributedCommandBus;
        } catch (Exception e) {
            throw new IllegalStateException("Something went wrong", e);
        }        
    }    
}
