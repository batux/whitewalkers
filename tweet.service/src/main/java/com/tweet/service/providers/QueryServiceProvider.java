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
import com.tweet.service.mongodb.config.MongoDbConfiguration;

public class QueryServiceProvider {

	private QueryDao queryDao;
	private QueryDocumentAdapter queryDocumentAdapter;
	private MongoDbClientManager mongoDbClientManager;
	
	public QueryServiceProvider() {
		
		this.queryDocumentAdapter = new QueryDocumentAdapter();
		
		this.mongoDbClientManager = new MongoDbClientManager(MongoDbConfiguration.MONGODB_HOST_NAME,MongoDbConfiguration.MONGODB_HOST_PORT_NO)
				.connectToDatabase(MongoDbConfiguration.QUERY_DATABASE_NAME)
				.connectToCollection(MongoDbConfiguration.QUERY_COLLECTION_NAME);
		
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
