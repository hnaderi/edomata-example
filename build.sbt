// https://typelevel.org/sbt-typelevel/faq.html#what-is-a-base-version-anyway
ThisBuild / tlBaseVersion := "0.0" // your current series x.y

ThisBuild / organization := "dev.hnaderi"
ThisBuild / organizationName := "Hossein Naderi"
ThisBuild / startYear := Some(2023)
ThisBuild / licenses := Seq(License.Apache2)
ThisBuild / developers := List(
  // your GitHub handle and name
  tlGitHubDev("hnaderi", "Hossein Naderi")
)

ThisBuild / scalaVersion := "3.3.5"

lazy val root = tlCrossRootProject.aggregate(domain, catsEffect, zio)

lazy val domain = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("core"))
  .enablePlugins(NoPublishPlugin)
  .settings(
    name := "edomata-domain-example",
    libraryDependencies ++= Seq(
      "dev.hnaderi" %%% "edomata-skunk-circe" % "0.12.5",
      "dev.hnaderi" %%% "edomata-munit" % "0.12.5" % Test,
      "io.circe" %%% "circe-generic" % "0.14.8"
    )
  )

lazy val catsEffect = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .dependsOn(domain)
  .enablePlugins(NoPublishPlugin)
  .settings(
    name := "edomata-ce-example"
  )
  .jsSettings(
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule))
  )

lazy val zio = project
  .dependsOn(domain.jvm)
  .settings(
    name := "edomata-zio-example",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio-interop-cats" % "23.1.0.4"
    )
  )
  .enablePlugins(NoPublishPlugin)
