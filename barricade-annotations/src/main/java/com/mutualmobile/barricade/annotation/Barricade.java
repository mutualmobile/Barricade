package com.mutualmobile.barricade.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.HashMap;

import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Annotation to configure Barricade for an endpoint.
 */
@Documented @Retention(CLASS) @Target(ElementType.METHOD) public @interface Barricade {
  String endpoint() default "";
  UrlPath[] path() default {};
  QueryParams[] queryParams() default {};
  RequestJson[] requestJson() default {};
  Response[] responses() default {};
}
