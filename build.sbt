name := "pops"

// homepage := Some(url("https://github.com/deaktator/pops"))
// 
// licenses := Seq("MIT License" -> url("http://opensource.org/licenses/MIT"))
// 
// description := """library for easily turning expressions into functions"""
// 
// publishTo := {
//   val nexus = "https://oss.sonatype.org/"
//   if (isSnapshot.value)
//     Some("snapshots" at nexus + "content/repositories/snapshots")
//   else
//     Some("releases" at nexus + "service/local/staging/deploy/maven2")
// }
// 
// publishMavenStyle := true

// publishArtifact in Test := false

// pomIncludeRepository := { _ => false }

// pomExtra := (
//      <url>https://github.com/deaktator/pops</url>
//      <licenses>
//        <license>
//          <name>MIT License</name>
//          <url>http://opensource.org/licenses/MIT</url>
//          <distribution>repo</distribution>
//        </license>
//      </licenses>
//      <scm>
//        <url>git@github.com:deaktator/pops</url>
//        <connection>scm:git:git@github.com:deaktator/pops.git</connection>
//      </scm>
//      <developers>
//        <developer>
//          <id>deaktator</id>
//          <name>R M Deak</name>
//          <url>https://deaktator.github.io</url>
//        </developer>
//      </developers>
//  )

lazy val commonSettings = Seq(
  organization := "com.github.deaktator",
  version := "0.0.1-SNAPSHOT",
  scalaVersion := "2.11.6",
  crossScalaVersions := Seq("2.11.6", "2.10.5"),
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
    "-Yinline-warnings",
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

    // Because 2.10 runtime reflection is not thread-safe, tests fail non-deterministically.
    // This is a hack to make tests pass by not allowing the tests to run in parallel.
    parallelExecution in Test := false,

    libraryDependencies ++= Seq(
      "org.typelevel" %% "macro-compat" % "1.1.1" % "provided",
       compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
  
      "org.scala-lang" % "scala-reflect" % scalaVersion.value % "provided",
      "org.scala-lang" % "scala-compiler" % scalaVersion.value % "provided",
      "com.google.protobuf" % "protobuf-java" % "2.4.1",
      "commons-codec" % "commons-codec" % "1.4",
      "com.eharmony" % "aloha-proto" % "2.0.0" % "test",
      "org.scalatest" %% "scalatest" % "2.2.6" % "test"
    )
  )
