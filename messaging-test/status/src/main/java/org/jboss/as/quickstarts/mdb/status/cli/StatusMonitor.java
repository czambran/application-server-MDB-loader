/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.as.quickstarts.mdb.status.cli;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.jboss.as.quickstarts.mdb.status.config.HostServerInstance;
import org.jboss.as.quickstarts.mdb.status.config.StatusConfig;
import org.jboss.as.quickstarts.mdb.status.model.Status;
import org.jboss.as.quickstarts.mdb.status.model.StoreAndForwardQueue;
import org.jboss.as.quickstarts.mdb.status.util.DiskUtil;
import org.jboss.as.quickstarts.mdb.status.util.JMXUtil;
import org.jboss.as.quickstarts.mdb.status.util.ManagementUtil;
import org.jboss.logging.Logger;

/**
 * @author bmaxwell
 *
 */
public class StatusMonitor {
	private final Logger log = Logger.getLogger(this.getClass().getName());	
	private ExecutorService executor = null;
	private PrintWriter logWriter = null;
	private StatusConfig config = null;
	
	public StatusMonitor(StatusConfig config) {
		this.config = config;
	}

	public static void main(String[] args) throws Exception {
		String statusConfigFilename = StatusConfig.DEFAULT_CONFIG_FILE_NAME;
		if(args.length > 0)
			statusConfigFilename = args[0];
		System.out.println(String.format("Reading config: %s", statusConfigFilename));
		StatusConfig config = StatusConfig.unmarshall(statusConfigFilename);
		StatusMonitor statusMonitor = new StatusMonitor(config);
		DiskUtil diskUtil = new DiskUtil(statusMonitor.getExecutor());
		Map<HostServerInstance, JMXUtil> jmxUtilMap = new HashMap<>();
		ManagementUtil mgmtUtil = new ManagementUtil(config);
		for(HostServerInstance instance : config.getHornetqServerInstances())			
			jmxUtilMap.put(instance, new JMXUtil(config, instance));
		try {			
			statusMonitor.runOnInterval(mgmtUtil, jmxUtilMap, diskUtil);
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			statusMonitor.closeLogWriter();			
			for(JMXUtil jmxUtil : jmxUtilMap.values())
				jmxUtil.closeSafe();
			mgmtUtil.close();
			statusMonitor.getExecutor().shutdownNow();
		}
	}

	public void runOnInterval(ManagementUtil mgmtUtil, Map<HostServerInstance, JMXUtil> jmxUtilMap, DiskUtil diskUtil) {
		Status previousStatus = null;
		while (true) {
			try {
				previousStatus = run(mgmtUtil, jmxUtilMap, diskUtil, previousStatus);
				// interval should be seconds so * 1000 for milliseconds
				Thread.sleep(config.getInterval() * 1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
		
	public Status run(ManagementUtil mgmtUtil, Map<HostServerInstance, JMXUtil> jmxUtilMap, DiskUtil diskUtil, Status previousStatus) throws Exception {
		Status status = new Status(config);
		try {					
			// create a Set to check
			Set<String> hostsUp = mgmtUtil.getHosts();			
			
			// dispatch to executor to check the store and forward queues
			Future<Map<HostServerInstance, List<StoreAndForwardQueue>>> storeAndForwardStatus = getStoreAndForwardQueuesFromServerInstances(jmxUtilMap, hostsUp);
			// dispatch to executor to check the disk usage while CLI commands are invoked
			Future<List<String>> diskUsages = diskUtil.diskUsage(config.getDirs());
			
			for (HostServerInstance hostServerInstance : config.getHornetqServerInstances()) {				
				// skip host if down
				if (!hostsUp.contains(hostServerInstance.getHost())) {
					status.addInfo(String.format("%s is down", hostServerInstance));
					continue;
				}

				for (String queue : config.getQueues()) {					
					Integer consumerCount = null, messageCount = null;
					try {
						consumerCount = mgmtUtil.getConsumerCount(hostServerInstance, queue);
						messageCount = mgmtUtil.getMessageCount(hostServerInstance, queue);
						status.addServerStatus(hostServerInstance.toString()).addQueueStatus(queue, consumerCount,
								messageCount);
					} catch (Exception e) {
						System.err.println(String.format("Error trying to read status for %s - %s - %s",
								hostServerInstance, "active", queue));
					}
				}
			}			
			status.setDiskStatus(diskUsages.get(5L, TimeUnit.SECONDS));
			status.setStoreAndForwardQueues(storeAndForwardStatus.get(5L, TimeUnit.SECONDS));
			status.checkForAnomalies(previousStatus);
			System.out.println("Writing status to: " + config.getLogFileName());
			getLogWriter().println(status.toString());
			getLogWriter().flush();
			System.out.println(status);
		} finally {
		}
		return status;
	}

	private Future<Map<HostServerInstance, List<StoreAndForwardQueue>>> getStoreAndForwardQueuesFromServerInstances(final Map<HostServerInstance, JMXUtil> jmxUtilMap, final Set<String> hostsUp) {		
		return getExecutor().submit(new Callable<Map<HostServerInstance, List<StoreAndForwardQueue>>>() {
			@Override
			public Map<HostServerInstance, List<StoreAndForwardQueue>> call() {
				Map<HostServerInstance, List<StoreAndForwardQueue>> map = new HashMap<>(jmxUtilMap.size());
				for(Map.Entry<HostServerInstance, JMXUtil> entry : jmxUtilMap.entrySet()) {
					try {
						// skip hosts that are down
						if (hostsUp.contains(entry.getKey().getHost()))
							map.put(entry.getKey(),entry.getValue().getStoreAndForwardQueueStatus());						
					} catch(Exception e) {
						e.printStackTrace();
					}
				}								
				return map;
			}
		});
	}
	
	private PrintWriter getLogWriter() throws IOException {
		if (logWriter == null)
			logWriter = new PrintWriter(new BufferedWriter(new FileWriter(config.getLogFileName(), true)));
		return logWriter;
	}

	private void closeLogWriter() {
		if (logWriter != null)
			logWriter.close();
		logWriter = null;
	}

	private ExecutorService getExecutor() {
		if (executor == null)
			executor = Executors.newFixedThreadPool(7);
		return executor;
	}
}