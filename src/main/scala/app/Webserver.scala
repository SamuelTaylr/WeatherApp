package com.samuel.weather
package app

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Framing, Sink}
import akka.util.ByteString
import config.TestConfig._

import scala.concurrent.Future
import scala.util.{Failure, Success}
import com.samuel.weather.gateway.WeatherGateway

object Webserver extends App {

  val weatherGateway = new WeatherGateway


  val tester = weatherGateway.callExternalApi().onComplete {
    case Success(response) =>
      println(s"Response status: ${response.status}")

      // Assuming the response is text-based and chunks are separated by newlines
      val delimiter: ByteString = ByteString("\n")
      val source = response.entity.dataBytes
        .via(Framing.delimiter(delimiter, maximumFrameLength = 5000, allowTruncation = true))
        .map(_.utf8String)

      // Consume the source
      source.runWith(Sink.foreach(println)).onComplete {
        case Success(_) => println("Stream processing finished.")
        case Failure(ex) => println(s"Stream processing failed with error: ${ex.getMessage}")
      }

    case Failure(exception) =>
      println(s"Request failed with error: ${exception.getMessage}")
  }



  val route = path("test-call" ) {
    //val test = "https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&hourly=temperature_2m"
    onComplete(weatherGateway.callExternalApi) {
      case Success(response) =>
        println(tester)
        println(response)
        val test = response.entity.toString
        println(test)
        complete(StatusCodes.Accepted, test)
      case Failure(ex) => complete(StatusCodes.InternalServerError, s"Error occurred $ex")
    }
  }

  // Start the server
  Http().newServerAt("localhost", 8080).bind(route)
  println(s"Server online at http://localhost:8080/")
}
