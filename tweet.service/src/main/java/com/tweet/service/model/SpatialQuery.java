package com.tweet.service.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SpatialQuery {

	private String type;
	private String searchKeyword;
	private List<Double[]> coordinates;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<Double[]> getCoordinates() {
		return coordinates;
	}
	public void setCoordinates(List<Double[]> coordinates) {
		this.coordinates = coordinates;
	}
	public String getSearchKeyword() {
		return searchKeyword;
	}
	public void setSearchKeyword(String searchKeyword) {
		this.searchKeyword = searchKeyword;
	}
	
}
