/**
 * 
 */
package org.jboss.as.quickstarts.mdb.status.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author bmaxwell
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class DomainController {

	@XmlAttribute(name="host")
	private String host = "localhost";
	@XmlAttribute(name="port")
	private Integer port = 9999;
	@XmlAttribute(name="username")
	private String username = "admin";
	@XmlAttribute(name="password")
	private String password = "redhat1!";
		
	/**
	 * 
	 */
	public DomainController() {
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
