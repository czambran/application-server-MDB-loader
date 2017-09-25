package org.jboss.as.quickstarts.mdb;

import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.BytesMessage;
import javax.jms.Session;
import javax.naming.Context;

/**
 * @author bmaxwell
 */
public abstract class AbstractMessageConsumer implements MessageListener {

    private Logger log = Logger.getLogger(this.getClass().getName());
    private MDBStats mdbStats = null; // this is not static, but the one passed
    // in from MessageConsumer is static
    @Resource(name = "jmsuser")
    private String jmsUser;

    @Resource(name = "jmspass")
    private String jmsPass;

    @Resource(name = "messageQueue3")
    private Destination messageQueue3;

    @Resource(name = "connectionFactory")
    private ConnectionFactory connectionFactory;

    private boolean sendToQueue3 = false;

    /**
     *
     */
    public AbstractMessageConsumer(MDBStats mdbStats, boolean sendToQueue3) {
        this.mdbStats = mdbStats;
        this.sendToQueue3 = sendToQueue3;
    }

    public void onMessage(Message msg) {
        long start = System.currentTimeMillis();

        try {
            if (msg instanceof BytesMessage)
            {
                String messageId = msg.getStringProperty("MESSAGE_ID");
                System.out.println(">>>>>>>>>>" + "MESSAGE_ID:" + messageId + "<<<<<<<<<<<<");
            }
            try {
                if (sendToQueue3)
                    sendMessage(messageQueue3, jmsUser, jmsPass);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // add the invocation start/finish time to the stats
                mdbStats.addInvocation(start, System.currentTimeMillis());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Destination queue, String jmsUser, String jmsPass) {
        Connection connection = null;
        MessageProducer producer = null;
        try {
            // jmsUser, jmsPass
            connection = connectionFactory.createConnection();
            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
            producer = session.createProducer(queue);
            producer.send(session.createTextMessage("Hello"));
            connection.start();
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            closeSafe(producer);
            closeSafe(connection);
        }
    }

    protected static void closeSafe(MessageProducer producer) {
        if (producer != null) {
            try {
                producer.close();
            } catch (Exception e) {
            }
        }
    }

    protected static void closeSafe(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
            }
        }
    }

    protected static void closeSafe(Context ctx) {
        if (ctx != null) {
            try {
                ctx.close();
            } catch (Exception e) {
            }
        }
    }
}