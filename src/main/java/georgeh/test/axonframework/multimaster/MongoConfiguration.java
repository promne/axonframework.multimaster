package georgeh.test.axonframework.multimaster;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.ServerAddress;

import georgeh.test.axonframework.multimaster.mongo.CustomMongoMappingContext;

public class MongoConfiguration {

	public static final String DATABASE_NAME = "axonmutlimaster";

	@Inject
	private Logger logger;

	@Produces
	@Singleton
	public MongoClient createMongoClient() throws UnknownHostException {
		String mongoIps = System.getProperty("axon.mongo.ip", "localhost");
		logger.info("Creating connection to mongodb: {}", mongoIps);
		List<ServerAddress> serverAddress = new ArrayList<>();
		for (String addr : mongoIps.split(",")) {
			serverAddress.add(new ServerAddress(addr));
		}
		Builder clientOptions = new MongoClientOptions.Builder().socketTimeout(5000);

		return new MongoClient(serverAddress, clientOptions.build());
	}

	@Produces
	@Singleton
	public MongoTemplate createSpringMongoTemplate(final MongoClient mongoClient) {
		MongoDbFactory mongoFactory = new SimpleMongoDbFactory(mongoClient, DATABASE_NAME);
		return new MongoTemplate(mongoFactory, getDefaultMongoConverter(mongoFactory));
	}

	/*
	 * Inspired by MongoTemplate#getDefaultMongoConverter The mapping context is
	 * replaced by custom one.
	 */
	private static final MongoConverter getDefaultMongoConverter(final MongoDbFactory factory) {
		DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
		CustomConversions conversions = new CustomConversions(Collections.emptyList());

		MongoMappingContext mappingContext = new CustomMongoMappingContext();
		mappingContext.setSimpleTypeHolder(conversions.getSimpleTypeHolder());
		mappingContext.afterPropertiesSet();

		MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mappingContext);
		converter.setCustomConversions(conversions);
		converter.afterPropertiesSet();

		return converter;
	}

}
