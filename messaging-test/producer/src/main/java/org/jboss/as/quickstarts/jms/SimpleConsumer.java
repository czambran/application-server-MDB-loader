/*
 * Copyright 2005-2014 Red Hat, Inc.
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */

package org.jboss.as.quickstarts.jms;

import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

/**
 * @author Clebert Suconic
 */

public class SimpleConsumer extends AbstractJMS
{

   public static void main(String arg[])
   {
      SimpleConsumer producer = new SimpleConsumer();
      try
      {
         producer.connect(arg[0], arg[1]);
         producer.run();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }


  public void run() throws Exception
   {
      Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      MessageConsumer consumer = session.createConsumer(queue);

      connection.start();

      long i = 0;
      while (true) {
         Message message = consumer.receive(5000);
         if (message == null) {
            System.out.println("Didn't receive a message for 5 seconds, giving up");
            break;
         }
         i++;
         if (i % 100 == 0)
         {
            System.out.println("Received " + i + " messages on " + queue);
         }
      }
   }
}
