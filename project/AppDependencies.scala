import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-backend-play-27" % "3.2.0",
    "uk.gov.hmrc" %% "simple-reactivemongo" % "7.31.0-play-27"
  )

  val test = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-27" % "3.2.0" % Test,
    "org.scalatest" %% "scalatest" % "3.2.3" % Test,
    "com.typesafe.play" %% "play-test" % current % Test,
    "org.scalamock" %% "scalamock" % "4.4.0" % Test,
    "com.vladsch.flexmark" % "flexmark-all" % "0.36.8" % "test, it",
    "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % "test, it",
    "org.jsoup" % "jsoup" % "1.13.1" % Test,
    "com.github.tomakehurst" % "wiremock-jre8" % "2.27.2" % "test, it"
  )
}
