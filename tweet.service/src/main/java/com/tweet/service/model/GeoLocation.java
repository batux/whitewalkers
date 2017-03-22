package com.tweet.service.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GeoLocation {

	private double latitude;
	private double longitude;
	
	public GeoLocation() {
		this.setLatitude(-1);
		this.setLongitude(-1);
	}
	
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
}
