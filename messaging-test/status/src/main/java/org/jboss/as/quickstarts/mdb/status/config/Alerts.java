/**
 * 
 */
package org.jboss.as.quickstarts.mdb.status.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author bmaxwell
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Alerts {

	@XmlElement(name="message-increase-percentage")
	private Integer messageIncreasePercentage = 10;
	
	/**
	 * 
	 */
	public Alerts() {
	}

	public Integer getMessageIncreasePercentage() {
		if(messageIncreasePercentage < 1)
			return null;
		return messageIncreasePercentage;
	}
	public Boolean isMessageIncreased(Integer previous, Integer current) {
		if(getMessageIncreasePercentage() == null || current == 0)
			return false; 
		float increasePercent = previous * ((getMessageIncreasePercentage() / 100f) + 1);		
		if((float)current >= increasePercent)
			return true;
		return false;
	}
}
