import org.nlogo.build.{ NetLogoExtension, ExtensionDocumentationPlugin }

enablePlugins(NetLogoExtension, ExtensionDocumentationPlugin)

name := "csv"
version := "1.1.1"
isSnapshot := true

scalaVersion := "2.12.12"
scalaSource in Compile := baseDirectory.value / "src" / "main"
scalaSource in Test := baseDirectory.value / "src" / "test"
scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xfatal-warnings", "-feature", "-encoding", "us-ascii")

netLogoVersion := "6.2.2"
netLogoClassManager := "org.nlogo.extensions.csv.CSVExtension"
netLogoTestExtras += (baseDirectory.value / "test")

libraryDependencies ++= Seq(
  "org.apache.commons" % "commons-csv" % "1.0",
  "com.typesafe"       % "config"      % "1.3.1" % "test"
)
