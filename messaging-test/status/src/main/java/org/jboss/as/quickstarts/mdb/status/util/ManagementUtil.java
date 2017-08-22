/**
 * 
 */
package org.jboss.as.quickstarts.mdb.status.util;

import java.util.Set;
import java.util.TreeSet;

import org.jboss.as.cli.scriptsupport.CLI;
import org.jboss.as.quickstarts.mdb.status.config.HostServerInstance;
import org.jboss.as.quickstarts.mdb.status.config.StatusConfig;
import org.jboss.dmr.ModelNode;

/**
 * @author bmaxwell
 *
 */
public class ManagementUtil {

	private static final String CONSUMER_COUNT_CLI = "/host=%s/server=%s/subsystem=messaging/hornetq-server=%s/jms-queue=%s:read-attribute(name=consumer-count)";
	private static final String MSG_COUNT_CLI = "/host=%s/server=%s/subsystem=messaging/hornetq-server=%s/jms-queue=%s:read-attribute(name=message-count)";
	private static final String MSG_ADDED_CLI = "/host=%s/server=%s/subsystem=messaging/hornetq-server=%s/jms-queue=%s:read-attribute(name=messages-added)";	
	
	private CLI cli = null;
	
	/**
	 * 
	 */
	public ManagementUtil(StatusConfig config) {
		cli = CLI.newInstance();
		connect(config);
	}
	
	public void connect(StatusConfig config) {
		System.out.println(String.format("Connecting %s %d", config.getDomainController().getHost(), config.getDomainController().getPort()));
		System.out.println(String.format("Mgmt User: %s Pass: %s", config.getDomainController().getUsername(), config.getDomainController().getPassword()));
		cli.connect(config.getDomainController().getHost(), config.getDomainController().getPort(), config.getDomainController().getUsername(), config.getDomainController().getPassword().toCharArray());
	}

	private static Set<String> toSet(CLI cli, String cmd) {
		Set<String> set = new TreeSet<String>();
		for (ModelNode item : cli.cmd(cmd).getResponse().get("result").asList()) {
			set.add(item.asString());
		}
		return set;
	}

	public Set<String> getHosts() {
		// create a Set to check
		return toSet(cli, ":read-children-names(child-type=host)");
	}

	public Integer getConsumerCount(HostServerInstance hostServerInstance, String queue) {
		String consumerCountCmd = String.format(CONSUMER_COUNT_CLI, hostServerInstance.getHost(), hostServerInstance.getServerInstance(), "active", queue);
		return cli.cmd(consumerCountCmd).getResponse().get("result").asInt();
	}
	public Integer getMessageCount(HostServerInstance hostServerInstance, String queue) {
		String messageCountCmd = String.format(MSG_COUNT_CLI, hostServerInstance.getHost(), hostServerInstance.getServerInstance(), "active", queue);
		return cli.cmd(messageCountCmd).getResponse().get("result").asInt();
	}
	public Long getMessagesAdded(HostServerInstance hostServerInstance, String queue) {
		String messageCountCmd = String.format(MSG_ADDED_CLI, hostServerInstance.getHost(), hostServerInstance.getServerInstance(), "active", queue);
		return cli.cmd(messageCountCmd).getResponse().get("result").asLong();
	}

	public void close() {
		if (cli != null)
			cli.disconnect();
	}
}
