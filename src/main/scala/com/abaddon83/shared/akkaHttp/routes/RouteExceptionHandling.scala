package com.abaddon83.shared.akkaHttp.routes

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.ExceptionHandler
import com.abaddon83.legal.adapters.ddMandateAdapters.AkkaHttp.messages.ErrorDDMandate
import com.abaddon83.shared.akkaHttp.messages.GenericJsonSupport

trait RouteExceptionHandling extends GenericJsonSupport{

  protected def globalExceptionHandler = ExceptionHandler {
    case e: Throwable =>
      extractUri { uri =>
        complete(InternalServerError -> ErrorDDMandate.build(e,uri.toString()))
      }
  }
}