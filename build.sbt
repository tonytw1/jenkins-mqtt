name := "jenkins-mqtt"

version := "1.0"

lazy val `jenkins-mqtt` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.12"

libraryDependencies ++= Seq(ws)
libraryDependencies ++= Seq("com.sandinh" %% "paho-akka" % "1.4.0")
libraryDependencies += specs2 % Test

enablePlugins(DockerPlugin)

import com.typesafe.sbt.packager.docker._
dockerBaseImage := "openjdk:11-jre"
