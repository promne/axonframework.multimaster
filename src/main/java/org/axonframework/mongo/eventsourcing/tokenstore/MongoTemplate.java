package org.axonframework.mongo.eventsourcing.tokenstore;

import org.bson.Document;

import com.mongodb.client.MongoCollection;

public interface MongoTemplate {

    MongoCollection<Document> trackingTokensCollection();
    
}
