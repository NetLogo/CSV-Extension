import org.nlogo.build.NetLogoExtension

enablePlugins(NetLogoExtension)
enablePlugins(org.nlogo.build.ExtensionDocumentationPlugin)

scalaVersion := "2.11.7"

name := "csv"

netLogoClassManager := "org.nlogo.extensions.csv.CSVExtension"

netLogoTarget :=
  NetLogoExtension.directoryTarget(baseDirectory.value)

netLogoZipSources := false

scalaSource in Compile := baseDirectory.value / "src" / "main"

scalaSource in Test := baseDirectory.value / "src" / "test"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xfatal-warnings",
                      "-feature", "-encoding", "us-ascii")

libraryDependencies ++= Seq(
  "org.apache.commons" % "commons-csv"   % "1.0",
  "org.scalatest"      %% "scalatest"    % "2.2.1"  % "test",
  "org.picocontainer"  % "picocontainer" % "2.13.6" % "test",
  "org.ow2.asm"        % "asm-all"       % "5.0.3"  % "test")

val moveToCsvDir = taskKey[Unit]("add all resources to CSV directory")

val csvDirectory = settingKey[File]("directory that extension is moved to for testing")

csvDirectory := {
  baseDirectory.value / "extensions" / "csv"
}

moveToCsvDir := {
  (packageBin in Compile).value
  val testTarget = NetLogoExtension.directoryTarget(csvDirectory.value)
  testTarget.create(NetLogoExtension.netLogoPackagedFiles.value)
  val testResources = (baseDirectory.value / "test" ***).filter(_.isFile)
  for (file <- testResources.get)
    IO.copyFile(file, csvDirectory.value / "test" / IO.relativize(baseDirectory.value / "test", file).get)
}

test in Test := {
  IO.createDirectory(csvDirectory.value)
  moveToCsvDir.value
  (test in Test).value
  IO.delete(csvDirectory.value)
}

netLogoVersion := "6.0.0-M5"
