
package controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future

class Application extends Controller {

  def ping = Action.async { request =>
      Future.successful(Ok(Json.toJson("ok")))
  }

}