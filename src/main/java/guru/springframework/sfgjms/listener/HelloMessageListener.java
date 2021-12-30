package guru.springframework.sfgjms.listener;

import guru.springframework.sfgjms.config.JmsConfig;
import guru.springframework.sfgjms.model.HelloWorldMessage;
import java.util.UUID;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class HelloMessageListener {
  private final JmsTemplate jmsTemplate;

  public HelloMessageListener(JmsTemplate jmsTemplate) {
    this.jmsTemplate = jmsTemplate;
  }

  @JmsListener(destination = "my-hello-world")
  public void listen(
      @Payload HelloWorldMessage helloWorldMessage,
      @Headers MessageHeaders headers,
      Message message) {}

  @JmsListener(destination = JmsConfig.MY_SEND_RCV_QUEUE)
  public void listenAndRecieve(
      @Payload HelloWorldMessage helloWorldMessage,
      @Headers MessageHeaders headers,
      Message message)
      throws JMSException {

    HelloWorldMessage reply =
        HelloWorldMessage.builder().id(UUID.randomUUID()).message(" World!").build();

    jmsTemplate.convertAndSend(message.getJMSReplyTo(), reply);
  }
}
