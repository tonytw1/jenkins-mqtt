package mqtt

import javax.inject.Inject

import akka.actor.{Actor, ActorRef, Props}
import com.google.inject.Singleton
import com.sandinh.paho.akka.MqttPubSub
import com.sandinh.paho.akka.MqttPubSub._
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.Results
import play.api.{Configuration, Logger}
import play.libs.Akka

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class MQTTListener @Inject() (configuration: Configuration, ws: WSClient) {

  val mqttUrl =  configuration.getString("mqtt.host").get + ":" +  configuration.getString("mqtt.port").get
  val topic =  configuration.getString("mqtt.topic").get
  val jenkinsUrl =   configuration.getString("jenkins.url").get

  {
    Logger.info("Connecting websocket to channel: " + topic)
    val pubsub: ActorRef = Akka.system.actorOf(Props(classOf[MqttPubSub], PSConfig(brokerUrl = "tcp://" + mqttUrl)))
    val mqttListener = Akka.system.actorOf(Props(new MQTTListeningActor(pubsub, topic, jenkinsUrl, ws)))
  }

}

class MQTTListeningActor(pubsub: ActorRef, topic: String, jenkinsUrl: String, ws: WSClient) extends Actor {

  pubsub ! Subscribe(topic, self)

  def receive: Receive = {
    case SubscribeAck(Subscribe(topic, _, _)) => {
      Logger.info("Subscribed successfully to: " + topic)
    }
    case msg: Message => {
      val message: String = new String(msg.payload)
      Logger.info("MQTT message received: " + message)

      val json = Json.parse(message)
      val pusher: Option[JsValue] = (json \ "pusher").toOption
      pusher.map { p =>
        Logger.info("Pusher element seen; likely to be a push event")
        val repoName: Option[JsValue] = (json \ "repository" \ "name").toOption
        Logger.info("Repo name: " + repoName)
        repoName.map { rn =>
          Logger.info("Triggering build git repo name: " + rn)

          val triggerUrl = jenkinsUrl + "/job/" + rn.as[String] + "/build"
          Logger.info("POSTing to Jenkins trigger url: " + triggerUrl)
          ws.url(triggerUrl).post(Results.EmptyContent()).map { r =>
            Logger.info("Trigger response: " + r.status)
          }
        }
      }
    }
  }

}