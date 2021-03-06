name := "pops"

homepage := Some(url("https://github.com/deaktator/pops"))

licenses := Seq("MIT License" -> url("http://opensource.org/licenses/MIT"))

description := """Pops (protobuf ops) makes it easier (and faster) to generically work with Protocol Buffers."""

lazy val commonSettings = Seq(
  organization := "com.github.deaktator",
  scalaVersion := "2.11.7",
  crossScalaVersions := Seq("2.11.7", "2.10.6"),
  crossPaths := true,
  incOptions := incOptions.value.withNameHashing(true),
  javacOptions ++= Seq("-Xlint:unchecked"),
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  ),
  scalacOptions ++= Seq(
    "-unchecked",
    "-deprecation",
    "-feature",
    "-Yinline",
//    "-Yinline-warnings", // Eliminate inline warnings from in scala library.
    "-Yclosure-elim",
    "-Ydead-code",
    "-Xverify",
    "-Ywarn-inaccessible",
    "-Ywarn-dead-code"
  ),

  scalacOptions <++= scalaVersion map {
    case v: String if v.split("\\.")(1).toInt >= 11 =>
      Seq(
        "-Ywarn-unused",
        "-Ywarn-unused-import",

        // These options don't play nice with IntelliJ.  Comment them out to debug.
        "-Ybackend:GenBCode",
        "-Ydelambdafy:method",
        "-Yopt:l:project",
        "-Yconst-opt"
      )
    case _ =>
      Seq()
  }
)

// ====================   Disable packaging root project   ====================
//  Paul P: http://stackoverflow.com/a/25653777
Keys.`package` :=  file("")

packageBin in Global :=  file("")

packagedArtifacts :=  Map()
// ====================   Disable packaging root project   ====================

lazy val root = project.in( file(".") ).
  // To run benchmarks with tests, add 'bench' to the aggregate list
  aggregate(pops241).
  settings(commonSettings: _*).
  settings ()

lazy val pops241 = project.in( file("pops-2.4.1") ).
  settings(commonSettings: _*).
  settings (
    name := "pops-241",

    // Because 2.10 runtime reflection is not thread-safe, tests relying on scala
    // reflection can fail non-deterministically.  Uncheck to disallow tests to
    // run in parallel.
//    parallelExecution in Test := false,

    libraryDependencies ++= Seq(
       compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),

      "org.typelevel" %% "macro-compat" % "1.1.1",
      "org.scala-lang" % "scala-reflect" % scalaVersion.value % "provided",
      "org.scala-lang" % "scala-compiler" % scalaVersion.value % "provided",

      "com.google.protobuf" % "protobuf-java" % "2.4.1",

      // TEST dependencies
      "com.eharmony" % "aloha-proto" % "2.0.0" % "test",
      "org.scalatest" %% "scalatest" % "2.2.6" % "test"
    )
  )

// ===========================   PUBLISHING   ===========================

sonatypeProfileName := "com.github.deaktator"

pomExtra in Global := (
  <url>https://github.com/deaktator/pops</url>
    <licenses>
      <license>
        <name>MIT License</name>
        <url>http://opensource.org/licenses/MIT</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:deaktator/pops</url>
      <developerConnection>scm:git:git@github.com:deaktator/pops.git</developerConnection>
      <connection>scm:git:git@github.com:deaktator/pops.git</connection>
    </scm>
    <developers>
      <developer>
        <id>deaktator</id>
        <name>R M Deak</name>
        <url>https://deaktator.github.io</url>
      </developer>
    </developers>
  )


import ReleaseTransformations._

releaseCrossBuild := true

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  ReleaseStep(action = Command.process("publishSigned", _), enableCrossBuild = true),
  setNextVersion,
  commitNextVersion,
  ReleaseStep(action = Command.process("sonatypeReleaseAll", _), enableCrossBuild = true),
  pushChanges
)
