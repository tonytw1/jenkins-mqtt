
package controllers

import mqtt.MQTTListener
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future

object Application extends Controller {

  val mqttListener = MQTTListener

  def ping = Action.async { request =>
      Future.successful(Ok(Json.toJson("ok")))
  }

}