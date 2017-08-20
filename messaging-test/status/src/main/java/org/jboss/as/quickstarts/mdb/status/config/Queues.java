/**
 * 
 */
package org.jboss.as.quickstarts.mdb.status.config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author bmaxwell
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Queues {

	@XmlElement(name="queue")
	private List<String> queues = new ArrayList<String>();

	/**
	 * 
	 */
	public Queues() {
	}

	public List<String> getQueues() {
		return queues;
	}

	public void setQueues(List<String> queues) {
		this.queues = queues;
	}

}
