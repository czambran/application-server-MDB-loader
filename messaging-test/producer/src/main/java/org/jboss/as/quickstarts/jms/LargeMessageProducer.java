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

import javax.jms.BytesMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;

/**
 * @author Clebert Suconic
 */

public class LargeMessageProducer extends AbstractJMS implements Runnable {
	
	private int sleepTime; 
	private int size;
	
	public LargeMessageProducer(String remote, String destinationString, int sleepTime, int size) {
		super(remote, destinationString);
		this.sleepTime = sleepTime;
		this.size = size;
	}
	
	public static void main(String args[]) {
		
		if(args.length < 5) {
			System.out.println("Usage: [remote://IP:4447,remote://IP:4447] [destination] [sleepTime] [size] [numberOfThreads]");
			System.out.println("Args Length: " + args.length);
			System.out.println("arg[0] = remote = " + args[0]);
			System.out.println("arg[1] = destination = " + args[1]);
			System.out.println("arg[2] = sleepTime = " + args[2]);
			System.out.println("arg[3] = size = " + args[3]);
			System.out.println("arg[4] = numberOfThreads = " + args[4]);
			System.out.println("To configure username/password: -Dusername=username -Dpassword=password");
			System.exit(0);
		}
		
		String remote = args[0];
		String destinationString = args[1];		
		int sleepTime = Integer.parseInt(args[2]);
		int size = Integer.parseInt(args[3]);
		int numberOfThreads = Integer.parseInt(args[4]);
		LargeMessageProducer[] producers = new LargeMessageProducer[numberOfThreads];
		for (int i = 0; i < producers.length; i++)
			producers[i] = new LargeMessageProducer(remote, destinationString, sleepTime, size);

		createAndRunThreads(producers);

		
//		LargeMessageProducer producer = new LargeMessageProducer();
//		try {
//			producer.connect(arg[0], arg[1]);
//			producer.run(Integer.parseInt(arg[2]), Integer.parseInt(arg[3]));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

//	public void run(int sleepTime, int size) throws Exception {
	public void run() {
		try {
			connect();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer producer = session.createProducer(queue);
			long thread_id = System.currentTimeMillis();
	
			logThreadMessage("sleepTime " + sleepTime);
			logThreadMessage("Using large message size as " + size);
			long i = 0;
			while (i < 500) {
                BytesMessage bytesMessage = session.createBytesMessage();
                byte[] messageContent = new byte[size];
                String message_id = (Long.toString(thread_id) + "-" + i);
                // Adding Message ID as a property
                bytesMessage.setStringProperty("MESSAGE_ID", message_id);
			    byte[] message_id_in_bytes = message_id.getBytes();
			    // Adding Message ID as the first part of the message body
                System.arraycopy(message_id_in_bytes, 0, messageContent, 0, message_id_in_bytes.length);
                bytesMessage.writeBytes(messageContent);
                producer.send(bytesMessage);
                logThreadMessage("Produced message with id: " + message_id);
                i++;
				if (sleepTime > 0) {
					Thread.sleep(sleepTime);
				}
				if (i % 100 == 0) {
					logThreadMessage("Sent " + i + " messages on " + queue);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}