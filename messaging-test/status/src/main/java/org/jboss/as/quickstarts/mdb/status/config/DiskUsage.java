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
public class DiskUsage {

	@XmlElement(name="dir")
	private List<String> dirs = new ArrayList<String>();
	
	/**
	 * 
	 */
	public DiskUsage() {	
	}
	public List<String> getDirs() {
		return dirs;
	}
	public void setDirs(List<String> dirs) {
		this.dirs = dirs;
	}
}