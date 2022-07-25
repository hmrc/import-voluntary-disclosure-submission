import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings

val appName = "import-voluntary-disclosure-submission"

val silencerVersion = "1.7.9"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin) //Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(
    majorVersion := 0,
    scalaVersion := "2.12.15",
    PlayKeys.playDefaultPort := 7951,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    // ***************
    // Use the silencer plugin to suppress warnings
    scalacOptions += "-P:silencer:pathFilters=routes",
    libraryDependencies ++= Seq(
      compilerPlugin("com.github.ghik" % "silencer-plugin" % silencerVersion cross CrossVersion.full),
      "com.github.ghik" % "silencer-lib" % silencerVersion % Provided cross CrossVersion.full
    )
    // ***************
  )
  .settings(publishingSettings: _*)
  .configs(IntegrationTest)
  .settings(integrationTestSettings(): _*)
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(CodeCoverageSettings.settings: _*)

  val codeStyleIntegrationTest = taskKey[Unit]("enforce code style then integration test")
  Project.inConfig(IntegrationTest)(ScalastylePlugin.rawScalastyleSettings()) ++
  Seq(
    IntegrationTest / scalastyleConfig := (scalastyle / scalastyleConfig).value,
    IntegrationTest / scalastyleTarget := target.value / "scalastyle-it-results.xml",
    IntegrationTest / scalastyleFailOnError := (scalastyle / scalastyleFailOnError).value,
    (IntegrationTest / scalastyleFailOnWarning) := (scalastyle / scalastyleFailOnWarning).value,
    IntegrationTest / scalastyleSources := (IntegrationTest / unmanagedSourceDirectories).value,
    codeStyleIntegrationTest := (IntegrationTest / scalastyle).toTask("").value,
    (IntegrationTest / test) := ((IntegrationTest / test) dependsOn codeStyleIntegrationTest).value
  )