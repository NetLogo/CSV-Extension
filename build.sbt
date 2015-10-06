import org.nlogo.build.NetLogoExtension

enablePlugins(NetLogoExtension)

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

val netLogoJarURL =
  Option(System.getProperty("netlogo.jar.url")).getOrElse("http://ccl.northwestern.edu/netlogo/5.3.0/NetLogo.jar")

val netLogoJarsOrDependencies = {
  import java.io.File
  import java.net.URI
  val urlSegments = netLogoJarURL.split("/")
  val lastSegment = urlSegments.last.replaceFirst("NetLogo", "NetLogo-tests")
  val testsUrl = (urlSegments.dropRight(1) :+ lastSegment).mkString("/")
  if (netLogoJarURL.startsWith("file:"))
    Seq(unmanagedJars in Compile ++= Seq(
      new File(new URI(netLogoJarURL)), new File(new URI(testsUrl))))
  else
    Seq(libraryDependencies ++= Seq(
      "org.nlogo" % "NetLogo" % "5.3.0" from netLogoJarURL,
      "org.nlogo" % "NetLogo-tests" % "5.3.0" % "test" from testsUrl))
}

netLogoJarsOrDependencies

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
