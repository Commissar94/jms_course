import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Enumeration;

//P2P
public class QueueBrowserDemo {

    public static void main(String[] args) {
        InitialContext initialContext = null;
        Connection connection = null;

        try {
            initialContext = new InitialContext();
            ConnectionFactory cf = (ConnectionFactory) initialContext.lookup("ConnectionFactory"); //from jndi.prop
            connection = cf.createConnection();

            Session session = connection.createSession();

            Queue queue = (Queue) initialContext.lookup("queue/myQueue"); //from jndi.prop

            MessageProducer producer = session.createProducer(queue);
            TextMessage message1 = session.createTextMessage("My first Text Message");
            TextMessage message2 = session.createTextMessage("My second Text Message");
            TextMessage message3 = session.createTextMessage("My third Text Message");

            producer.send(message1);
            producer.send(message2);
            producer.send(message3);

            QueueBrowser browser = session.createBrowser(queue);
            Enumeration messagesEnum = browser.getEnumeration();

            while (messagesEnum.hasMoreElements()) {
                TextMessage eachMessage = (TextMessage) messagesEnum.nextElement();
                System.out.println("Browsing: " + eachMessage.getText());
            }

            MessageConsumer consumer = session.createConsumer(queue);
            connection.start();
            TextMessage messageReceived = (TextMessage) consumer.receive();
            System.out.println("Received message: " + messageReceived.getText());
            messageReceived = (TextMessage) consumer.receive();
            System.out.println("Received message: " + messageReceived.getText());
            messageReceived = (TextMessage) consumer.receive();
            System.out.println("Received message: " + messageReceived.getText());

        } catch (NamingException | JMSException e) {
            e.printStackTrace();
        } finally {
            if (initialContext != null) {
                try {
                    initialContext.close(); // need to close context
                } catch (NamingException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();  // need to close connection
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
