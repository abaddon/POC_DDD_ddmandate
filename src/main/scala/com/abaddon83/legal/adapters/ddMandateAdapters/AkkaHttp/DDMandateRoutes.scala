package com.abaddon83.legal.adapters.ddMandateAdapters.AkkaHttp

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server._
import com.abaddon83.legal.adapters.ddMandateAdapters.AkkaHttp.messages.{CreateDDMandate, DDMandateJsonSupport, ErrorDDMandate, ViewDDMandate}
import com.abaddon83.legal.domainModel.ddMandates.DDMandateNotAccepted
import com.abaddon83.shared.akkaHttp.routes.{RouteRejectionHandler, Routes}

import scala.concurrent.Future
import scala.util.{Failure, Success}

class DDMandateRoutes(ddMandateAdapter: DDMandateAdapter) extends Routes with DDMandateJsonSupport with RouteRejectionHandler{



  override protected val routes: Route = {
    handleRejections(globalRejectionHandler) {
      pathPrefix("ddmandates") {
        pathEndOrSingleSlash {
          get {
            complete {
              "Hello world"
            }
          } ~ post {
            entity(as[CreateDDMandate]) { createDDMandate =>
              val ddMandate: Future[DDMandateNotAccepted] = ddMandateAdapter.createDDMandate(createDDMandate.bankAccountId, createDDMandate.legalEntity)
              onComplete(ddMandate) {
                _ match {
                  case Success(ddMandate) => complete(ViewDDMandate(ddMandate))
                  case Failure(throwable) => throwable match {
                    case t: ArithmeticException => complete(ErrorDDMandate.build(t, "/ddmandates"))
                    case t: Exception => complete(StatusCodes.BadRequest, ErrorDDMandate.build(t, "/ddmandates"))
                  }
                  //logger.error(s"Failed: ${throwable.getMessage}")
                  //complete(StatusCodes.InternalServerError, "Failed to query the employees.")
                }
              }
            }
          }
        }
      }
    }
  }
}
