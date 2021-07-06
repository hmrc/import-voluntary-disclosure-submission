import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-backend-play-27" % "5.6.0",
    "uk.gov.hmrc" %% "simple-reactivemongo" % "8.0.0-play-27"
  )

  val test = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-27" % "5.6.0" % Test,
    "org.scalatest" %% "scalatest" % "3.2.3" % Test,
    "org.pegdown" % "pegdown" % "1.6.0" % "test, it",
    "com.typesafe.play" %% "play-test" % current % Test,
    "org.scalamock" %% "scalamock" % "5.1.0" % Test,
    "com.vladsch.flexmark" % "flexmark-all" % "0.36.8" % "test, it",
    "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % "test, it",
    "org.jsoup" % "jsoup" % "1.13.1" % Test,
    "com.github.tomakehurst" % "wiremock-jre8" % "2.27.2" % "test, it"
  )
}
