package com.samuel.weather
package gateway

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import config.TestConfig._
import scala.concurrent.Future


class WeatherGateway() {

  def callExternalApi(): Future[HttpResponse] = {
    val test = "https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&hourly=temperature_2m"

    Http().singleRequest(HttpRequest(uri = test))
  }
}
