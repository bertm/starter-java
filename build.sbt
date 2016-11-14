name := "starter-java"
version := "1.0-SNAPSHOT"

scalaVersion := "2.10.4"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

libraryDependencies ++= Seq(
    javaCore,
    javaJdbc,
    "com.twilio.sdk" % "twilio-java-sdk" % "5.2.1"
)
