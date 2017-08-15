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
package org.jboss.as.quickstarts.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Properties;
import java.util.logging.Logger;

public class AbstractJMS
{
   private static final Logger log = Logger.getLogger(AbstractJMS.class.getName());

   // Set up all the default values
   private static final String DEFAULT_CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
   private static final String DEFAULT_USERNAME = "guest";
   private static final String DEFAULT_PASSWORD = "Redhat1!";
   private static final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
   //private static final String PROVIDER_URL = "remote://127.0.0.1:4447";

   /** public static void main(String[] args) throws Exception
   {
      YourClass object = new YourClass();
       object.doRun(args[0], args[1]);
   } */

   private ConnectionFactory connectionFactory;
   Connection connection = null;
   Queue queue = null;
   Context context = null;

   public void connect(String remote, String destinationString) throws Exception {

      System.out.println("Parameters expected:");
      System.out.println("[0]=remote://IP:4447");
      System.out.println("[1]=destination");


      try
      {
         // Set up the context for the JNDI lookup
         final Properties env = new Properties();
         env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
         env.put(Context.PROVIDER_URL, System.getProperty(Context.PROVIDER_URL, remote));
         env.put(Context.SECURITY_PRINCIPAL, System.getProperty("username", DEFAULT_USERNAME));
         env.put(Context.SECURITY_CREDENTIALS, System.getProperty("password", DEFAULT_PASSWORD));
         context = new InitialContext(env);

         // Perform the JNDI lookups
         String connectionFactoryString = System.getProperty("connection.factory", DEFAULT_CONNECTION_FACTORY);
         log.info("Attempting to acquire connection factory \"" + connectionFactoryString + "\"");
         connectionFactory = (ConnectionFactory) context.lookup(connectionFactoryString);
         log.info("Found connection factory \"" + connectionFactoryString + "\" in JNDI");

         log.info("Attempting to acquire destination \"" + destinationString + "\"");
         queue = (Queue) context.lookup(destinationString);
         // large_queue = (Queue) context.lookup(large_destinationString);
         log.info("Found destination \"" + destinationString + "\" in JNDI");

         // Create the JMS connection, session, producer, and consumer
         connection = connectionFactory.createConnection(System.getProperty("username", DEFAULT_USERNAME), System.getProperty("password", DEFAULT_PASSWORD));

      }
      catch (Exception e)
      {
         log.severe(e.getMessage());
         throw e;
      }
   }
}

