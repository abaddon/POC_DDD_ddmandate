package com.abaddon83.libs.akkaHttp.routes

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.ExceptionHandler
import com.abaddon83.legal.ddMandates.adapters.ddMandateControllerAdapters.akka.http.messages.ErrorDDMandate
import com.abaddon83.libs.akkaHttp.messages.GenericJsonSupport

trait RouteExceptionHandling extends GenericJsonSupport{

  protected val globalExceptionHandler = ExceptionHandler {
    case e: Throwable =>
      extractUri { uri =>
        complete(InternalServerError -> ErrorDDMandate.build(e,uri.toString()))
      }
  }
}