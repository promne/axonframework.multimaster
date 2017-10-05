package georgeh.test.axonframework.multimaster;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

public class MongoConfiguration {

    public static final String DATABASE_NAME = "axonmutlimaster";
    
    @Inject
    private Logger logger;
    
    @Produces
    @Singleton
    public MongoClient createMongoClient() throws UnknownHostException {
        String mongoIps = System.getProperty("axon.mongo.ip");
        logger.info("Creating connection to mongodb: {}", mongoIps);
        List<ServerAddress> serverAddress = new ArrayList<>();
        for (String addr : mongoIps.split(",")) {
            serverAddress.add(new ServerAddress(addr));
        }
        return new MongoClient(serverAddress);
    }
    
    @Produces
    @Singleton
    public MongoTemplate createSpringMongoTemplate(MongoClient mongoClient) {
        MongoDbFactory mongoFactory = new SimpleMongoDbFactory(mongoClient, DATABASE_NAME);
        return new MongoTemplate(mongoFactory);
    }

}
