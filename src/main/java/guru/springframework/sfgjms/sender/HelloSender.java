package guru.springframework.sfgjms.sender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.sfgjms.config.JmsConfig;
import guru.springframework.sfgjms.model.HelloWorldMessage;
import java.util.UUID;
import javax.jms.JMSException;
import javax.jms.Message;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class HelloSender {
  private final JmsTemplate jmsTemplate;
  private final ObjectMapper objectMapper;

  public HelloSender(JmsTemplate jmsTemplate, ObjectMapper objectMapper) {
    this.jmsTemplate = jmsTemplate;
    this.objectMapper = objectMapper;
  }

  @Scheduled(fixedRate = 2000)
  public void sendMessage() {

    HelloWorldMessage message =
        HelloWorldMessage.builder().id(UUID.randomUUID()).message("Hello World").build();

    jmsTemplate.convertAndSend(JmsConfig.MY_QUEUE, message);
  }

  @Scheduled(fixedRate = 2000)
  public void sendAndRecieveMessage() throws JMSException {
    System.out.println("I'm Sending a Message");

    HelloWorldMessage message =
        HelloWorldMessage.builder().id(UUID.randomUUID()).message("Hello").build();
    Message recieveMessage =
        jmsTemplate.sendAndReceive(
            JmsConfig.MY_SEND_RCV_QUEUE,
            session -> {
              Message helloMessage = null;
              try {
                helloMessage = session.createTextMessage(objectMapper.writeValueAsString(message));
                helloMessage.setStringProperty(
                    "_type", "guru.springframework.sfgjms.model.HelloWorldMessage");

                System.out.println("Hello");

                return helloMessage;
              } catch (JsonProcessingException e) {
                throw new JMSException("Error");
              }
            });

    System.out.println(recieveMessage.getBody(String.class));
  }
}
