enablePlugins(org.nlogo.build.NetLogoExtension)

scalaVersion := "2.11.7"

scalaSource in Compile := baseDirectory.value / "src" / "main"

scalaSource in Test := baseDirectory.value / "src" / "test"

javaSource in Compile := baseDirectory.value / "src" / "main"

javaSource in Test := baseDirectory.value / "src" / "test"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xfatal-warnings",
                      "-feature", "-encoding", "us-ascii")

val netLogoJarsOrDependencies =
  Option(System.getProperty("netlogo.jar.url"))
    .orElse(Some("http://ccl.northwestern.edu/netlogo/5.3.0/NetLogo.jar"))
    .map { url =>
      import java.io.File
      import java.net.URI
      val testsUrl = url.replaceFirst("NetLogo", "NetLogo-tests")
      if (url.startsWith("file:"))
        (Seq(new File(new URI(url)), new File(new URI(testsUrl))), Seq())
      else
        (Seq(), Seq(
          "org.nlogo" % "NetLogo" % "5.3.0" from url,
          "org.nlogo" % "NetLogo-tests" % "5.3.0" % "test" from testsUrl))
    }.get

unmanagedJars in Compile ++= netLogoJarsOrDependencies._1

libraryDependencies ++= netLogoJarsOrDependencies._2

libraryDependencies += "org.apache.commons" % "commons-csv" % "1.0"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "org.picocontainer" % "picocontainer" % "2.13.6" % "test",
  "asm" % "asm-all" % "3.3.1" % "test"
)

name := "csv"

netLogoClassManager := "org.nlogo.extensions.csv.CSVExtension"

netLogoZipSources := false

packageBin in Compile := {
  val jar = (packageBin in Compile).value
  val csvZip = baseDirectory.value / "csv.zip"
  if (csvZip.exists) {
    IO.unzip(csvZip, baseDirectory.value)
    for (file <- (baseDirectory.value / "csv" ** "*.jar").get)
      IO.copyFile(file, baseDirectory.value / file.getName)
    IO.delete(baseDirectory.value / "csv")
  } else {
    sys.error("No zip file - csv extension not built")
  }
  jar
}

val moveToCsvDir = taskKey[Unit]("move to csv directory")

val csvDirectory = settingKey[File]("directory that extension is moved to for testing")

csvDirectory := {
  baseDirectory.value / "extensions" / "csv"
}

moveToCsvDir := {
  val csvJar = (packageBin in Compile).value
  val base = baseDirectory.value
  IO.createDirectory(csvDirectory.value)
  val allDependencies =
    Attributed.data((dependencyClasspath in Compile).value)
  val zipExtras =
    (allDependencies :+ csvJar)
      .filterNot(_.getName contains "NetLogo")
  for(extra <- zipExtras)
    IO.copyFile(extra, csvDirectory.value / extra.getName)
  for (dir <- Seq("alternate-netlogolite", "demo"))
    IO.copyDirectory(base / dir, csvDirectory.value / dir)
  val testResources = (baseDirectory.value / "test" ***).filter(_.isFile)
  for (file <- testResources.get)
    IO.copyFile(file, csvDirectory.value / "test" / IO.relativize(baseDirectory.value / "test", file).get)
}

test in Test := {
  moveToCsvDir.value
  (test in Test).value
  IO.delete(baseDirectory.value / "extensions")
}

cleanFiles ++= {
  val base = baseDirectory.value
  Seq(base / "csv.jar",
      base / "csv.zip")
}
