package com.example.demo.controller.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.example.demo.controller.router.MyRouter;


@Component("apiGatewayHandler")
public class ApiGatewayHandler<T> implements Function<APIGatewayProxyRequestEvent, Message<T>> {

  private MyRouter<T> myRouter;

  @Autowired
  public ApiGatewayHandler(MyRouter<T> myRouter) {
    this.myRouter = myRouter;
  }

  @Override
  public Message<T> apply(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent) {
    Message<T> apiGwMessage = (Message<T>) MessageBuilder.withPayload(apiGatewayProxyRequestEvent).build();
    Message<T> object = myRouter.apply(apiGwMessage);
    T response = object.getPayload();
    Map<String, Object> messageHeaders = new HashMap<>();

      messageHeaders.put("Access-Control-Allow-Origin", "access_control_allow_origin");

    GenericMessage<T> messageResponse = new GenericMessage<>(response, messageHeaders);

    return messageResponse;
  }
  

}
