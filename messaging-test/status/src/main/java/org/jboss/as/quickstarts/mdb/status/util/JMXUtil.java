package org.jboss.as.quickstarts.mdb.status.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.jboss.as.quickstarts.mdb.status.config.HostServerInstance;
import org.jboss.as.quickstarts.mdb.status.config.StatusConfig;
import org.jboss.as.quickstarts.mdb.status.model.StoreAndForwardQueue;

public class JMXUtil {
	private static final String JMX_URL_BASE = "service:jmx:remoting-jmx://%s:%d";
	private static final String[] HORNETQ_QUEUE_ATTRS = new String[] { "Name", "Paused", "ConsumerCount", "DeliveringCount", "MessageCount", "MessagesAdded", "ScheduledCount" };
	private static final String HORNETQ_QUEUE_MBEANS_QUERY = String.format("org.hornetq:module=Core,type=Queue,address=*,name=*");

	private MBeanServerConnection connection = null;
	private JMXConnector jmxConnector = null;
	private Logger log = Logger.getLogger(JMXUtil.class.getName());
	private String host;
	private Integer port;	
	private Map<String,String[]> env = null;
	
	public JMXUtil(StatusConfig config, HostServerInstance instance) {
		// order - instance, else hornetq-server-instances defaults, else domain controller user/pass
		this.host = instance.getAddress();
		this.port = returnFirstNotNull(instance.getJmxPort(), config.getDefaultJmxPort(), config.getDomainController().getPort());
		String username = returnFirstNotNull(instance.getJmxUsername(), config.getDefaultJmxUsername(), config.getDomainController().getUsername());
		String password = returnFirstNotNull(instance.getJmxPassword(), config.getDefaultJmxPassword(), config.getDomainController().getPassword());
		if(username != null) {
			System.out.println(String.format("For JMX Using user: %s pass: %s", username, password));
			env = new HashMap<>(1);
			env.put(JMXConnector.CREDENTIALS, new String[] { username, password });
		}
	}
	
	private Integer returnFirstNotNull(Integer...ints) {
		for(Integer i : ints) {
			if(i != null)
				return i;
		}
		return null;
	}
	
	private String returnFirstNotNull(String...strings) {
		for(String string : strings) {
			if(string != null && !string.isEmpty())
				return string;
		}
		return null;
	}

	private MBeanServerConnection getConnection() throws Exception {		
		// use the specified host/port if jmx.service.url system property is not set
		if (connection == null) {
			String urlString = System.getProperty("jmx.service.url", String.format(JMX_URL_BASE, host, port));
			log.info(String.format("JMXUrl: %s", urlString));
			JMXServiceURL serviceURL = new JMXServiceURL(urlString);
			JMXConnector jmxConnector = JMXConnectorFactory.connect(serviceURL, env);
			connection = jmxConnector.getMBeanServerConnection();			
		}
		return connection;
	}

	public void closeSafe() {
		try {
			if (jmxConnector != null)
				jmxConnector.close();
		} catch (Exception e) {
			// eat it
		}
		connection = null;
		jmxConnector = null;
	}

	public Integer getMBeanCount() throws Exception {
		MBeanServerConnection connection = getConnection();
		int count = connection.getMBeanCount();	
		return count;
	}
		
	public List<StoreAndForwardQueue> getStoreAndForwardQueueStatus() throws Exception {
		List<StoreAndForwardQueue> list = new ArrayList<StoreAndForwardQueue>();
		MBeanServerConnection connection = getConnection();		;
		ObjectName HORNETQ_QUEUE_MBEANS_ON = new ObjectName(HORNETQ_QUEUE_MBEANS_QUERY);
		
		Set<ObjectInstance> queueMBeans = connection.queryMBeans(HORNETQ_QUEUE_MBEANS_ON, null);

		for(ObjectInstance mbean : queueMBeans) {
			// Store & Forward queues have address=name= "sf.my-cluster....
			if(mbean.getObjectName().getCanonicalName().contains("address=\"sf.")) {								
//				String jmxQueueName = String.format("jms.queue.", queueName);
				// read the attributes of it				
				AttributeList attrList = connection.getAttributes(mbean.getObjectName(), HORNETQ_QUEUE_ATTRS);
				StoreAndForwardQueue queue = new StoreAndForwardQueue();
				for(int i=0; i<attrList.size(); i++) {					
					Attribute attr = (Attribute) attrList.get(i);
					switch(attr.getName()) {
						case "ConsumerCount": queue.setConsumerCount((Integer) attr.getValue()); break;
						case "DeliveringCount": queue.setDeliveringCount((Integer) attr.getValue()); break;
						case "ScheduledCount": queue.setScheduledCount((Long) attr.getValue()); break;
						case "MessageCount": queue.setMessageCount((Long) attr.getValue()); break;
						case "MessagesAdded": queue.setMessagesAdded((Long) attr.getValue()); break;
						case "Name": queue.setName((String) attr.getValue()); break;
						case "Paused": queue.setPaused((Boolean) attr.getValue()); break;
					}
				}
				list.add(queue);
			}
		}		
		return list;
	}

	public String[] getMBeanAttributeNames(ObjectName objectName) throws Exception {
		MBeanInfo info = getConnection().getMBeanInfo(objectName);
		MBeanAttributeInfo[] attributes = info.getAttributes();

		String[] attributeNames = new String[attributes.length];
		for (int i = 0; i < attributeNames.length; i++)
			attributeNames[i] = attributes[i].getName();

		return attributeNames;
	}
}