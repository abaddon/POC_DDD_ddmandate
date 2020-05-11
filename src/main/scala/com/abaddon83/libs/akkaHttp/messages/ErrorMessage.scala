package com.abaddon83.libs.akkaHttp.messages

case class ErrorMessage(instance: String, message: String, exceptionType: String, errorCode: Int)
