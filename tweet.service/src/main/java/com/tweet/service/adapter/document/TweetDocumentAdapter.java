package com.tweet.service.adapter.document;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.tweet.service.model.GeoLocation;
import com.tweet.service.model.Tweet;

public class TweetDocumentAdapter {

	public Document convertTextToDocument(String rawTweetAsText) {
		
		Document tweetRecordAsDocument = Document.parse(rawTweetAsText);
		
		Document geoJsonField = null;
		
		Document geoLocation = (Document) tweetRecordAsDocument.get("geoLocation");
		
		if(geoLocation != null && geoLocation.get("longitude") != null && geoLocation.get("latitude") != null) {
			double longitude = (Double) geoLocation.get("longitude");
			double latitude = (Double) geoLocation.get("latitude");
			double[] location = { longitude, latitude };
			
			geoJsonField = new Document();
			geoJsonField.put("type", "Point");
			geoJsonField.put("coordinates", location);
		}
		
		tweetRecordAsDocument.put("locationAsGeoJSON", geoJsonField);
//		tweetRecordAsDocument.put("locationAsGeoJSON", location);
		
		return tweetRecordAsDocument;
	}
	
	public List<Tweet> convertDocumentsToTweetList(FindIterable<Document> results) {
		
		List<Tweet> tweets = new ArrayList<Tweet>();
		
		MongoCursor<Document> resultsIterator = results.iterator();
		
		while(resultsIterator.hasNext()) {
			
			Document resultItem = resultsIterator.next();
			
			Tweet tweet = new Tweet();
			
			String text = (String) resultItem.get("text");
			tweet.setText(text);
			
			long creationDateTime = (Long) resultItem.get("createdAt");
			tweet.setCreationDateTime(creationDateTime);
			
			Document user = (Document) resultItem.get("user");
			
			String userFullName = (String) user.get("name");
			tweet.setUserFullName(userFullName);
			
			Document geoLocation = (Document) resultItem.get("geoLocation");
			Double latitude = (Double) geoLocation.get("latitude");
			Double longitude = (Double) geoLocation.get("longitude");
			
			GeoLocation geoLocationItem = new GeoLocation();
			geoLocationItem.setLatitude(latitude);
			geoLocationItem.setLongitude(longitude);
			tweet.setGeoLocation(geoLocationItem);
			
			tweets.add(tweet);
		}
		
		return tweets;
	}
	
}
