## Jenkins MQTT hook

Triggers builds on an internal Jenkins instance in response to Github webhook JSON events arriving on an MQTT channel.

This allows us to capture webhooks on a public callback URL and bring them down into the bridge environment using a MQTT bridge.
There are probably better ways todo this but it works for us.



