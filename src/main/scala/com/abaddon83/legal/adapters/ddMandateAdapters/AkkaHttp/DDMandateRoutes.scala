package com.abaddon83.legal.adapters.ddMandateAdapters.AkkaHttp

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server._
import com.abaddon83.legal.adapters.ddMandateAdapters.AkkaHttp.messages.{CreateDDMandate, DDMandateJsonSupport, ErrorDDMandate, RestViewDDMandate}
import com.abaddon83.legal.domainModel.ddMandates.DDMandateNotAccepted
import com.abaddon83.shared.akkaHttp.routes.{RouteRejectionHandler, Routes}

import scala.concurrent.Future
import scala.util.{Failure, Success}

class DDMandateRoutes(ddMandateAdapter: DDMandateAdapter) extends Routes with DDMandateJsonSupport with RouteRejectionHandler{


  override protected val routes: Route = {
    handleRejections(globalRejectionHandler) {
      pathPrefix("ddmandates") {
        path(JavaUUID) { mandateUUID =>
          get {
            onComplete(ddMandateAdapter.findByIdDDMandate(mandateUUID)){
              _ match {
                case Success(ddMandate) => complete(RestViewDDMandate(ddMandate))
                case Failure(throwable) => throwable match {
                  case ex: NoSuchElementException => complete(StatusCodes.NotFound, ErrorDDMandate.build(ex, "/ddmandates"))
                  case ex: Exception => complete(StatusCodes.InternalServerError, ErrorDDMandate.build(ex, "/ddmandates"))
                }
              }
            }
          } ~ path("activate-command"/JavaUUID){ commandUUID =>
            put {
              onComplete(ddMandateAdapter.accept(mandateUUID, commandUUID)) {
                _ match {
                  case Success(ddMandate) => complete(RestViewDDMandate(ddMandate))
                  case Failure(throwable) => throwable match {
                    case ex: NoSuchElementException => complete(StatusCodes.NotFound, ErrorDDMandate.build(ex, "/ddmandates"))
                    case ex: Exception => complete(StatusCodes.InternalServerError, ErrorDDMandate.build(ex, "/ddmandates"))
                  }
                }
              }
            }
          }
        } ~ pathEndOrSingleSlash {
           post {
            entity(as[CreateDDMandate]) { createDDMandate =>
              val ddMandate: Future[DDMandateNotAccepted] = ddMandateAdapter.createDDMandate(createDDMandate.bankAccountId, createDDMandate.legalEntity)
              onComplete(ddMandate) {
                _ match {
                  case Success(ddMandate) => {complete(RestViewDDMandate(ddMandate))}
                  case Failure(throwable) => throwable match {
                    case ex: NoSuchElementException => complete(StatusCodes.BadRequest, ErrorDDMandate.build(ex, "/ddmandates"))
                    case ex: Exception => complete(StatusCodes.InternalServerError, ErrorDDMandate.build(ex, "/ddmandates"))
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
