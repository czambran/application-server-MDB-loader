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
import javax.jms.Session;

/**
 * @author Clebert Suconic
 */

public class SimpleConsumer extends AbstractJMS implements Runnable {

	public SimpleConsumer(String remote, String destinationString) {
		super(remote, destinationString);
	}

	public static void main(String args[]) {
		
		if(args.length < 3) {
			System.out.println("Usage: [remote://IP:4447] [destination] [numberOfThreads]");
			System.out.println("To configure username/password: -Dusername=username -Dpassword=password");
			System.exit(0);
		}
		
		String remote = args[0];
		String destinationString = args[1];
		int numberOfThreads = Integer.parseInt(args[2]);
		SimpleConsumer[] consumers = new SimpleConsumer[numberOfThreads];
		for (int i = 0; i < consumers.length; i++)
			consumers[i] = new SimpleConsumer(remote, destinationString);

		createAndRunThreads(consumers);
	}

	public void run() {
		try {
			connect();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageConsumer consumer = session.createConsumer(queue);

			connection.start();

			long i = 0;
			while (true) {
				Message message = consumer.receive(5000);
				if (message == null) {
					logThreadMessage("Didn't receive a message for 5 seconds, giving up");
					break;
				}
				i++;
				if (i % 100 == 0) {
					logThreadMessage("Received " + i + " messages on " + queue);
				}
			}
		} catch (Exception e) {			
			e.printStackTrace();
		}
	}
}
