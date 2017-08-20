#!/usr/bin/env bash

echo "Usage: run-small.sh -Dconnection.factory=\"jms/RemoteConnectionFactory\" -Dusername=jmsuser -Dpassword=\"redhat1!\" [comma separated (no spaces) remote://host:port urls] [queue name] [sleep ms between sending messages] [number of threads - use multiple of # of remotes]"
echo "Usage example: run-small.sh remote://hornetq1:4447,remote://hornetq2:4447,remote://hornetq3:4477 queue2 500 3"

if [ -z "$JBOSS_HOME" ]
then
  echo "JBOSS_HOME not set"
else
	java -classpath target/classes:$JBOSS_HOME/bin/client/jboss-client.jar -Djava.naming.provider.url="remote://localhost:4447"  -Dusername=jmsuser -Dpassword="redhat1!" -Dconnection.factory="jms/RemoteConnectionFactory" -Dnumbmessages=1000 -Dclientid=sub1 org.jboss.as.quickstarts.jms.SimpleProducer $* 
# remote://localhost:4447 $1
fi
