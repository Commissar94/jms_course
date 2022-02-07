package messagestructure;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class MessagePriority {
    public static void main(String[] args) throws NamingException, JMSException {

        InitialContext context = new InitialContext();
        Queue queue = (Queue) context.lookup("queue/myQueue");

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
             JMSContext jmsContext = cf.createContext()) {
            JMSProducer producer = jmsContext.createProducer();

            String[] messages = new String[3];

            messages[0] = "Message One";
            messages[1] = "Message Two";
            messages[2] = "Message Three";

            producer.setPriority(3); //default priority is 4
            producer.send(queue, messages[0]);

            producer.setPriority(1);
            producer.send(queue,messages[1]);

            producer.setPriority(9);
            producer.send(queue,messages[2]);


            JMSConsumer consumer = jmsContext.createConsumer(queue);

            for (int i = 0; i < messages.length; i++) {
                Message receivedMessage = consumer.receive();
                System.out.println(receivedMessage.getJMSPriority());

                //System.out.println(consumer.receiveBody(String.class));
            }
        }
    }
}
