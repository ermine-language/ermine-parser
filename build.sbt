ThisBuild / version := "0.2.3"
// ThisBuild / scalaVersion := "2.11.12"
ThisBuild / scalaVersion := "2.13.0"

val scalazVersion = "7.2.28"
lazy val root = (project in file("."))
  .settings(
    name := "scala-parsers",
    description := "A Trifecta inspired parser in Scala.",
    licenses += ("BSD Simplified", url("https://github.com/ermine-language/ermine-parser/blob/master/LICENSE")),
    homepage := Some(url("https://github.com/ermine-language/ermine-parser")),
    bintrayOrganization := Some("ermine"),
    ThisBuild / bintrayReleaseOnPublish := false,
    publishMavenStyle := true,
    scalacOptions ++=
      Seq("-encoding", "UTF-8", "-Yrecursion", "50", "-deprecation",
          "-unchecked", "-Xlint", "-feature",
          "-language:implicitConversions", "-language:higherKinds",
          "-language:existentials", "-language:postfixOps"),
          
    // for kind-projector
    resolvers += Resolver.bintrayRepo("non", "maven"),
    addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3"),

    parallelExecution := true,
    javacOptions += "-Xlint",
    libraryDependencies ++= Seq(
      "org.scalaz" %% "scalaz-core" % scalazVersion,
      // "org.scalaz" %% "scalaz-scalacheck-binding" % scalazVersion % "test"
    ),
    console / initialCommands := "import scalaz._, Scalaz._; import scalaparsers._",
  )
