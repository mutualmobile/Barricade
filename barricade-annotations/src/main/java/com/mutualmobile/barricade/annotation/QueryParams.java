package com.mutualmobile.barricade.annotation;

/**
 * Created by Vikram on 09/05/19.
 */
public @interface QueryParams {
	Params[] params();
	Response response();
}
