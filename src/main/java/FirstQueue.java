import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

//P2P
public class FirstQueue {

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
            TextMessage message = session.createTextMessage("My first Text Message");
            producer.send(message);
            System.out.println("Send message: " + message.getText());

            MessageConsumer consumer = session.createConsumer(queue);
            connection.start();
            TextMessage messageReceived = (TextMessage) consumer.receive(5000); // 5000 is 5 sec delay, not necessary
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
            if (connection != null){
                try {
                    connection.close();  // need to close connection
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
