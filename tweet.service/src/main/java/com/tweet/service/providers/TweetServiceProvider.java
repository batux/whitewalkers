package com.tweet.service.providers;

import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import com.tweet.service.adapter.document.TweetDocumentAdapter;
import com.tweet.service.adapter.filter.TweetFilterAdapter;
import com.tweet.service.dao.implementation.TweetDao;
import com.tweet.service.model.SpatialQuery;
import com.tweet.service.model.Tweet;
import com.tweet.service.mongodb.MongoDbClientManager;

public class TweetServiceProvider {

	private String databaseName = "twitter";
	private String collectionName = "tweets";
	
	private TweetDao tweetDao;
	private TweetFilterAdapter tweetFilterAdapter;
	private TweetDocumentAdapter tweetDocumentAdapter;
	private MongoDbClientManager mongoDbClientManager;
	
	
	public TweetServiceProvider() {
		
		this.tweetFilterAdapter = new TweetFilterAdapter();
		this.tweetDocumentAdapter = new TweetDocumentAdapter();
		
		this.mongoDbClientManager = new MongoDbClientManager()
				.connectToDatabase(this.databaseName)
				.connectToCollection(this.collectionName);

		this.tweetDao = new TweetDao(this.mongoDbClientManager.build());
	}
	
	public void saveTweet(String tweetAsJsonText) {
		
		Document tweetAsDocument = this.tweetDocumentAdapter.convertTextToDocument(tweetAsJsonText);
		
		this.tweetDao.insertDocument(tweetAsDocument);
	}
	
	public List<Tweet> getTweets(SpatialQuery spatialQuery) {
		
		Bson queryFilter = this.tweetFilterAdapter.convertSpatialQueryToBson(spatialQuery);
		
		FindIterable<Document> results = this.tweetDao.queryDocument(queryFilter);
		
		List<Tweet> tweets = this.tweetDocumentAdapter.convertDocumentsToTweetList(results);
		
		return tweets;
	}
	
	public List<Tweet> getTweets(String tweetType, int tweetMaximumLimit) {
		
		Bson queryFilter = "geo".equals(tweetType) ? Filters.ne("geoLocation", null) : null;
		
		FindIterable<Document> results = this.tweetDao.queryDocument(queryFilter);
		
		List<Tweet> tweets = this.tweetDocumentAdapter.convertDocumentsToTweetList(results);
		
		return tweets;
	}

}
