package com.tweet.service.adapter.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mongodb.client.model.geojson.Geometry;
import com.tweet.service.util.GeometryFactory;

public class TweetLocationAdapter {

	public List<Geometry> convertLocationsToGeoJsonFormat(List<Map<String, Double>> locations) {
		
		List<Geometry> locationsInGeoJsonFormat = new ArrayList<Geometry>();
		
		for(Map<String, Double> location : locations) {
			
			Geometry point = GeometryFactory.createPoint(location);
			locationsInGeoJsonFormat.add(point);
		}
		
		return locationsInGeoJsonFormat;
	}
	
}
