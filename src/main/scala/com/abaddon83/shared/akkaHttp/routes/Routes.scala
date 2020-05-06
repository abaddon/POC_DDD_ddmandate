package com.abaddon83.shared.akkaHttp.routes

import akka.http.scaladsl.server.Route

trait Routes {
  protected val  routes: Route

  def getRoute():Route ={
    routes
  }
}
