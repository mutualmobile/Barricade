package com.mutualmobile.barricade.annotation;

/**
 * Support for dynamic url path
 * @path will appended after endpoint.
 * for eg. if your retrofit url is user/{path}/class
 * you barricade code will be endpoint = user , UrlPath{ path= {expected value}/class}
 */
public @interface UrlPath {
	String path();
	Response[] responses();
}
