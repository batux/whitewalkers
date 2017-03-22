package com.tweet.service.dao.implementation;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.tweet.service.dao.InsertionDao;
import com.tweet.service.dao.QueryDao;

public class TweetDao implements InsertionDao, QueryDao {

	private MongoCollection<Document> mongoCollection;
	
	public TweetDao(MongoCollection<Document> mongoCollection) {
		this.setMongoCollection(mongoCollection);
	}

	public FindIterable<Document> queryDocument(Bson filter) {
		
		FindIterable<Document> results = (filter == null) ? this.mongoCollection.find() : this.mongoCollection.find(filter);
		return results;
	}

	public void insertDocument(Document record) {
		this.getMongoCollection().insertOne(record);
	}

	public MongoCollection<Document> getMongoCollection() {
		return mongoCollection;
	}

	public void setMongoCollection(MongoCollection<Document> mongoCollection) {
		this.mongoCollection = mongoCollection;
	}

}
