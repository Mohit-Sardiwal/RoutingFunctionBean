package com.example.demo.functions;

import java.util.function.Function;

import org.springframework.context.annotation.Bean;

public class FunctionBeans {

  @Bean
  public Function<String,String> uppercase(){
    return new Uppercase();
  }

  @Bean
  public Function<String,String> lowercase(){
    return new Lowercase();
  }
}
