# Barricade

[![Build Status](https://travis-ci.org/mutualmobile/Barricade.svg)](https://travis-ci.org/mutualmobile/Barricade)
[![Version](https://api.bintray.com/packages/mutualmobile/Android/barricade/images/download.svg)](https://bintray.com/mutualmobile/Android/barricade)
[![Javadoc](https://javadoc-emblem.rhcloud.com/doc/com.mutualmobile/barricade/badge.svg)](http://www.javadoc.io/doc/com.mutualmobile/barricade)

Barricade is a tool for running a local server running inside the app itself. It uses OkHttp network interceptors to return a response from a pre-defined set of results. The response to be returned can be configured in run time.

Most other local server implementations only support a single response per request, but Barricade supports multiple responses per request. This allows us to present the user with an interface for modifying which response will be returned for a request at runtime.

<p align="center">
<img src="ReadmeResources/barricade-realtime-config.png") alt="Example App"/>
</p>