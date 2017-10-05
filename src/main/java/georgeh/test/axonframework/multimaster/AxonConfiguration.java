package georgeh.test.axonframework.multimaster;

import java.io.ObjectStreamClass;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.commandhandling.distributed.DistributedCommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.common.annotation.AnnotationUtils;
import org.axonframework.config.Configuration;
import org.axonframework.config.Configurer;
import org.axonframework.config.DefaultConfigurer;
import org.axonframework.config.EventHandlingConfiguration;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.jgroups.commandhandling.JGroupsConnector;
import org.axonframework.messaging.MetaData;
import org.axonframework.mongo.eventsourcing.eventstore.DefaultMongoTemplate;
import org.axonframework.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.axonframework.mongo.eventsourcing.eventstore.MongoTemplate;
import org.axonframework.mongo.eventsourcing.eventstore.documentperevent.DocumentPerEventStorageStrategy;
import org.axonframework.mongo.eventsourcing.tokenstore.MongoTokenStore;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.xml.XStreamSerializer;
import org.jgroups.JChannel;
import org.slf4j.Logger;

import com.mongodb.MongoClient;

import georgeh.test.axonframework.multimaster.domain.Counter;
import georgeh.test.axonframework.multimaster.query.CounterEventHandler;
import georgeh.test.axonframework.multimaster.query.ProcessingGroupTarget;
import georgeh.test.axonframework.multimaster.query.SecondHandler;
import georgeh.test.axonframework.multimaster.util.ClassHashUtils;

@ApplicationScoped
public class AxonConfiguration {

    @Inject
    private Logger log;
    
    private Serializer SERIALIZER = new XStreamSerializer();
    
    public Configurer getConfigurer() {
        Configurer result = DefaultConfigurer.defaultConfiguration();
    
//        result.configureCommandBus(c -> createDistributedCommandBus());
        
        
        result.configureEventStore(c -> defaultMongoEventStore());
        result.registerComponent(TokenStore.class, c -> defaultMongoTokenStore());

//        result.configureEventStore(c -> new EmbeddedEventStore(new InMemoryEventStorageEngine()));
//        result.registerComponent(TokenStore.class, c -> new InMemoryTokenStore());
        
        EventHandlingConfiguration ehConfiguration = new EventHandlingConfiguration()
            .byDefaultAssignTo(o -> {
                Class<?> handlerType = o.getClass();
                // generate unique based on handler & the entity it handles 
                // alternative - serial version uid, but that doesn't include method bodies - ObjectStreamClass.lookup(c).getSerialVersionUID()
                String handlerId = String.format("%s-%s", handlerType.getCanonicalName(), ClassHashUtils.getHash(handlerType));
                
                Optional<String> targetEntityId = Optional.ofNullable(handlerType.getAnnotation(ProcessingGroupTarget.class))
                    .map(ProcessingGroupTarget::value).map(c -> String.format("%s-%s", c.getCanonicalName(), ClassHashUtils.getHash(c)));
               
                return targetEntityId.map(entityId -> handlerId+"-"+entityId).orElse(handlerId);
                
            })
            .usingTrackingProcessors()
            .registerEventHandler(c -> new CounterEventHandler())
            .registerEventHandler(c -> new SecondHandler());
        
        result.registerModule(ehConfiguration);

        result.configureAggregate(Counter.class);
        
        return result;
        
    }
    
    @Produces
    @ApplicationScoped
    public Configuration getConfiguration() {
        return getConfigurer().start();
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
        MongoClient mongoClient = CDI.current().select(MongoClient.class).get();
        return new MongoTokenStore(new org.axonframework.mongo.eventsourcing.tokenstore.DefaultMongoTemplate(mongoClient, MongoConfiguration.DATABASE_NAME, "trackingTokens"), SERIALIZER);
    }
    
    
    public EventStore defaultMongoEventStore() {
        MongoClient mongoClient = CDI.current().select(MongoClient.class).get();
        
        MongoTemplate mongoTemplate = new DefaultMongoTemplate(mongoClient, MongoConfiguration.DATABASE_NAME, "domainevents",
                "snapshotevents");
        MongoEventStorageEngine storageEngine = new MongoEventStorageEngine(SERIALIZER, null, mongoTemplate, new DocumentPerEventStorageStrategy());
        
        
        return new EmbeddedEventStore(storageEngine);
    }       
    
    private CommandBus createDistributedCommandBus() {
        SimpleCommandBus localSegment = new SimpleCommandBus();
        
        try (JChannel channel = new JChannel()) {
            JGroupsConnector connector = new JGroupsConnector(localSegment, channel, "myCommandBus", SERIALIZER);
            
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
