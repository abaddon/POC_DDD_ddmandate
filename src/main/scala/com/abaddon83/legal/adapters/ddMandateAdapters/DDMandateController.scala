/*
package com.abaddon83.legal.adapters.ddMandateAdapters

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directives, Route}
import com.abaddon83.legal.adapters.ddMandateAdapters.DDMandateJsonProtocol.{jsonFormat2, jsonFormat4, jsonFormat5, jsonFormat6}
import com.abaddon83.legal.adapters.ddMandateAdapters.components.{CreateDDMandate, RestCreditor, ViewDDMandate, RestDebtor}
import com.abaddon83.legal.services.{ContractService, DDMandateService}
import spray.json.DefaultJsonProtocol
import com.abaddon83.legal.adapters.ddMandateAdapters.utils.DateMarshalling._
import com.abaddon83.legal.adapters.ddMandateAdapters.utils.UUIDMarshalling._
import com.abaddon83.legal.domainModel.ddMandates.DDMandateNotAccepted

import scala.concurrent.Future
import scala.util.{Failure, Success}

object DDMandateJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val restCreditorFormat = jsonFormat4(RestCreditor.apply)
  implicit val restDebtorFormat = jsonFormat6(RestDebtor.apply)
  implicit val restDDMandateFormat = jsonFormat5(ViewDDMandate.apply)

}


class DDMandateController(contractService: ContractService, ddMandateService: DDMandateService){

  import DDMandateJsonProtocol._
  implicit def actorSystem: ActorSystem

  lazy val ddMandateAdapter: DDMandateAdapter=new DDMandateAdapter(ddMandateService,contractService)

  lazy val logger = Logging(actorSystem, classOf[DDMandateController])

  lazy val ddMandateRoutes: Route = pathPrefix("ddmandate") {
    /*get {
      path(Segment) { id =>
        onComplete(ddMandateService.) {
          _ match {
            case Success(employee) =>
              logger.info(s"Got the employee records given the employee id ${id}")
              complete(StatusCodes.OK, employee)
            case Failure(throwable) =>
              logger.error(s"Failed to get the employee record given the employee id ${id}")
              throwable match {
                case e: EmployeeNotFoundException => complete(StatusCodes.NotFound, "No employee found")
                case e: DubiousEmployeeRecordsException => complete(StatusCodes.NotFound, "Dubious records found.")
                case _ => complete(StatusCodes.InternalServerError, "Failed to get the employees.")
              }
          }
        }
      }
    }*/ post {
      path("") {
        entity(as[CreateDDMandate]) { request =>
          val ddMandate: Future[DDMandateNotAccepted] = ddMandateAdapter.createDDMandate(request.bankAccountId,request.legalEntity)
          onComplete(ddMandate) {
            _ match {
              case Success(ddMandate) =>
                logger.info("DDMandateCreated")
                complete {
                  ViewDDMandate.apply(ddMandate)
                }
              case Failure(throwable) =>
                logger.error("Failed to get the employees with the given query condition.")
                complete(StatusCodes.InternalServerError, "Failed to query the employees.")
            }
          }
        }
      }
    }
  }

}
*/
