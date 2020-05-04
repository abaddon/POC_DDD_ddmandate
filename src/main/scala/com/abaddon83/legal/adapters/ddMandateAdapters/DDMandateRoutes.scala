package com.abaddon83.legal.adapters.ddMandateAdapters

import java.util.UUID

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import com.abaddon83.legal.adapters.ddMandateAdapters.components.{CreateDDMandate, ViewDDMandate}
import com.abaddon83.legal.adapters.ddMandateAdapters.components.ViewDDMandateJsonSupport._
import akka.stream.ActorMaterializer
import com.abaddon83.legal.domainModel.ddMandates.DDMandateNotAccepted
import spray.json.RootJsonFormat

import scala.concurrent.Future
import scala.util.{Failure, Success}

trait Router {
  def route: Route
}

case class DDMandateRoutes(ddMandateAdapter: DDMandateAdapter) extends Router with Directives {


  //lazy val logger = Logging(actorSystem, classOf[App])

  override def route: Route = pathPrefix("ddmandates") {
    implicit val CreateDDMandateJsonFormat: RootJsonFormat[CreateDDMandate] = jsonFormat2(CreateDDMandate)
    pathEndOrSingleSlash {
      get {
        complete {
          "Hello world"
        }
      } ~ post {
        entity(as[CreateDDMandate]) { createDDMandate =>
          val ddMandate: Future[DDMandateNotAccepted] = ddMandateAdapter.createDDMandate(createDDMandate.bankAccountId,createDDMandate.legalEntity)
          onComplete(ddMandate) {
            _ match {
              case Success(ddMandate) =>
                //logger.info("DDMandateCreated")
                complete {
                  ViewDDMandate(ddMandate)
                }
              case Failure(throwable) =>
                //logger.error(s"Failed: ${throwable.getMessage}")
                complete(StatusCodes.InternalServerError, "Failed to query the employees.")
            }
          }
        }
      }
    }
  }
}
