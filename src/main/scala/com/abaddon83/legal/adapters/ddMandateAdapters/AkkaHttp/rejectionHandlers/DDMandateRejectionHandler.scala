package com.abaddon83.legal.adapters.ddMandateAdapters.AkkaHttp.rejectionHandlers

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.{RejectionHandler, _}
import Directives._

trait DDMandateRejectionHandler {

  implicit def DDMandateRejectionHandler =
    RejectionHandler.newBuilder()
      .handle {
        case ValidationRejection(msg, _) =>
          complete((InternalServerError, "That wasn't valid! " + msg))
      }
      .handleAll[MethodRejection] { methodRejections =>
        val names = methodRejections.map(_.supported.name)
        complete((MethodNotAllowed, s"Can't do that! Supported: ${names mkString " or "}!"))
      }
      .handleNotFound {
        extractUnmatchedPath { p =>
          complete((NotFound, s"The path you requested [${p}] does not exist."))
        }
      }
      .result()
}
