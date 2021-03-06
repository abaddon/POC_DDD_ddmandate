package com.abaddon83.legal.contracts.adapters.contractUIAdapters.akka.akkHttp

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import com.abaddon83.legal.contracts.adapters.contractUIAdapters.ContractUIAdapter
import com.abaddon83.legal.contracts.adapters.contractUIAdapters.akka.akkHttp.messages._
import com.abaddon83.legal.contracts.domainModels.FileRepositories.S3FileRepository
import com.abaddon83.legal.contracts.domainModels.{ContractSigned, ContractUnSigned}
import com.abaddon83.legal.contracts.ports.{ContractRepositoryPort, DDMandatePort, FileDocumentPort}
import com.abaddon83.libs.akkaHttp.routes.RouteRejectionHandler

import scala.concurrent.Future
import scala.util.{Failure, Success}



class ContractUIRoutes()(implicit
                         actorSystem: ActorSystem,
                         ddMandateAdapter : DDMandatePort,
                         contractRepositoryAdapter : ContractRepositoryPort,
                         fileRepositoryAdapter : FileDocumentPort
  ) extends ContractUIAdapter with ContractJsonSupport with RouteRejectionHandler{

  override val ddMandatePort : DDMandatePort = ddMandateAdapter// new DDMandateInternalAdapter //bind[DDMandatePort]
  override val contractRepositoryPort : ContractRepositoryPort =contractRepositoryAdapter //  FakeContractRepositoryAdapter //bind[ContractRepositoryPort]
  override val fileRepositoryPort : FileDocumentPort = fileRepositoryAdapter// new FakeFileRepositoryAdapter //bind[FileRepositoryPort]



  val routes: Route = {
    extractUri { uri =>
      pathPrefix("contracts") {
        handleRejections(globalRejectionHandler) {
          concat(
            pathEndOrSingleSlash {
              post { // POST /contracts
                entity(as[CreateContractRequest]) { createContractRequest =>
                  val contract: Future[ContractUnSigned] = createContract(createContractRequest.contractType,createContractRequest.reference)
                  onComplete(contract) {
                    _ match {
                      case Success(contract) => {complete(ContractView(contract))}
                      case Failure(throwable) => throwable match {
                        case ex: NoSuchElementException => complete(StatusCodes.BadRequest, ErrorContract.build(ex, uri.path.toString()))
                        case ex: Exception => complete(StatusCodes.InternalServerError, ErrorContract.build(ex, uri.path.toString()))
                      }
                    }
                  }
                }
              }
            },
            pathPrefix(JavaUUID) { contractUUID =>
              concat(
                pathEndOrSingleSlash {
                  get { // GET /contracts/UUID
                    onComplete(findByIdContract(contractUUID)){
                      _ match {
                        case Success(contract) => complete(ContractView(contract))
                        case Failure(throwable) => throwable match {
                          case ex: NoSuchElementException => complete(StatusCodes.NotFound, ErrorContract.build(ex, uri.path.toString()))
                          case ex: Exception => complete(StatusCodes.InternalServerError, ErrorContract.build(ex, uri.path.toString()))
                        }
                      }
                    }
                  }
                },
                path("sign") {
                  put { // PUT /contracts/UUID/sign
                    entity(as[SignContractRequest]) { signContractRequest =>
                      val contract: Future[ContractSigned] = signContract(contractUUID, S3FileRepository("",""),signContractRequest.signatureDate)
                      onComplete(contract) {
                        _ match {
                          case Success(contract) => complete(ContractView(contract))
                          case Failure(throwable) => throwable match {
                            case ex: NoSuchElementException => complete(StatusCodes.BadRequest, ErrorContract.build(ex, uri.path.toString()))
                            case ex: ClassCastException => complete(StatusCodes.BadRequest, ErrorContract.build(ex, uri.path.toString()))
                            case ex: Exception => complete(StatusCodes.InternalServerError, ErrorContract.build(ex, uri.path.toString()))
                          }
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
