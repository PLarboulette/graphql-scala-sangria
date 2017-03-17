name := "play-scala"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "org.sangria-graphql" %% "sangria" % "1.1.0",
  "org.sangria-graphql" %% "sangria-relay" % "1.0.0",
  "org.sangria-graphql" %% "sangria-play-json" % "1.0.0",
  "org.sangria-graphql" %% "sangria-circe" % "1.0.0",
  "org.mongodb.scala" %% "mongo-scala-driver" % "1.0.1",
  "org.sangria-graphql" %% "sangria-monix" % "1.0.0"
)

