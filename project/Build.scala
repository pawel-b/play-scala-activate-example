import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName = "tasks"
  val appVersion = "1.0"

  val customResolvers = Seq(
    "fwbrasil.net" at "http://fwbrasil.net/maven/")

  val appDependencies = Seq(
    jdbc)

  def customLessEntryPoints(base: File): PathFinder = (
    (base / "app" / "assets" / "stylesheets" / "bootstrap" * "bootstrap.less") +++
    (base / "app" / "assets" / "stylesheets" / "bootstrap" * "responsive.less") +++
    (base / "app" / "assets" / "stylesheets" * "*.less"))

  val main = play.Project(appName, appVersion, appDependencies).settings(
    lessEntryPoints <<= baseDirectory(customLessEntryPoints),
    libraryDependencies += "net.fwbrasil" %% "activate-core" % "1.3",
    libraryDependencies += "net.fwbrasil" %% "activate-play" % "1.3",
    libraryDependencies += "net.fwbrasil" %% "activate-jdbc" % "1.3",
    libraryDependencies += "org.clapper" % "grizzled-scala_2.10" % "1.1.4",
    libraryDependencies += "org.clapper" % "grizzled-slf4j_2.10" % "1.0.1",
    libraryDependencies += "joda-time" % "joda-time" % "2.1",
    resolvers ++= customResolvers,
    Keys.fork in Test := false)

}
            
