package com.mutualmobile.barricade.annotation;

/**
 * Annotations for Post Request body and corresponding response
 */
public @interface RequestJson {
	String body();
	Response response();
}
