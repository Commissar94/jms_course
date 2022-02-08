package messagestructure;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

//JMS 2.0
public class MessageExpirationDemo {
    public static void main(String[] args) throws NamingException, InterruptedException {

        InitialContext context = new InitialContext();
        Queue queue = (Queue) context.lookup("queue/myQueue");
        Queue expiryQueue = (Queue) context.lookup("queue/expiryQueue");

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
             JMSContext jmsContext = cf.createContext()) {

            /*
            Отправляем сообщение с временем жизни в 2 секунды и засыпаем на 4 секунды, чтобы сообщение точно истекло
             */

            JMSProducer producer = jmsContext.createProducer();
            producer.setTimeToLive(2000);
            producer.send(queue, "Hi from JMS");
            Thread.sleep(4000);

            /*
            Пытаемся получить сообщение, но получаем null: время жизни сообщения истекло
             */

            Message messageReceived2 = jmsContext.createConsumer(queue).receive(4000);
            System.out.println(messageReceived2);

            /*
            Но истекшие сообщения хранятся в специальной очереди, что создается по умолчанию в ActiveMQ - ExpiryQueue
             */
            System.out.println(jmsContext.createConsumer(expiryQueue).receiveBody(String.class));
        }
    }
}
