/**
 * 
 */
package org.jboss.as.quickstarts.mdb.status.config;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author bmaxwell
 *
 */
@XmlRootElement(name="status-config")
@XmlAccessorType(XmlAccessType.FIELD)
public class StatusConfig {

	public static final String DEFAULT_CONFIG_FILE_NAME = "status-config.xml"; 
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy-hh_mm_ss");
	
	// CLI/management api host/port/user/pass
	@XmlElement(name="domain-controller")
	private DomainController domainController;
	
	// list of HQ server instances
	@XmlElement(name="hornetq-server-instances")
	private HostServerInstances hornetqServerInstances;
	
	// list of queues to check
	@XmlElement(name="queues")
	private Queues queues;

	@XmlElement(name="disk-usage")
	private DiskUsage diskUsage;	
	
	// interval to log
	@XmlElement(name="interval")
	private Integer interval = 5;
	
	// output dir or file
	@XmlElement(name="output-dir")
	private String outputDir;
	
	// alerts of change from last status
	@XmlElement(name="alerts")
	private Alerts alerts = new Alerts();
	
	@XmlTransient
	private Date startDate = null;
	
	// 1 - connect to management interface
	// 2 - validate HQ instances, queues are valid
	// 3 - read the active/backup dirs for each HQ instance and log it
	// 4 - log: read consumer/message count for each, disk usage, etc
	// some how read the store and forward queue from JMX - either 
	
	/**
	 * 
	 */
	public StatusConfig() {
		startDate = new Date();
	}

	public List<String> getQueues() {
		return queues.getQueues();
	}

	public void setQueues(List<String> queues) {
		this.queues.setQueues(queues);
	}

	public Integer getInterval() {
		return interval;
	}

	public void setInterval(Integer interval) {
		this.interval = interval;
	}

	public String getOutputDir() {
		return outputDir;
	}

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}

	public void marshall(String fileName) throws Exception {
		JAXBContext ctx = JAXBContext.newInstance(StatusConfig.class);
		Marshaller marshaller = ctx.createMarshaller();
		marshaller.marshal(this, new File(fileName));
	}

	public static StatusConfig unmarshall(String fileName) throws Exception {
		File file = new File(fileName);
		if(!file.exists())
			throw new RuntimeException(String.format("Configuration file %s does not exist", file));
		if(!file.canRead())
			throw new RuntimeException(String.format("Unable to read Configuration file %s", file));
		JAXBContext ctx = JAXBContext.newInstance(StatusConfig.class);
		Unmarshaller unmarshaller = ctx.createUnmarshaller();
		return (StatusConfig) unmarshaller.unmarshal(new File(fileName));
	}
	
	public List<HostServerInstance> getHornetqServerInstances() {
		return hornetqServerInstances.getInstances();
	}

	public void setHornetqServerInstances(List<HostServerInstance> hornetqServerInstances) {
		this.hornetqServerInstances.setInstances(hornetqServerInstances);
	}
	
	public String getDefaultJmxUsername() {
		return this.hornetqServerInstances.getDefaultJmxUsername();
	}

	public String getDefaultJmxPassword() {
		return this.hornetqServerInstances.getDefaultJmxPassword();
	}
	
	public Integer getDefaultJmxPort() {
		return this.hornetqServerInstances.getDefaultJmxPort();
	}

	public List<String> getDirs() {
		return diskUsage.getDirs();
	}

	public void setDirs(List<String> dirs) {
		this.diskUsage.setDirs(dirs);
	}
	
	@XmlTransient
	public String getLogFileName() {
		String outputFileName;
		if(getOutputDir() == null || getOutputDir().isEmpty())
			outputFileName = "status-logs-" + DATE_FORMAT.format(getStartDate()) + ".log";
		else
			outputFileName = getOutputDir() + "/status-logs-" + DATE_FORMAT.format(getStartDate()) + ".log";
		return outputFileName;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Alerts getAlerts() {
		return alerts;
	}

	public void setAlerts(Alerts alerts) {
		this.alerts = alerts;
	}

	public DomainController getDomainController() {
		return domainController;
	}

	public void setDomainController(DomainController domainController) {
		this.domainController = domainController;
	}
}