package com.mutualmobile.barricade.annotation;

/**
 * Annotations to declare query parameters and response for corresponding query params
 */
public @interface QueryParams {
	Params[] params();
	Response response();
}
