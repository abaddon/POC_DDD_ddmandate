package com.abaddon83.libs.akkaHttp.routes


import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.headers.`Content-Type`
import akka.http.scaladsl.server.{MalformedRequestContentRejection, MethodRejection, RejectionHandler, ValidationRejection}
import com.abaddon83.legal.ddMandates.adapters.ddMandateUIAdapters.akkaHttp.messages.ErrorDDMandate
import com.abaddon83.libs.akkaHttp.messages.GenericJsonSupport

trait RouteRejectionHandler extends GenericJsonSupport{


  val globalRejectionHandler = RejectionHandler.newBuilder()
    .handle {
      case ValidationRejection(msg, e) =>
        complete(BadRequest, List(`Content-Type`(`application/json`)),ErrorDDMandate.build(e.get,"/ValidationRejection"))
      case MalformedRequestContentRejection(msg,e) =>
        complete(BadRequest, List(`Content-Type`(`application/json`)), ErrorDDMandate.build(e,"/MalformedRequestContentRejection"))
    }
    .handleAll[MethodRejection] { methodRejections =>
      val names = methodRejections.map(_.supported.name).toString()
      complete(MethodNotAllowed, List(`Content-Type`(`application/json`)), ErrorDDMandate.build(s"Can't do that! Supported: ${names mkString " or "}!",names))
    }
    .handleNotFound {
      extractUnmatchedPath { p =>
        complete(NotFound, ErrorDDMandate.build("Resource Missing",p.toString()))
      }
    }
    .result()

  /*val myRejectionHandler =
    RejectionHandler.default
      .mapRejectionResponse {
        case res @ HttpResponse(_, _, ent: HttpEntity.Strict, _) =>
          // since all Akka default rejection responses are Strict this will handle all rejections
          val message = ent.data.utf8String.replaceAll("\"", """\"""")

          // we copy the response in order to keep all headers and status code, wrapping the message as hand rolled JSON
          // you could the entity using your favourite marshalling library (e.g. spray json or anything else)
          //res.copy(entity = HttpEntity(ContentTypes.`application/json`, s"""{"rejection": "$message"}"""))

          res.copy(entity = HttpEntity(ContentTypes.`application/json`, ErrorDDMandate.build("URL Missing",message)))



        case x => x // pass through all other types of responses
      }
  */
}
