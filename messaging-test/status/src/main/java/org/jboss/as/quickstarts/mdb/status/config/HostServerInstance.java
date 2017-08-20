/**
 * 
 */
package org.jboss.as.quickstarts.mdb.status.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author bmaxwell
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class HostServerInstance {

	@XmlAttribute(name="host-controller-name")
	private String host;
	@XmlAttribute(name="server-name")
	private String serverInstance;
	
	@XmlAttribute(name="address")
	private String address;
	@XmlAttribute(name="jmx-port")
	private Integer jmxPort;
	
	@XmlAttribute(name="jmx-username")
	private String jmxUsername;

	@XmlAttribute(name="jmx-password")
	private String jmxPassword;

	
	/**
	 * 
	 */
	public HostServerInstance() {	
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getServerInstance() {
		return serverInstance;
	}

	public void setServerInstance(String serverInstance) {
		this.serverInstance = serverInstance;
	}
	
	public String toString() {
		return String.format("%s/%s", host, serverInstance);
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getJmxPort() {
		return jmxPort;
	}

	public void setJmxPort(Integer jmxPort) {
		this.jmxPort = jmxPort;
	}

	public String getJmxUsername() {
		return jmxUsername;
	}

	public void setJmxUsername(String jmxUsername) {
		this.jmxUsername = jmxUsername;
	}

	public String getJmxPassword() {
		return jmxPassword;
	}

	public void setJmxPassword(String jmxPassword) {
		this.jmxPassword = jmxPassword;
	}
}