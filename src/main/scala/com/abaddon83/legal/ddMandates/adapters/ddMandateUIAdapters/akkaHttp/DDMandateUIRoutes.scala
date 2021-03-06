package com.abaddon83.legal.ddMandates.adapters.ddMandateUIAdapters.akkaHttp

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server._
import com.abaddon83.legal.ddMandates.adapters.ddMandateUIAdapters.DDMandateUIAdapter
import com.abaddon83.legal.ddMandates.adapters.ddMandateUIAdapters.akkaHttp.messages.{CreateDDMandateRequest, DDMandateJsonSupport, DDMandateView, ErrorDDMandate}
import com.abaddon83.legal.ddMandates.domainModels.DDMandateNotAccepted
import com.abaddon83.legal.ddMandates.ports.{BankAccountPort, DDMandateContractPort, CreditorPort, DDMandateRepositoryPort}
import com.abaddon83.libs.akkaHttp.routes.RouteRejectionHandler

import scala.concurrent.Future
import scala.util.{Failure, Success}


class DDMandateUIRoutes()(
  implicit actorSystem: ActorSystem,
  bankAccountAdapter: BankAccountPort,
  contractAdapter :DDMandateContractPort,
  creditorAdapter : CreditorPort,
  ddMandateRepositoryAdapter : DDMandateRepositoryPort
) extends DDMandateUIAdapter with DDMandateJsonSupport with RouteRejectionHandler{


  override val bankAccountPort: BankAccountPort = bankAccountAdapter
  override val contractPort :DDMandateContractPort = contractAdapter
  override val creditorPort : CreditorPort = creditorAdapter
  override val ddMandateRepositoryPort : DDMandateRepositoryPort = ddMandateRepositoryAdapter


  /*
  override val bankAccountPort: BankAccountPort = bind[BankAccountPort]
  override val contractPort :ContractDDMandatePort = bind[ContractDDMandatePort]
  override val creditorPort : CreditorPort = bind[CreditorPort]
  override val ddMandateRepositoryPort : DDMandateRepositoryPort = bind[DDMandateRepositoryPort]
  */

  val route: Route = {
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
                      case Success(ddMandate) => {complete(DDMandateView(ddMandate))}
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
                        case Success(ddMandate) => complete(DDMandateView(ddMandate))
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
                        case Success(ddMandate) => complete(DDMandateView(ddMandate))
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
                        case Success(ddMandate) => complete(DDMandateView(ddMandate))
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

