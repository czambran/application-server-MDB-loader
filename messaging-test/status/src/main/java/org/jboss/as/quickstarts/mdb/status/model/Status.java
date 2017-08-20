/**
 * 
 */
package org.jboss.as.quickstarts.mdb.status.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jboss.as.quickstarts.mdb.status.config.HostServerInstance;
import org.jboss.as.quickstarts.mdb.status.config.StatusConfig;

/**
 * @author bmaxwell
 *
 */
public class Status {
	
	private Map<String, ServerStatus> servers = new TreeMap<String,ServerStatus>();
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
	private List<String> diskStatus = new ArrayList<String>();
	private List<String> info = new ArrayList<String>();
	private Map<HostServerInstance, List<StoreAndForwardQueue>> storeAndForwardQueues = new HashMap<>();
	private StatusConfig config = null;
	/**
	 * 
	 */
	public Status(StatusConfig config) {
		this.config = config;
	}

	public ServerStatus addServerStatus(String serverName) {
		ServerStatus status = servers.get(serverName);
		if(status == null) {
			status = new ServerStatus(serverName);
			servers.put(serverName, status);
		}
		return status;
	}
	
	public Collection<ServerStatus> getServers() {
		return servers.values();
	}
	
	public Map<String,ServerStatus> getServersMap() {
		return servers;
	}
	
	private void addEvents(StringBuilder sb, ServerStatus totals) {
		// if consumers drops by x or messages grows by y then add an event occured
		// if hornetq3 is down add an event that it is shutdown
	}
	
	public void addInfo(String info) {
		this.info.add(info);
	}
	
	public int getTotalConsumerCount() {
		int total = 0;
		for(ServerStatus server : servers.values())
			total += server.getTotalConsumerCount();
		return total;
	}

	public int getTotalMessageCount() {
		int total = 0;
		for(ServerStatus server : servers.values())
			total += server.getTotalMessageCount();
		return total;
	}

	public void checkForAnomalies(Status previous) {
		if(previous == null)
			return;
		// check for loss of server
		if(previous.getServers().size() > this.getServers().size())
			addInfo(String.format("Event: Less servers than last check, there were %d now %d", previous.getServers().size(),this.getServers().size() ));
		// check for loss of consumers
		if(previous.getTotalConsumerCount() > getTotalConsumerCount())
			addInfo(String.format("Event: Consumers dropped from %d to %d", previous.getTotalConsumerCount(), getTotalConsumerCount()));	
		// check for message count increase
		if(config != null) {
			if(config.getAlerts().isMessageIncreased(previous.getTotalConsumerCount(), getTotalConsumerCount())) {
				addInfo(String.format("Event: Messaged increased %d%% from %d to %d", config.getAlerts().getMessageIncreasePercentage(), previous.getTotalConsumerCount(), getTotalConsumerCount()));
			}
		}
	}
	
	private ServerStatus sumTotals() {
		ServerStatus totals = new ServerStatus("Totals");
		for(Map.Entry<String, ServerStatus> entry : servers.entrySet()) {			
			// Sum the Queue total consumers and messages						
			for(QueueStatus queue : entry.getValue().getQueues().values())
				totals.addQueueStatus(queue.getName(), queue.getConsumerCount(), queue.getMessageCount(), true);			
		}
		return totals;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("\n------------------------------\n"));
		sb.append(String.format("Date: %s\n", DATE_FORMAT.format(new Date())));
		if(!info.isEmpty()) {
			sb.append(String.format("Info:\n"));
			for(String i : info)
				sb.append(String.format("%s\n", i));
			sb.append(String.format("\n"));
		}
		// add Server Status
		for(Map.Entry<String, ServerStatus> entry : servers.entrySet())
			sb.append(String.format("%s\n", entry.getValue()));
	
		ServerStatus totals = sumTotals();		
		sb.append(String.format("Totals:\n"));
		for(QueueStatus queue : totals.getQueues().values())
			sb.append(String.format("%s\n", queue));
						
		sb.append(String.format("\nStore And Forward Queues:\n"));
		for(Map.Entry<HostServerInstance, List<StoreAndForwardQueue>> entry : storeAndForwardQueues.entrySet()) {
			sb.append(String.format("Server: %s\n", entry.getKey()));
			for(StoreAndForwardQueue queue : entry.getValue()) {
				sb.append(String.format("%s\n", queue));
			}
		}	
		sb.append(String.format("\nDisk Status:\n"));
		for(String disk : diskStatus)
			sb.append(String.format("%s\n", disk));
		sb.append(String.format("\n==============================\n"));
		return sb.toString();
	}

	public void addDiskStatus(String diskStatus) {
		this.diskStatus.add(diskStatus);
	}
	
	public List<String> getDiskStatus() {
		return diskStatus;
	}

	public void setDiskStatus(List<String> diskStatus) {
		this.diskStatus = diskStatus;
	}

	public Map<HostServerInstance, List<StoreAndForwardQueue>> getStoreAndForwardQueues() {
		return storeAndForwardQueues;
	}

	public void setStoreAndForwardQueues(Map<HostServerInstance, List<StoreAndForwardQueue>> storeAndForwardQueues) {
		this.storeAndForwardQueues = storeAndForwardQueues;
	}
}