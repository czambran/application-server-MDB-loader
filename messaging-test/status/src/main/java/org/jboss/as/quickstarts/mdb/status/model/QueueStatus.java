/**
 * 
 */
package org.jboss.as.quickstarts.mdb.status.model;

/**
 * @author bmaxwell
 *
 */
public class QueueStatus {

	private String name;
	private Integer messageCount = 0;
	private Long messagesAdded = 0L;
	private Integer consumerCount = 0;
	
	/**
	 * 
	 */
	public QueueStatus() {
	}
	
	public QueueStatus(String name) {
		this.name = name;	
	}
	
	public QueueStatus(String name, Integer consumerCount, Integer messageCount) {
		this.name = name;
		this.consumerCount = consumerCount;
		this.messageCount = messageCount;
	}

	public Integer getConsumerCount() {
		return consumerCount;
	}

	public void setConsumerCount(Integer consumerCount) {
		this.consumerCount = consumerCount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getMessageCount() {
		return messageCount;
	}

	public void setMessageCount(Integer messageCount) {
		this.messageCount = messageCount;
	}

	public String toString() {
		return String.format("Queue: %s consumerCount: %d messageCount: %d messagesAdded: %d", getName(), getConsumerCount(), getMessageCount(), getMessagesAdded());
	}

	public Long getMessagesAdded() {
		return messagesAdded;
	}

	public void setMessagesAdded(Long messagesAdded) {
		this.messagesAdded = messagesAdded;
	}
}