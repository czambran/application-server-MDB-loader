#!/usr/bin/env bash
java -classpath target/classes:$JBOSS_HOME/bin/client/jboss-client.jar -Djava.naming.provider.url="remote://localhost:4447"  -Dusername=jmsuser -Dpassword="redhat1!" -Dconnection.factory="jms/RemoteConnectionFactory" -Dnumbmessages=1000 -Dclientid=sub1 org.jboss.as.quickstarts.jms.LargeMessageProducer remote://localhost:4447 MyLargeQueue 10 2097152
