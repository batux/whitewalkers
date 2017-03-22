package com.tweet.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.spi.resource.Singleton;
import com.tweet.service.model.QueryHolder;
import com.tweet.service.model.QuerySelectBoxItem;
import com.tweet.service.model.SpatialQuery;
import com.tweet.service.model.Tweet;
import com.tweet.service.providers.QueryServiceProvider;
import com.tweet.service.providers.TweetServiceProvider;
import com.tweet.service.providers.TweetStreamProvider;

@Path("/tweetservice")
@Singleton
public class TweetService {

	private TweetStreamProvider tweetStreamProvider;
	private TweetServiceProvider tweetServiceProvider;
	private QueryServiceProvider queryServiceProvider;
	
	public TweetService() {
		this.tweetServiceProvider = new TweetServiceProvider();
		this.tweetStreamProvider = new TweetStreamProvider();
		this.queryServiceProvider = new QueryServiceProvider();
	}
	
	@POST
	@Path("/tweetstream/{action}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response manageTweetStream(@PathParam("action") String streamAction) {
		
		String operationResultMessage = "Unsupported operation type!";
		
		if("open".equals(streamAction)) {
			
			Thread tweetStreamThread = new Thread(new Runnable() {
				
				public void run() {
					tweetStreamProvider.startToListenTweetStream();
				}
			});
			
			tweetStreamThread.start();
			
			operationResultMessage = "Tweet Stream was started!";
		}
		else if("close".equals(streamAction)) {
			this.tweetStreamProvider.stopToListenTweetStream();
			operationResultMessage = "Tweet Stream was stoped!";
		}
		
		return Response.status(200).type(MediaType.APPLICATION_JSON).entity(operationResultMessage).build();
	}
	
	@GET
	@Path("/tweets/{type}/{maximum}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON } )
	public Response getTweetsWhichContainsGeoLocation(@PathParam("type") String tweetType, @PathParam("maximum") int tweetMaximumLimit) {
		
		List<Tweet> tweets = this.tweetServiceProvider.getTweets(tweetType, tweetMaximumLimit);
		
		GenericEntity<List<Tweet>> genericTweetList = new GenericEntity<List<Tweet>>(tweets) {};
		
		return Response.status(200).type(MediaType.APPLICATION_JSON).entity(genericTweetList).build();
	}
	
	
	@POST
	@Path("/tweets")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON } )
	@Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON } )
	public Response getTweetsWithinSpatialQuery(SpatialQuery spatialQuery) {
		
		List<Tweet> tweets = this.tweetServiceProvider.getTweets(spatialQuery);
		
		GenericEntity<List<Tweet>> genericTweetList = new GenericEntity<List<Tweet>>(tweets) {};
		
		return Response.status(200).type(MediaType.APPLICATION_JSON).entity(genericTweetList).build();
	}
	
	
	@POST
	@Path("/queries")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON } )
	@Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON } )
	public Response saveUserQuery(Map<String, Object> queryParameters) {
		
		this.queryServiceProvider.saveQuery(queryParameters);
		
		GenericEntity<List<Tweet>> genericTweetList = new GenericEntity<List<Tweet>>(new ArrayList<Tweet>()) {};
		
		return Response.status(200).type(MediaType.APPLICATION_JSON).entity(genericTweetList).build();
	}
	
	@GET
	@Path("/queries/{queryid}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON } )
	@Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON } )
	public Response getUserQuery(@PathParam("queryid") String queryid) {
		
		QueryHolder queryHolder = this.queryServiceProvider.getQueryById(queryid);
		
		GenericEntity<QueryHolder> queryHolderEntity = new GenericEntity<QueryHolder>(queryHolder) {};
		
		return Response.status(200).type(MediaType.APPLICATION_JSON).entity(queryHolderEntity).build();
	}
	
	@GET
	@Path("/queryselectboxitems")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON } )
	public Response getQuerySelectBoxItemList() {
		
		List<QuerySelectBoxItem> selectBoxItemList = this.queryServiceProvider.getQuerySelectboxItems();
		
		GenericEntity<List<QuerySelectBoxItem>> querySelectBoxItems = new GenericEntity<List<QuerySelectBoxItem>>(selectBoxItemList) {};
		
		return Response.status(200).type(MediaType.APPLICATION_JSON).entity(querySelectBoxItems).build();
	}
}
