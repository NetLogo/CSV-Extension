import org.nlogo.build.{ NetLogoExtension, ExtensionDocumentationPlugin }

enablePlugins(NetLogoExtension, ExtensionDocumentationPlugin)

name := "csv"
version := "1.1.1"
isSnapshot := true

scalaVersion := "2.13.16"
Compile / scalaSource := baseDirectory.value / "src" / "main"
Test / scalaSource := baseDirectory.value / "src" / "test"
scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xfatal-warnings", "-feature", "-encoding", "us-ascii", "-release", "11")

netLogoVersion := "6.3.0"
netLogoClassManager := "org.nlogo.extensions.csv.CSVExtension"
netLogoTestExtras += (baseDirectory.value / "test")

libraryDependencies ++= Seq(
  "org.apache.commons" % "commons-csv" % "1.0",
  "com.typesafe"       % "config"      % "1.3.1" % "test"
)
