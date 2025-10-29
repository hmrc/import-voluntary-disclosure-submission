import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  val bootstrapVersion = "10.3.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "bootstrap-backend-play-30" % bootstrapVersion
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-30" % bootstrapVersion,
    "org.scalatest"          %% "scalatest"              % "3.2.15",
    "org.pegdown"             % "pegdown"                % "1.6.0",
    "org.playframework"      %% "play-test"              % current,
    "org.scalamock"          %% "scalamock"              % "5.2.0",
    "org.scalatestplus.play" %% "scalatestplus-play"     % "5.1.0",
    "org.jsoup"               % "jsoup"                  % "1.15.4"
  ).map(_ % Test)
}
