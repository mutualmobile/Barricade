package com.mutualmobile.barricade.annotation

/**
 * Annotation to configure Barricade for an endpoint.
 */
@Retention
@Target(AnnotationTarget.FUNCTION)
annotation class Barricade(
    val endpoint: String = "",
    val responses: Array<Response> = []
)
