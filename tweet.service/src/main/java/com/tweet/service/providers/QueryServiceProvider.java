package com.tweet.service.providers;

import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import com.tweet.service.adapter.document.QueryDocumentAdapter;
import com.tweet.service.dao.implementation.QueryDao;
import com.tweet.service.model.QueryHolder;
import com.tweet.service.model.QuerySelectBoxItem;
import com.tweet.service.mongodb.MongoDbClientManager;

public class QueryServiceProvider {

	private String databaseName = "twitter";
	private String collectionName = "queries";
	
	private QueryDao queryDao;
	private QueryDocumentAdapter queryDocumentAdapter;
	private MongoDbClientManager mongoDbClientManager;
	
	public QueryServiceProvider() {
		
		this.queryDocumentAdapter = new QueryDocumentAdapter();
		
		this.mongoDbClientManager = new MongoDbClientManager()
				.connectToDatabase(this.databaseName)
				.connectToCollection(this.collectionName);
		
		this.queryDao = new QueryDao(this.mongoDbClientManager.build());
	}
	
	public void saveQuery(Map<String, Object> queryParameters) {
		
		Document record = this.queryDocumentAdapter.convertQueryToDocument(queryParameters);
		
		this.queryDao.insertDocument(record);
	}
	
	public QueryHolder getQueryById(String queryIdAshexString) {
		
		FindIterable<Document> results = this.queryDao.queryDocument(Filters.eq("_id", new ObjectId(queryIdAshexString)));
		
		QueryHolder queryHolder = this.queryDocumentAdapter.convertDocumentToQueryHolder(results);
		
		return queryHolder;
	}
	
	public List<QuerySelectBoxItem> getQuerySelectboxItems() {
		
		FindIterable<Document> results = this.queryDao.queryDocument(null).sort(new Document("_id", -1)).limit(10);
		
		List<QuerySelectBoxItem> selectboxItemList = this.queryDocumentAdapter.convertDocumentsToSelectboxItemList(results);
		
		return selectboxItemList;
	}
	
}
