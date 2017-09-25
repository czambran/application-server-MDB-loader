/**
 * 
 */
package org.jboss.as.quickstarts.mdb.status.config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author bmaxwell
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class HostServerInstances {

	@XmlElement(name="server-instance")
	private List<HostServerInstance> instances = new ArrayList<>();
	
	@XmlAttribute(name="default-jmx-port")
	private Integer defaultJmxPort;
	
	@XmlAttribute(name="default-jmx-username")
	private String defaultJmxUsername;

	@XmlAttribute(name="default-jmx-password")
	private String defaultJmxPassword;
	
	/**
	 * 
	 */
	public HostServerInstances() {
	}

	public List<HostServerInstance> getInstances() {
		return instances;
	}

	public void setInstances(List<HostServerInstance> instances) {
		this.instances = instances;
	}

	public Integer getDefaultJmxPort() {
		return defaultJmxPort;
	}

	public void setDefaultJmxPort(Integer defaultJmxPort) {
		this.defaultJmxPort = defaultJmxPort;
	}

	public String getDefaultJmxUsername() {
		return defaultJmxUsername;
	}

	public void setDefaultJmxUsername(String defaultJmxUsername) {
		this.defaultJmxUsername = defaultJmxUsername;
	}

	public String getDefaultJmxPassword() {
		return defaultJmxPassword;
	}

	public void setDefaultJmxPassword(String defaultJmxPassword) {
		this.defaultJmxPassword = defaultJmxPassword;
	}

}