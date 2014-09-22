name := "scala-parsers"

version := "0.2"

scalaVersion := "2.11.2"

crossScalaVersions := Seq("2.11.2", "2.10.4")

description := "A Trifecta inspired parser in Scala."

licenses += ("BSD Simplified", url("https://github.com/ermine-language/ermine-parser/blob/master/LICENSE"))

homepage := Some(url("https://github.com/ermine-language/ermine-parser"))

bintraySettings

bintray.Keys.bintrayOrganization in bintray.Keys.bintray := Some("ermine")

publishMavenStyle := true

scalacOptions ++=
  Seq("-encoding", "UTF-8", "-Yrecursion", "50", "-deprecation",
      "-unchecked", "-Xlint", "-feature",
      "-language:implicitConversions", "-language:higherKinds",
      "-language:existentials", "-language:postfixOps")

parallelExecution := true

javacOptions += "-Xlint"

libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.0.6"

initialCommands in console := "import scalaz._, Scalaz._; import scalaparsers._"
