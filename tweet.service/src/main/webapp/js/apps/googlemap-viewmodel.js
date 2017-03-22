define(['jquery',
		'knockout',
		'components/eventbus/ui-event-bus',
		'bootstrap',
		'async!https://maps.googleapis.com/maps/api/js?libraries=geometry,drawing&key=AIzaSyB7eVj45GZPJ5Xua31hm-N1ncS3vkK7GOo'
		], 
function($,ko,eventBus){
	
	var GooleMapViewModel = function() {
		
		var self = this;
		
		self.markers = [];
		self.infowindows = [];
		self.geoLocations = [];
		
		var maxZoomLevel = 7;
		var minZoomLevel = 2;
		
		var mapOptions = {
			zoom : 8,
			center : new google.maps.LatLng(40.9761726, 28.8142807),
			disableDefaultUI : true
		};
		
		self.geometry = null;
		self.eventBus = eventBus;
		self.map = new google.maps.Map(document.getElementById('map'), mapOptions);
		
		self.queryName = ko.observable("");
		self.selectedQuery = ko.observable("");
		self.searchedKeyword = ko.observable("");
		self.textBasedDisability = ko.observable(false);
		self.querySelectBoxItems = ko.observableArray([]);
		
		self.openSaveQueryModal = function() {
			$('#querySaveModal').modal('show');
		}
		
		self.openLoadQueryModal = function() {
			
			$.ajax({ 
				   type: "GET",
				   dataType:"json",
				   url: "http://localhost:8080/tweet.service/rest/tweetservice/queryselectboxitems",
				   success: function(response){        
					   self.querySelectBoxItems(response);
					   $('#queryLoadModal').modal('show');
				   }
				});
		}
		
		self.loadQuery = function() {
			
			console.log(self.selectedQuery());
			
			$.ajax({ 
				   type: "GET",
				   dataType:"json",
				   url: "http://localhost:8080/tweet.service/rest/tweetservice/queries/" + self.selectedQuery(),
				   success: function(response){        
					   console.log(response);
					   
					   self.clearDataLayerOfMap();
					   
					   self.searchedKeyword(response.searchKeyword);
					   
					   for(var i=0; i < self.markers.length; i++) {
							  var marker = self.markers[i];
							  marker.setMap(null);
					   }
					   
					   self.markers = [];
					   
					   if(response.queryResults) {
						   for(var i=0; i < response.queryResults.length; i++) {
							   
							   var locationAsGeoJson = response.queryResults[i];
							   
							   var location = { lat: locationAsGeoJson.coordinates[1], lng: locationAsGeoJson.coordinates[0]};
							   
							   self.markers[i] = new google.maps.Marker({
							          position: location,
							          map: self.map,
							          title: 'Hello Google Map!'
						       });
								
							   self.markers[i].index = i;
							   
							   if(i == 0) {
								   self.map.panTo(self.markers[i].getPosition());
							   }
						   }
					   }
					   
					   var spatialQueryGeometry = response.spatialQueryGeometry;
					   
					   if(spatialQueryGeometry) {
						   
						   var tmp = {
								    "type": "FeatureCollection",
								    "features": [
								    	{
								            "type": "Feature",
									        "geometry": {
									          "type": spatialQueryGeometry.type,
									          "coordinates": spatialQueryGeometry.coordinates
									        }
								    	}
								    ]
						   };
							   
						   self.map.data.addGeoJson(tmp);
					   }
					   
					   $('#queryLoadModal').modal('hide');
				   }
				});
			
		}
		
		self.saveQuery = function() {
			
			if(self.geometry || self.searchedKeyword()) {
				
				if(!self.geometry) {
					self.geometry = {
							type: "",
							coordinates: [],
							searchKeyword: 	self.searchedKeyword()
					}
				}
				else {
					self.geometry.searchKeyword = self.searchedKeyword();
				}
				
				var data = {
						'query': self.geometry,
						'name': self.queryName(),
						'results': self.geoLocations
				}
				
				$.ajax({
					   type: "POST",
					   dataType:"json",
					   url: "http://localhost:8080/tweet.service/rest/tweetservice/queries",
					   contentType: "application/json; charset=utf-8",
					   data: JSON.stringify(data),
					   beforeSend : function(xhr) {
							xhr.setRequestHeader("Accept-Language", "en");
							xhr.setRequestHeader("country", "us");
							xhr.setRequestHeader('page', window.location.href);
					   },
					   success: function(response){
						   self.queryName("");
						   $('#querySaveModal').modal('hide');
						   $('#successMessageModal').modal('show');
					   }
				  });
			}
			else {
				alert("Search parameters are empty!");
			}
			
		}
		
		self.search = function() {

			if(!self.textBasedDisability()) {
				
				if(!self.geometry) {
					self.geometry = {
							type: "",
							coordinates: [],
							searchKeyword: 	self.searchedKeyword()
					}
				}
				else {
					self.geometry.searchKeyword = self.searchedKeyword();
				}
				
				self.callSpatialQuery(self.geometry);
			}
			else if(self.textBasedDisability() && !self.geometry) {
				alert("Please select area over map!");
			}
			
		}
		
		self.rectangleOptions = {
				editable: false,
                draggable: false,
                geodesic: false
		}
		
		self.polygonOptions = {
				editable: false,
                draggable: false,
                geodesic: false
		}
		
		self.drawingManagerConfiguration = {
					drawingMode: google.maps.drawing.OverlayType.POLYGON,
					drawingControl: true,
					drawingControlOptions: {
						position: google.maps.ControlPosition.TOP_CENTER,
						drawingModes: ['polygon', 'rectangle']
				    },
				    rectangleOptions: self.rectangleOptions,
                    polygonOptions: self.polygonOptions,
		}
		
		self.drawingManagerConfiguration.geometryBag = [];
		
		self.drawingManager = new google.maps.drawing.DrawingManager(self.drawingManagerConfiguration);
		
		
		self.clearGeometriesOnMap = function(geometryBag) {
			for(var i=0; i < geometryBag.length; i++) {
				  geometryBag[i].setMap(null);
			}
		}
		
		self.createRectangle = function(geometryFeature) {
			
			var b = geometryFeature.getBounds(),
	          p = [b.getSouthWest(), {
	              lat: b.getSouthWest().lat(),
	              lng: b.getNorthEast().lng()
	            },
	            b.getNorthEast(), {
	              lng: b.getSouthWest().lng(),
	              lat: b.getNorthEast().lat()
	            }
	          ]
			  
			  var rectangle = new google.maps.Data.Polygon([p]);
			  return rectangle;
		}
		
		self.createPolygon = function(geometryFeature) {
			
			var polygon = new google.maps.Data.Polygon([geometryFeature.getPath().getArray()]);
			return polygon;
		}
		
		self.createCircle = function(geometryFeature) {
			
			var circle = {
		          properties: {
		            radius: geometryFeature.getRadius()
		          },
		          geometry: new google.maps.Data.Point(geometryFeature.getCenter())
	        }
			
			return circle;
		}
		
		
		self.callSpatialQuery = function(geometry) {
			
			$.ajax({ 
				   type: "POST",
				   dataType:"json",
				   url: "http://localhost:8080/tweet.service/rest/tweetservice/tweets",
				   contentType: "application/json; charset=utf-8",
				   data: JSON.stringify(geometry),
				   beforeSend : function(xhr) {
						xhr.setRequestHeader("Accept-Language", "en");
						xhr.setRequestHeader("country", "us");
						xhr.setRequestHeader('page', window.location.href);
				   },
				   success: function(response){
					   self.createTweets(response);
				   }
			  });
		}
		
		self.makeSpatialQuery = function(geometry) {
			
			if(self.textBasedDisability()) {
				geometry.searchKeyword = "";
			}
			else {
				geometry.searchKeyword = self.searchedKeyword();
			}
			
			self.geometry = geometry;
			self.callSpatialQuery(self.geometry);
		}
		
		
		self.clearDataLayerOfMap = function() {
			
			  var geometryBag = self.drawingManagerConfiguration.geometryBag;
			  self.clearGeometriesOnMap(geometryBag);
			  
			  self.map.data.forEach(function(feature) {
				  self.map.data.remove(feature);
			  });
			  
			  for(var i=0; i < self.markers.length; i++) {
				  var marker = self.markers[i];
				  marker.setMap(null);
			  }
			
			  self.geometry = null;
		}
		
		google.maps.event.addListener(self.drawingManager, 'overlaycomplete',
				
				function(event) {
			
				  self.clearDataLayerOfMap();
				  
				  if(event.type == google.maps.drawing.OverlayType.RECTANGLE) {
					  
					  var geometryFeature = event.overlay;
					  
					  var rectangle = self.createRectangle(geometryFeature);
					  var feauture = new google.maps.Data.Feature({
				          geometry: rectangle
				      });
					  
					  self.map.data.add(feauture);
					  
					  self.map.data.toGeoJson(function(rectangle) {
						  
						  var geometry = rectangle.features[0].geometry;
						  geometry.coordinates = geometry.coordinates[0]
						  
						  self.makeSpatialQuery(geometry);
					  });
					  
					  geometryFeature.addListener('bounds_changed', 
							  function(event) {
								  self.map.data.toGeoJson(function(rectangle) {
									  
									  var geometry = rectangle.features[0].geometry;
									  geometry.coordinates = geometry.coordinates[0]
									  
									  self.makeSpatialQuery(geometry);
								  });
					  });
					  
				  }
				  else if(event.type == google.maps.drawing.OverlayType.POLYGON) {
					  
					  var geometryFeature = event.overlay;
					  
					  var polygon = self.createPolygon(geometryFeature);
					  
					  var feauture = new google.maps.Data.Feature({
				          geometry: polygon
				      });
					  
					  self.map.data.add(feauture);
					  
					  self.map.data.toGeoJson(function(polygon) {
						  
						  var geometry = polygon.features[0].geometry;
						  geometry.coordinates = geometry.coordinates[0]
						  
						  self.makeSpatialQuery(geometry);
					  });
					  
					  geometryFeature.addListener('insert_at', 
							  function(event) {
						        var geometry = polygon.features[0].geometry;
						  		self.makeSpatialQuery(geometry);
					  });
					  
					  geometryFeature.addListener('remove_at', 
							  function(event) {
						        var geometry = polygon.features[0].geometry;
						  		self.makeSpatialQuery(geometry);
					  });
					  
				  }

				  self.drawingManagerConfiguration.geometryBag.push(event.overlay);
				  self.drawingManager.setDrawingMode(null);
		});
		
		
		
		self.drawingManager.setMap(self.map);
		
		
		self.createTweets = function(tweets) {
			
			self.geoLocations = [];
			
			for(var i=0; i < self.markers.length; i++) {
				  var marker = self.markers[i];
				  marker.setMap(null);
			}
			
			for(var i=0; i < tweets.length; i++) {
				
				var tweet = tweets[i];
				
				var contentText = "<div><h3>" + tweet.userFullName + "</h3>";
				contentText += "<p>Tweet time: " + new Date(tweet.creationDateTime).toString() + "</p>";
				contentText += "<p>" + tweet.text + "</p>";
				contentText += "</div>"
				
				self.infowindows[i] = new google.maps.InfoWindow({
					content: contentText
		        });
				
				var geoLocation = { lat: tweet.geoLocation.latitude, lng: tweet.geoLocation.longitude};
				
				self.markers[i] = new google.maps.Marker({
			          position: geoLocation,
			          map: self.map,
			          title: 'Hello Google Map!'
		        });
				self.markers[i].index = i;
				
				google.maps.event.addListener(self.markers[i], 'click', function() {
					
					self.infowindows[this.index].open(self.map,self.markers[this.index]);
			        self.map.panTo(self.markers[this.index].getPosition());
			        
			    });
				
				if(i == 0) {
					self.map.panTo(self.markers[i].getPosition());
				}
				
				self.geoLocations.push(tweet.geoLocation);
			}
			
		}
	}
	
	ko.applyBindings(new GooleMapViewModel());
	
});