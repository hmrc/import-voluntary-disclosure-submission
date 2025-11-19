import uk.gov.hmrc.DefaultBuildSettings

val appName = "import-voluntary-disclosure-submission"

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "3.3.7"
ThisBuild / scalacOptions += "-Wconf:msg=Flag.*repeatedly:s"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin) // Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(
    PlayKeys.playDefaultPort := 7951,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    // required in place of silencer plugin
    scalacOptions += "-Wconf:src=routes/.*:s"
  )
  .settings(
    // To resolve a bug with version 2.x.x of the scoverage plugin - https://github.com/sbt/sbt/issues/6997
    libraryDependencySchemes ++= Seq("org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always)
  )
  .settings(CodeCoverageSettings.settings *)
  .configs(Test)

val codeStyleIntegrationTest = taskKey[Unit]("enforce code style then integration test")

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test") // the "test->test" allows reusing test code and test dependencies
  .settings(DefaultBuildSettings.itSettings())
  .settings(libraryDependencies ++= AppDependencies.test)
  .settings(
    inConfig(Test)(ScalastylePlugin.rawScalastyleSettings()) ++ Seq(
      Test / scalastyleConfig          := (scalastyle / scalastyleConfig).value,
      Test / scalastyleTarget          := target.value / "scalastyle-it-results.xml",
      Test / scalastyleFailOnError     := (scalastyle / scalastyleFailOnError).value,
      (Test / scalastyleFailOnWarning) := (scalastyle / scalastyleFailOnWarning).value,
      Test / scalastyleSources         := (Test / unmanagedSourceDirectories).value,
      codeStyleIntegrationTest         := (Test / scalastyle).toTask("").value,
      (Test / test)                    := ((Test / test) dependsOn codeStyleIntegrationTest).value
    )
  )
