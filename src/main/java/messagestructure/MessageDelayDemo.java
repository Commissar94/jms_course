package messagestructure;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

//JMS 2.0
public class MessageDelayDemo {
    public static void main(String[] args) throws NamingException, InterruptedException {

        InitialContext context = new InitialContext();
        Queue queue = (Queue) context.lookup("queue/myQueue");

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
             JMSContext jmsContext = cf.createContext()) {

            /*
            Отправляем сообщение с задержкой в 3 секунды
             */

            JMSProducer producer = jmsContext.createProducer();
            producer.setDeliveryDelay(3000);
            producer.send(queue, "Hi from JMS");


            /*
            Пытаемся получить сообщение с задержкой и получаем его через 3 секунды
             */

            Message messageReceived2 = jmsContext.createConsumer(queue).receive(4000);
            System.out.println(messageReceived2);

        }
    }
}
