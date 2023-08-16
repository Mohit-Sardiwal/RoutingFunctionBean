package com.example.demo.functions;

import java.util.function.Function;

public class Lowercase implements Function<String, String> {
  @Override
  public String apply(String s) {
    return s.toLowerCase();
  }
}
