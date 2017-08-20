/**
 * 
 */
package org.jboss.as.quickstarts.mdb.status.model;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author bmaxwell
 *
 */
public class ServerStatus {

	private Map<String, QueueStatus> queues = new TreeMap<String, QueueStatus>();
	private String name;
	
	/**
	 * 
	 */
	public ServerStatus() {
	}
	
	public ServerStatus(String serverName) {
		this.name = serverName;
	}

	public QueueStatus addQueueStatus(String name, Integer consumerCount, Integer messageCount) {
		return addQueueStatus(name, consumerCount, messageCount, false);
	}
	
	public QueueStatus addQueueStatus(String name, Integer consumerCount, Integer messageCount, boolean sum) {
		QueueStatus status = queues.get(name);
		if(status == null) {
			status = new QueueStatus(name);
			queues.put(name, status);
		}
		if(sum) {
			consumerCount += status.getConsumerCount();
			messageCount += status.getMessageCount();
		}
		status.setConsumerCount(consumerCount);
		status.setMessageCount(messageCount);
		return status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, QueueStatus> getQueues() {
		return queues;
	}

	public void setQueues(Map<String, QueueStatus> queues) {
		this.queues = queues;
	}
	
	public int getTotalConsumerCount() {
		int total = 0;
		for(QueueStatus queue : queues.values())
			total += queue.getConsumerCount();
		return total;
	}

	public int getTotalMessageCount() {
		int total = 0;
		for(QueueStatus queue : queues.values())
			total += queue.getMessageCount();
		return total;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder(String.format("Server: %s\n", getName()));
		for(QueueStatus status : queues.values())
			sb.append(String.format("%s\n", status));
		return sb.toString();
	}	
}