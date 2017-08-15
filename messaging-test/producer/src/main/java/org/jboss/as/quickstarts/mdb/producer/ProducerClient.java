/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.mdb.producer;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

import java.util.Hashtable;
import java.util.logging.Logger;

/**
 * @author bmaxwell
 */
public class ProducerClient {
	
	private static final Logger logger = Logger.getLogger(ProducerClient.class.getName());
	private static final long SIZE = 200000;

    public static void main(String[] args) throws Exception {
    	
    	for(int i=0; i<100; i++) {
    	 sendMessage("localhost", "4447", "jmsuser", "redhat1!", "jms/RemoteConnectionFactory", "jms/queue/messageQueue1", 1L, 0L);
    	 sendMessage("localhost", "4447", "jmsuser", "redhat1!", "jms/RemoteConnectionFactory", "jms/queue/messageQueue2", 1L, 0L);
    	}
    	
    }
    
    private static Context getRemoteNamingContext(String host, String port, String user, String pass) throws Exception {
        String providerURL = "remote://"+host+":"+port;
        System.out.println("providerURL: " + providerURL);

        Hashtable<String, Object> p = new Hashtable<String, Object>();
        p.put(InitialContext.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
        p.put(InitialContext.PROVIDER_URL, providerURL);

        p.put(Context.SECURITY_PRINCIPAL,"testuser1");
        p.put(Context.SECURITY_CREDENTIALS, "testuser1!");
        p.put("org.xnio.Options.SASL_POLICY_NOPLAINTEXT", "false");
        p.put("jboss.naming.client.connect.options.org.xnio.Options.SASL_POLICY_NOPLAINTEXT", "false");

        return new InitialContext(p);
      }

    public static void sendMessage(String host, String port, String user, String pass, String connectionFactoryJNDIAddress, String queueJNDIAddress, Long number, Long sleep) throws Exception {
      Context initialContext = getRemoteNamingContext(host, port, user, pass);
      ConnectionFactory connectionFactory = (ConnectionFactory) initialContext.lookup(connectionFactoryJNDIAddress);
      Connection connection = connectionFactory.createConnection(user,pass);
      Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      MessageProducer producer = session.createProducer((Destination) initialContext.lookup(queueJNDIAddress));
      producer.setDeliveryMode(DeliveryMode.PERSISTENT);
      connection.start();
      TextMessage textmessage = session.createTextMessage(sleep.toString());
      for (int i = 0; i < number; i++) {
        try {
          producer.send(textmessage);
          if (i % 1000 == 0 && i != 0) {
            logger.info("Sent " + i + " messages so far...");
          }
        } catch (Exception e) {
        	e.printStackTrace();
        }
      }
      connection.close();
    }
}
