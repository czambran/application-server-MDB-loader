#!/usr/bin/env bash
java -classpath target/classes:$JBOSS_HOME/bin/client/jboss-client.jar -Djava.naming.provider.url="remote://localhost:4447"  -Dusername=jmsuser -Dpassword="redhat1!" -Dconnection.factory="jms/RemoteConnectionFactory" -Dnumbmessages=1000 -Dclientid=sub1 org.jboss.as.quickstarts.jms.SimpleProducer remote://localhost:4447 MyQueue 0
