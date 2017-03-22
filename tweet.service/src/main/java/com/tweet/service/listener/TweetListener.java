package com.tweet.service.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweet.service.providers.TweetServiceProvider;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

public class TweetListener implements StatusListener {

	private int storedTweetCount = 0;
	
	private ObjectMapper objectMapper;
	
	private TweetServiceProvider tweetServiceProvider;
	
	public TweetListener() {
		super();
		this.objectMapper = new ObjectMapper();
	}
	
	public TweetListener(TweetServiceProvider tweetServiceProvider) {
		this();
		this.setTweetServiceProvider(tweetServiceProvider);
	}
	
	public void onException(Exception exception) {
		System.out.println(exception.getMessage());
	}

	public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
		System.out.println(statusDeletionNotice.toString());
	}

	public void onScrubGeo(long arg0, long arg1) {
		System.out.println("X: " + arg0 + ", Y: " + arg1);
	}

	public void onStallWarning(StallWarning stallWarning) {
		System.out.println("Code: " + stallWarning.getCode() + ", Message: " + stallWarning.getMessage());
	}

	public void onStatus(Status status) {
		
		if(status != null) {
			
			try {
				
				String tweetAsJsonText = objectMapper.writeValueAsString(status);
				
				this.getTweetServiceProvider().saveTweet(tweetAsJsonText);
				
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
		
	}

	public void onTrackLimitationNotice(int arg0) {
		System.out.println("TrackLimitation: " + arg0);
	}

	public int getStoredTweetCount() {
		return storedTweetCount;
	}

	public void setStoredTweetCount(int storedTweetCount) {
		this.storedTweetCount = storedTweetCount;
	}

	public TweetServiceProvider getTweetServiceProvider() {
		return tweetServiceProvider;
	}

	public void setTweetServiceProvider(TweetServiceProvider tweetServiceProvider) {
		this.tweetServiceProvider = tweetServiceProvider;
	}

}
