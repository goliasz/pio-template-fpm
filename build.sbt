import AssemblyKeys._

assemblySettings

name := "pio-template-text-clustering"

organization := "com.kolibero"

libraryDependencies ++= Seq(
  "io.prediction"    %% "core"          % "0.9.5" % "provided",
  "org.apache.spark" %% "spark-core"    % "1.5.1" % "provided",
  "org.apache.spark" %% "spark-mllib"   % "1.5.1" % "provided")
