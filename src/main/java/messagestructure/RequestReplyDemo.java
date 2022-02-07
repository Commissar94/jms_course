package messagestructure;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

//JMS 2.0
public class RequestReplyDemo {
    public static void main(String[] args) throws NamingException, JMSException {

        InitialContext context = new InitialContext();
        Queue queue = (Queue) context.lookup("queue/requestQueue");
      //  Queue replyQueue = (Queue) context.lookup("queue/replyQueue"); //commented cause of trying Temporary Queue

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
             JMSContext jmsContext = cf.createContext()) {

            JMSProducer producer = jmsContext.createProducer();
            TemporaryQueue replyQueue = jmsContext.createTemporaryQueue();
            TextMessage message = jmsContext.createTextMessage("Hi From JMS");
            message.setJMSReplyTo(replyQueue);
            producer.send(queue, message);

            JMSConsumer consumer = jmsContext.createConsumer(queue);
            TextMessage messageReceived = (TextMessage) consumer.receive();
            System.out.println(messageReceived.getText());

            JMSProducer replyProducer = jmsContext.createProducer();
            replyProducer.send(messageReceived.getJMSReplyTo(), "Hello there!!!");

            JMSConsumer replyConsumer = jmsContext.createConsumer(messageReceived.getJMSReplyTo());
            System.out.println(replyConsumer.receiveBody(String.class));
        }
    }
}
