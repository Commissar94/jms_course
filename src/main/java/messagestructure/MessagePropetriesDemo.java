package messagestructure;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

//JMS 2.0
public class MessagePropetriesDemo {
    public static void main(String[] args) throws NamingException, JMSException {

        InitialContext context = new InitialContext();
        Queue queue = (Queue) context.lookup("queue/myQueue");

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
             JMSContext jmsContext = cf.createContext()) {

            /*
            Отправляем сообщение с двумя кастомными свойствами
             */

            JMSProducer producer = jmsContext.createProducer();

            TextMessage message = jmsContext.createTextMessage("Hi from JMS");
            message.setBooleanProperty("loggedIn", true);
            message.setStringProperty("userToken", "abc123");
            producer.send(queue, message);


            /*
            Пытаемся получить сообщение и его свойства
             */

            Message messageReceived2 = jmsContext.createConsumer(queue).receive();
            System.out.println(messageReceived2);
            System.out.println(messageReceived2.getBooleanProperty("loggedIn"));
            System.out.println(messageReceived2.getStringProperty("userToken"));

        }
    }
}
