import sbt.Keys._
import sbt._
import sbt.plugins.JvmPlugin

/**
 * Settings that are comment to all the SBT projects
 */
object Common extends AutoPlugin {
  override def trigger = allRequirements
  override def requires: sbt.Plugins = JvmPlugin

  override def projectSettings = Seq(
    organization := "com.acadaca.fakenews",
    version := "1.0-SNAPSHOT",
 //   resolvers += Resolver.typesafeRepo("releases"),
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
    scalacOptions ++= Seq(
      "-encoding",
      "UTF-8", // yes, this is 2 args
      "-target:jvm-1.8",
      "-deprecation",
      "-feature",
      "-unchecked",
      "-Xlint",
      "-Yno-adapted-args",
      "-Ywarn-numeric-widen",
      "-Xfatal-warnings"),
    scalacOptions in Test ++= Seq("-Yrangepos"),
    autoAPIMappings := true)
}

object versions {
  val sparkVersion = "2.1.0"
}

object Settings {
  
  val jvmDependencies =
    Seq(
      "com.netaporter" %% "scala-uri" % "0.4.14",
      "com.typesafe.play" %% "play-slick" % "2.0.2",
      "com.typesafe.slick" %% "slick-codegen" % "3.1.0",
      "mysql" % "mysql-connector-java" % "6.0.5",
      "com.github.t3hnar" %% "scala-bcrypt" % "3.0",
      "commons-validator" % "commons-validator" % "1.4.0",
      "net.ruippeixotog" %% "scala-scraper" % "1.2.0",
      "com.googlecode.owasp-java-html-sanitizer" % "owasp-java-html-sanitizer" % "r239",
      "be.objectify" %% "deadbolt-scala" % "2.5.1",
      "org.apache.spark" %% "spark-core" % versions.sparkVersion,
      "org.apache.spark" %% "spark-sql" % versions.sparkVersion,
      "org.apache.spark" %% "spark-mllib" % versions.sparkVersion,
      "org.apache.spark" %% "spark-streaming" % versions.sparkVersion,
      "org.apache.spark" %% "spark-hive" % versions.sparkVersion,
      "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % "test",
      "org.mockito" % "mockito-core" % "2.7.22")
      
}