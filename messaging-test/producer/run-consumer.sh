#!/usr/bin/env bash

if [ -z "$JBOSS_HOME" ]
then
	echo "JBOSS_HOME not set"
else
	java -classpath target/classes:$JBOSS_HOME/bin/client/jboss-client.jar -Dusername=jmsuser -Dpassword="redhat1!" -Dconnection.factory="jms/RemoteConnectionFactory" -Dnumbmessages=1000 -Dclientid=sub1 org.jboss.as.quickstarts.jms.SimpleConsumer $*
fi
