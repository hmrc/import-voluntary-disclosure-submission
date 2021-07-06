import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings

val appName = "import-voluntary-disclosure-submission"

val silencerVersion = "1.7.5"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin)
  .settings(
    majorVersion := 0,
    scalaVersion := "2.12.14",
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
    scalastyleConfig in IntegrationTest := (scalastyleConfig in scalastyle).value,
    scalastyleTarget in IntegrationTest := target.value / "scalastyle-it-results.xml",
    scalastyleFailOnError in IntegrationTest := (scalastyleFailOnError in scalastyle).value,
    (scalastyleFailOnWarning in IntegrationTest) := (scalastyleFailOnWarning in scalastyle).value,
    scalastyleSources in IntegrationTest := (unmanagedSourceDirectories in IntegrationTest).value,
    codeStyleIntegrationTest := scalastyle.in(IntegrationTest).toTask("").value,
    (test in IntegrationTest) := ((test in IntegrationTest) dependsOn codeStyleIntegrationTest).value
  )