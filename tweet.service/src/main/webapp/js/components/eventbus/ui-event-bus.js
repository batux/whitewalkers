define(['knockout', 'underscore'], function(ko, underscore){
	
	
	function EventBus() {
		
		var self = this;
		
		self.subscribers = [];
		self.notifierMessageItems = [];
		
		self.communicationChannel = new ko.subscribable();
		
		self.addSubscriber = function(subscriberPackage, instanceReference, topicName) {
			
			var subscriber = self.communicationChannel.subscribe(subscriberPackage, instanceReference, topicName);
			
			subscriber.topicName = topicName;
			subscriber.owner = instanceReference;
			
			self.subscribers.push(subscriber);
			
			var waitedNotifierMessages = self.getWaitedNotifierMessages(topicName);
			
			if(waitedNotifierMessages.length > 0) {
				
				for(var i=0; i < waitedNotifierMessages.length; i++) {
					var waitedNotifierMessage = waitedNotifierMessages[i];
					self.notifySubscribers(waitedNotifierMessage[topicName], waitedNotifierMessage.topicName);
				}
			}
		}
		
		self.removeSubscriber = function(subscriberOwner) {
			
			for(var i=0; i < self.subscribers.length; i++) {
				
				var subscriber = self.subscribers[i];
				
				if(subscriber.owner == subscriberOwner) {
					
					subscriber.dispose();
					underscore.without(self.subscribers, subscriber);
					break;
				}
				
			}
			
		}
		
		self.containsSubscriberByTopicName = function(topicName) {
			
			var hasSubscriber = false;
			
			for(var i=0; i < self.subscribers.length; i++) {
				var subscriber = self.subscribers[i];
				if(subscriber.topicName == topicName) {
					hasSubscriber = true;
					break;
				}
			}
			
			return hasSubscriber;
		}
		
		self.notifySubscribers = function(notifierPackage, topicName) {
			
			var hasSubscriber = self.containsSubscriberByTopicName(topicName);
			
			if(hasSubscriber) {
				self.communicationChannel.notifySubscribers(notifierPackage, topicName);
			}
			else {
				self.insertMessageInQueueToNotify(notifierPackage, topicName);
			}
		}
		
		self.insertMessageInQueueToNotify = function(message, topicName) {
			
			var notifierMessageItem = {};
			notifierMessageItem[topicName] = message;
			notifierMessageItem['topicName'] = topicName;
			
			self.notifierMessageItems.push(notifierMessageItem);
		}
		
		self.getWaitedNotifierMessages = function(topicName) {
			
			var waitedNotifierMessages = [];
			
			for(var i=0; i < self.notifierMessageItems.length; i++) {
				
				var notifierMessageItem = self.notifierMessageItems[i];
				
				if(notifierMessageItem.topicName == topicName) {
					waitedNotifierMessages.push(notifierMessageItem);
				}
			}
			
			return waitedNotifierMessages;
		}
	}
	
	var eventBus = new EventBus();
	
	return eventBus;
	
});





