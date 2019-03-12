
package controllers

import health.ConnectionState
import javax.inject.Inject
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future

class Application @Inject()(connectionState: ConnectionState) extends Controller {

  def ping = Action.async { request =>
    Future.successful(Ok(Json.toJson("ok")))
  }

  def health = Action.async { request =>
    Future.successful(
      if (connectionState.connected) {
        Logger.info("Health check returned ok")
        Ok(Json.toJson("Connected"))
      } else {
        Logger.warn("Health check returned not connected")
        ServiceUnavailable(Json.toJson("Not connected"))
      }
    )
  }

}