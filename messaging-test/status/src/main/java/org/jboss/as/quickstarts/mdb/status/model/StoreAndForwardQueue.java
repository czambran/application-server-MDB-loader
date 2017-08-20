/**
 * 
 */
package org.jboss.as.quickstarts.mdb.status.model;

/**
 * @author bmaxwell
 *
 */
public class StoreAndForwardQueue {

	private Integer consumerCount = null;
	private Integer deliveringCount = null;
	private Long messageCount = null;
	private Long messagesAdded = null;
	private Long scheduledCount = null;
	private String name = null;
	private Boolean paused = null;

	
	/**
	 * 
	 */
	public StoreAndForwardQueue() {
	}


	public Integer getConsumerCount() {
		return consumerCount;
	}


	public void setConsumerCount(Integer consumerCount) {
		this.consumerCount = consumerCount;
	}


	public Integer getDeliveringCount() {
		return deliveringCount;
	}


	public void setDeliveringCount(Integer deliveringCount) {
		this.deliveringCount = deliveringCount;
	}


	public Long getMessageCount() {
		return messageCount;
	}


	public void setMessageCount(Long messageCount) {
		this.messageCount = messageCount;
	}


	public Long getMessagesAdded() {
		return messagesAdded;
	}


	public void setMessagesAdded(Long messagesAdded) {
		this.messagesAdded = messagesAdded;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public Boolean getPaused() {
		return paused;
	}


	public void setPaused(Boolean paused) {
		this.paused = paused;
	}
	
	public String toString() {
		return String.format("SaF Queue: %s consumers: %d messageCount: %d messagesAdded: %d deliveryCount: %d scheduledCount: %d paused: %s", name, consumerCount, messageCount, messagesAdded, deliveringCount, scheduledCount, paused);
	}


	public Long getScheduledCount() {
		return scheduledCount;
	}


	public void setScheduledCount(Long scheduledCount) {
		this.scheduledCount = scheduledCount;
	}
}
