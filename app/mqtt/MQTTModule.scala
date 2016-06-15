package mqtt

import com.google.inject.AbstractModule
import play.api.Logger

class MQTTModule extends AbstractModule {

  def configure() = {
    Logger.info("Binding MQTTListener for eager startup")
    bind(classOf[MQTTListener]).asEagerSingleton()
  }

}
