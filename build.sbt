//scalaVersion := "0.1.1-20170412-3a9deec-NIGHTLY"
//
//scalaOrganization := "ch.epfl.lamp"
//
//scalaBinaryVersion := "2.11"
//
//ivyScala ~= (_ map (_ copy (overrideScalaVersion = false)))
//
//libraryDependencies += "ch.epfl.lamp" % "dotty_2.11" % scalaVersion.value % "scala-tool"
//
//scalaCompilerBridgeSource := ("ch.epfl.lamp" % "dotty-sbt-bridge" % scalaVersion.value % "component").sources()

lazy val root = (project in file("."))
  .settings(
    name := "dotty-example-project",
    description := "Example sbt project that compiles using Dotty",
    version := "0.1",

//    scalaVersion := "0.1.1-bin-20170502-df22149-NIGHTLY"
    scalaVersion := "0.1.1-bin-20170523-83745f3-NIGHTLY"
  ).enablePlugins(DottyPlugin)
