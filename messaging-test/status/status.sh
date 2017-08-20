class=org.jboss.as.quickstarts.mdb.status.cli.StatusMonitor
if [ -z "$JBOSS_HOME" ]
then
  echo "JBOSS_HOME not set"
else
	java -cp target/status.jar:$JBOSS_HOME/bin/client/jboss-client.jar:$JBOSS_HOME/bin/client/jboss-cli-client.jar $class $*
fi
