name := "jenkins-mqtt"

version := "1.0"

lazy val `jenkins-mqtt` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(ws)

libraryDependencies ++= Seq("com.sandinh" %% "paho-akka" % "1.2.0")

libraryDependencies += specs2 % Test

maintainer in Linux := "Tony McCrae <tony@eelpieconsulting.co.uk>"

packageSummary in Linux := "Jenkins MQTT"

packageDescription := "Trigger Jenkins from MQTT"

import com.typesafe.sbt.packager.archetypes.ServerLoader

serverLoading in Debian:= ServerLoader.Systemd

