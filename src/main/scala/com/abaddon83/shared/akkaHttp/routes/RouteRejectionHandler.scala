package com.abaddon83.shared.akkaHttp.routes

import akka.http.scaladsl.model.StatusCodes.{InternalServerError, MethodNotAllowed, NotFound}
import akka.http.scaladsl.server.{MethodRejection, RejectionHandler, ValidationRejection}
import com.abaddon83.legal.adapters.ddMandateAdapters.AkkaHttp.messages.ErrorDDMandate
import com.abaddon83.shared.akkaHttp.messages.GenericJsonSupport

trait RouteRejectionHandler extends GenericJsonSupport{

  protected def globalRejectionHandler = RejectionHandler.newBuilder()
    .handle {
      case ValidationRejection(msg, e) =>
        complete(InternalServerError,ErrorDDMandate.build(e.get,"/"))
    }
    .handleAll[MethodRejection] { methodRejections =>
      val names = methodRejections.map(_.supported.name).toString()
      complete(MethodNotAllowed, ErrorDDMandate.build(s"Can't do that! Supported: ${names mkString " or "}!",names))
    }
    .handleNotFound {
      extractUnmatchedPath { p =>
        complete(NotFound, ErrorDDMandate.build("URL Missing",p.toString()))
      }
    }
    .result()
}
