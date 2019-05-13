package com.mutualmobile.barricade.annotation;

/**
 * Annotation to declare a params of API. It has sensible defaults for most params.
 */
public @interface Params {
  String name();
  String value();
}
