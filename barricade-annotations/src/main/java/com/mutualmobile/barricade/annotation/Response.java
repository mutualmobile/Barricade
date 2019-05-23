package com.mutualmobile.barricade.annotation;

/**
 * Annotation to declare a response for an endpoint. Each endpoint will typically have many of
 * these. It has sensible defaults for most params.
 */
public @interface Response {

  String fileName();

  int statusCode() default 200;

  String type() default "application/json";

  boolean isDefault() default false;
}
