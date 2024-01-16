package com.samuel.weather
package config

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

object TestConfig {
  implicit val system = ActorSystem("WeatherSystem")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
}
