package com.abaddon83.legal.ddMandates.adapters.ddMandateAdapters.akkaHttp

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server._
import com.abaddon83.legal.ddMandates.adapters.ddMandateAdapters.akkaHttp.messages.{CreateDDMandateRequest, DDMandateJsonSupport, ErrorDDMandate, RestViewDDMandate}
import com.abaddon83.legal.ddMandates.domainModels.DDMandateNotAccepted
import com.abaddon83.libs.akkaHttp.routes.RouteRejectionHandler

import scala.concurrent.Future
import scala.util.{Failure, Success}


trait DDMandateRoutes extends DDMandateAdapter with DDMandateJsonSupport with RouteRejectionHandler{

  val ddMandateRoutes: Route = {
    extractUri { uri =>
      pathPrefix("ddmandates") {
        handleRejections(globalRejectionHandler) {
          concat(
            pathEndOrSingleSlash {
              post { // POST /ddmandates
                entity(as[CreateDDMandateRequest]) { request =>
                  val ddMandate: Future[DDMandateNotAccepted] = createDDMandate(request.bankAccountId, request.legalEntity)
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
            },
            pathPrefix(JavaUUID) { mandateUUID =>
              concat(
                pathEndOrSingleSlash{
                  get { // GET /ddmandates/UUID
                    onComplete(findByIdDDMandate(mandateUUID)){
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
                  put { // PUT /ddmandates/UUID/activate
                    onComplete(acceptDDMandate(mandateUUID)) {
                      _ match {
                        case Success(ddMandate) => complete(RestViewDDMandate(ddMandate))
                        case Failure(throwable) => throwable match {
                          case ex: NoSuchElementException => complete(StatusCodes.BadRequest, ErrorDDMandate.build(ex, uri.path.toString()))
                          case ex: ClassCastException => complete(StatusCodes.BadRequest, ErrorDDMandate.build(ex, uri.path.toString()))
                          case ex: Exception => complete(StatusCodes.InternalServerError, ErrorDDMandate.build(ex, uri.path.toString()))
                          //case x =>
                        }
                      }
                    }
                  }
                },
                path("cancel"){
                  put { // PUT /ddmandates/UUID/cancel
                    onComplete(cancelDDMandate(mandateUUID)) {
                      _ match {
                        case Success(ddMandate) => complete(RestViewDDMandate(ddMandate))
                        case Failure(throwable) => throwable match {
                          case ex: NoSuchElementException => complete(StatusCodes.BadRequest, ErrorDDMandate.build(ex, uri.path.toString()))
                          case ex: ClassCastException => complete(StatusCodes.BadRequest, ErrorDDMandate.build(ex, uri.path.toString()))
                          case ex: Exception => complete(StatusCodes.InternalServerError, ErrorDDMandate.build(ex, uri.path.toString()))
                        }
                      }
                    }
                  }
                }
              )
            }
          )
        }
      }
    }
  }

}

