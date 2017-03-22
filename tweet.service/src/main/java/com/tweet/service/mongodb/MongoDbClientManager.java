package com.tweet.service.mongodb;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDbClientManager {

	private final String hostName = "localhost";
	private final int portNo = 27017;
	
	private MongoClient mongoClient;
	private MongoDatabase selectedMongoDatabase;
	private MongoCollection<Document> selectedMongoCollection;
	
	public MongoDbClientManager() {
		
		if(this.mongoClient == null) {
			this.mongoClient = new MongoClient(hostName, portNo);
		}
	}
	
	public MongoDbClientManager connectToDatabase(String databaseName) {

		this.selectedMongoDatabase = this.getMongoClient().getDatabase(databaseName);
		return this;
	}
	
	public MongoDbClientManager connectToCollection(String collectionName) {
		
		this.selectedMongoCollection = this.getSelectedMongoDatabase().getCollection(collectionName);
		return this;
	}
	
	public MongoCollection<Document> build() {
		return this.selectedMongoCollection;
	}
	
	public MongoDatabase getSelectedMongoDatabase() {
		return this.selectedMongoDatabase;
	}
	
	public MongoClient getMongoClient() {
		return mongoClient;
	}
}
