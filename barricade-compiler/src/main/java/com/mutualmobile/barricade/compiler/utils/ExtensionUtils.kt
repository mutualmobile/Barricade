package com.mutualmobile.barricade.compiler.utils

import java.io.OutputStream

infix fun OutputStream.writeToFile(str: String) {
    write(str.toByteArray())
}
