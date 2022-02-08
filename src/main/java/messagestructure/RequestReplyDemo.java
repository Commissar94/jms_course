package messagestructure;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.HashMap;
import java.util.Map;

//JMS 2.0
public class RequestReplyDemo {
    public static void main(String[] args) throws NamingException, JMSException {

        InitialContext context = new InitialContext();
        Queue queue = (Queue) context.lookup("queue/requestQueue");
        //  Queue replyQueue = (Queue) context.lookup("queue/replyQueue"); //commented cause of trying Temporary Queue

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
             JMSContext jmsContext = cf.createContext()) {

            /*
            Создаем и отправлем сообщение
             */
            JMSProducer producer = jmsContext.createProducer();
            TemporaryQueue replyQueue = jmsContext.createTemporaryQueue();
            TextMessage message = jmsContext.createTextMessage("Hi From JMS");
            message.setJMSReplyTo(replyQueue);
            producer.send(queue, message);
            System.out.println(message.getJMSMessageID());

            /*
            Создаем мэпу из айди и сообщения
             */
            Map<String, TextMessage> requestMessages = new HashMap<>();
            requestMessages.put(message.getJMSMessageID(), message);

            /*
            Получаем сообщение
             */
            JMSConsumer consumer = jmsContext.createConsumer(queue);
            TextMessage messageReceived = (TextMessage) consumer.receive();
            System.out.println(messageReceived.getText());

            /*
            Создаем ответ, которому задаем CorrelationID, который соответстует полученному MessageID
             */
            JMSProducer replyProducer = jmsContext.createProducer();
            TextMessage replyMessage = jmsContext.createTextMessage("Hello there!!!");
            replyMessage.setJMSCorrelationID(messageReceived.getJMSMessageID());
            replyProducer.send(messageReceived.getJMSReplyTo(), replyMessage);

            /*
            Получаем ответ на первое сообщение и выводим его CorrelationID, видим что он совпадает и находим сообщение в мэпе
             */
            JMSConsumer replyConsumer = jmsContext.createConsumer(messageReceived.getJMSReplyTo());
            TextMessage replyReceived = (TextMessage) replyConsumer.receive();
            System.out.println(replyReceived.getText());
            System.out.println(replyReceived.getJMSCorrelationID());
            System.out.println(requestMessages.get(replyReceived.getJMSCorrelationID()).getText());
            // System.out.println(replyConsumer.receiveBody(String.class));
        }
    }
}
