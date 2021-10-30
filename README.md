## Jenkins MQTT hook

Triggers Jenkins builds on an internal Jenkins instance in response to Github webhook JSON events arriving on an MQTT topic.

The Github webhook was captured and published to MQTT by [github-mqtt-webhooks](https://github.com/tonytw1/github-mqtt-webhooks).

This allows us to capture webhooks on a public callback URL and bring them down into the build environment using a MQTT bridge.

There are probably better ways todo this but this works for us as we are already familiar with MQTT.


### Configuration

In `conf/application.conf` set your MQTT connection and Jenkins API settings.
