package com.abaddon83.legal.ddMandates.adapters.ddMandateAdapters.akkaHttp

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server._
import com.abaddon83.legal.ddMandates.adapters.ddMandateAdapters.akkaHttp.messages.{CreateDDMandate, DDMandateJsonSupport, ErrorDDMandate, RestViewDDMandate}
import com.abaddon83.legal.ddMandates.domainModels.DDMandateNotAccepted
import com.abaddon83.libs.akkaHttp.routes.{RouteRejectionHandler, Routes}

import scala.concurrent.Future
import scala.util.{Failure, Success}

class DDMandateRoutes(ddMandateAdapter: DDMandateAdapter) extends Routes with DDMandateJsonSupport with RouteRejectionHandler{


  override protected val routes: Route = {
    handleRejections(globalRejectionHandler) {
      extractUri { uri =>
        pathPrefix("ddmandates") {
          concat(
            pathPrefix(JavaUUID) { mandateUUID =>
              concat(
                pathEndOrSingleSlash{
                  get {
                    onComplete(ddMandateAdapter.findByIdDDMandate(mandateUUID)){
                      _ match {
                        case Success(ddMandate) => complete(RestViewDDMandate(ddMandate))
                        case Failure(throwable) => throwable match {
                          case ex: NoSuchElementException => complete(StatusCodes.NotFound, ErrorDDMandate.build(ex, uri.path.toString()))
                          case ex: Exception => complete(StatusCodes.InternalServerError, ErrorDDMandate.build(ex, uri.path.toString()))
                        }
                      }
                    }
                  }
                },
                path("activate"){
                  put {
                    onComplete(ddMandateAdapter.acceptDDMandate(mandateUUID)) {
                      _ match {
                        case Success(ddMandate) => complete(RestViewDDMandate(ddMandate))
                        case Failure(throwable) => throwable match {
                          case ex: NoSuchElementException => complete(StatusCodes.BadRequest, ErrorDDMandate.build(ex, uri.path.toString()))
                          case ex: Exception => complete(StatusCodes.InternalServerError, ErrorDDMandate.build(ex, uri.path.toString()))
                        }
                      }
                    }
                  }
                }
              )
            },
            pathEndOrSingleSlash {
              post {
                entity(as[CreateDDMandate]) { createDDMandate =>
                  val ddMandate: Future[DDMandateNotAccepted] = ddMandateAdapter.createDDMandate(createDDMandate.bankAccountId, createDDMandate.legalEntity)
                  onComplete(ddMandate) {
                    _ match {
                      case Success(ddMandate) => {complete(RestViewDDMandate(ddMandate))}
                      case Failure(throwable) => throwable match {
                        case ex: NoSuchElementException => complete(StatusCodes.BadRequest, ErrorDDMandate.build(ex, uri.path.toString()))
                        case ex: Exception => complete(StatusCodes.InternalServerError, ErrorDDMandate.build(ex, uri.path.toString()))
                      }
                      //logger.error(s"Failed: ${throwable.getMessage}")
                      //complete(StatusCodes.InternalServerError, "Failed to query the employees.")
                    }
                  }
                }
              }
            }
          )
        }
      }
    }
  }
}
