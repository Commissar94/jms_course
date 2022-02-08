package messagestructure;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.Serializable;

//JMS 2.0
public class MessageTypesDemo {
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

            /*
            Создаем сообщение типа Байт и записываем в него строку и число
             */
            BytesMessage bytesMessage = jmsContext.createBytesMessage();
            bytesMessage.writeUTF("John");
            bytesMessage.writeLong(150L);

            /*
            Создаем сообщение типа Stream
             */
            StreamMessage streamMessage = jmsContext.createStreamMessage();
            streamMessage.writeBoolean(true);
            streamMessage.writeFloat(2.5F);

            /*
            Создаем сообщение типа Map
             */
            MapMessage mapMessage = jmsContext.createMapMessage();
            mapMessage.setBoolean("isCreditAvailable", true);


            /*
            Создаем сообщение типа Object
             */
            ObjectMessage objectMessage = jmsContext.createObjectMessage();
            Patient patient = new Patient();
            patient.setId(1);
            patient.setName("John");
            objectMessage.setObject(patient);

            /*
            Отправляем сообщение (отправлял поочередно разные типы)

             */
            producer.send(queue, patient);

            /*
            Пытаемся получить байтовое сообщение и считываем его строку и число
             */

//            BytesMessage messageReceived2 = (BytesMessage) jmsContext.createConsumer(queue).receive();
//            System.out.println(messageReceived2.readUTF());
//            System.out.println(messageReceived2.readLong());

            /*
            Пытаемся получить Stream сообщение и считываем его флаг и число
             */

//            StreamMessage messageReceived2 = (StreamMessage) jmsContext.createConsumer(queue).receive();
//            System.out.println(messageReceived2.readBoolean());
//            System.out.println(messageReceived2.readFloat());

            /*
            Пытаемся получить Map сообщение и считываем его значение по ключу
             */

//            MapMessage messageReceived2 = (MapMessage) jmsContext.createConsumer(queue).receive();
//            System.out.println(messageReceived2.getBoolean("isCreditAvailable"));

            /*
            Пытаемся получить Object сообщение и считываем его объект
            Не обязательно создавать типы сообщений, метод Send может принимать сразу и объект и сообщение и мэпу
             */
            Patient patientRecived = jmsContext.createConsumer(queue).receiveBody(Patient.class);
            //Patient object = (Patient) messageReceived2.getObject();
            System.out.println(patientRecived.getId());
            System.out.println(patientRecived.getName());
        }
    }
}
