package mqtt

import akka.actor.FSM.{CurrentState, SubscribeTransitionCallBack, Transition}
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.sandinh.paho.akka._
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.Results
import play.api.{Configuration, Logger}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MQTTListener @Inject()(configuration: Configuration, ws: WSClient, actorSystem: ActorSystem) {

  val mqttUrl = configuration.getString("mqtt.host").get + ":" + configuration.getString("mqtt.port").get
  val topic = configuration.getString("mqtt.topic").get
  val jenkinsUrl = configuration.getString("jenkins.url").get

  val pubsub: ActorRef = actorSystem.actorOf(Props(classOf[MqttPubSub], PSConfig(brokerUrl = "tcp://" + mqttUrl)))
  val mqttListener = actorSystem.actorOf(Props(new MQTTListeningActor(pubsub, topic, jenkinsUrl, ws)))
}

class MQTTListeningActor(pubsub: ActorRef, topic: String, jenkinsUrl: String, ws: WSClient) extends Actor {

  pubsub ! Subscribe(topic, self)

  def receive: Receive = {
    case SubscribeAck(Subscribe(topic, _, _)) =>
      Logger.info("Subscribed successfully to: " + topic)
    case msg: Message =>
      val message = new String(msg.payload)
      Logger.info("MQTT message received: " + message)

      val json = Json.parse(message)
      val pusher = (json \ "pusher").toOption
      pusher.map { p =>
        Logger.info("Pusher element seen; likely to be a push event")
        val repoName = (json \ "repository" \ "name").toOption
        Logger.info("Repo name: " + repoName)
        repoName.map { rn =>
          triggerBuild(rn.as[String])
        }
      }.getOrElse {
        Logger.info("Not a push event; ignoring")
      }
  }

  def triggerBuild(repoName: String): Future[Int] = {
    Logger.info("Triggering build git repo name: " + repoName)
    val triggerUrl = jenkinsUrl + "/job/" + repoName + "/build"
    Logger.info("POSTing to Jenkins trigger url: " + triggerUrl)
    ws.url(triggerUrl).post(Results.EmptyContent()).map { r =>
      Logger.info("Trigger response: " + r.status)
      r.status
    }
  }

}