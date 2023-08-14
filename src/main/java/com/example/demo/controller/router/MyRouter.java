package com.example.demo.controller.router;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.function.context.config.RoutingFunction;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;


@Component
public class MyRouter<T> implements UnaryOperator<Message<T>> {

  private RoutingFunction routingFunction;

  @Autowired
  public MyRouter(RoutingFunction routingFunction) {
    this.routingFunction = routingFunction;
  }

  @Override
  public Message<T> apply(Message<T> object) {

    String springCloudFunctions = System.getenv("spring_cloud_function_order");
      return startRouting(object,springCloudFunctions);

  }

  private Message<T> startRouting(Message<T> object,String springCloudFunctions) {
    T incomingAWSEvent = object.getPayload();
    List<String> functions =
      Arrays.asList(springCloudFunctions.split(Pattern.quote("|")));
    Iterator<String> functionsItr = functions.iterator();
    while (functionsItr.hasNext()) {
      String function = functionsItr.next();
      try {
        Message<T> functionMessage = MessageBuilder
          .withPayload(object.getPayload())
          .setHeader("spring.cloud.function.definition", function)
          .setHeader(MessageHeaders.CONTENT_TYPE, "text/plain")
          .copyHeadersIfAbsent(object.getHeaders())
          .build();
        Object payload = routingFunction.apply(functionMessage);
        object = getPayloadMessage(object, payload);
        if (!functionsItr.hasNext()) {
          return object;
        }
      } catch (Exception e) {

      }
    }
    return null;
  }

  private Message<T> getPayloadMessage(Message<T> object, Object payload) {
    if (payload != null) {
      if (payload instanceof Message) {
        object = MessageBuilder.fromMessage((Message<T>) payload).build();
      } else {
        object = (Message<T>) MessageBuilder.withPayload(payload).build();
      }
     }
    return object;
  }

  private boolean isAPIGWEvent(T incomingAWSEvent) {
    return incomingAWSEvent instanceof APIGatewayProxyRequestEvent;
  }


}
