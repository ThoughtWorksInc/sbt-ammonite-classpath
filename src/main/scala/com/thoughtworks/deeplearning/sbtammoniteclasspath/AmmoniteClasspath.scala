package com.thoughtworks.deeplearning.sbtammoniteclasspath

import com.thoughtworks.dsl.keywords.Each
import sbt._, Keys._
import sbt.plugins.JvmPlugin
import scala.meta._

/**
  * @author 杨博 (Yang Bo)
  */
object AmmoniteClasspath extends AutoPlugin {
  override def trigger = allRequirements

  override def requires = JvmPlugin

  object autoImport {
    val exportToAmmoniteScript = taskKey[File](
      "Export classpath as a .sc file, which can be loaded by another ammonite script or an Almond notebook")

    lazy val Ammonite = config("ammonite")
      .extend(Compile)
      .withDescription("Ammonite config to run REPL, similar to Compile (default) config.")
    lazy val AmmoniteTest =
      config("ammonite-test")
      .extend(Test)
      .withDescription("Ammonite config to run REPL, similar to Test config.")
    lazy val AmmoniteRuntime = config("ammonite-runtime")
      .extend(Runtime)
      .withDescription("Ammonite config to run REPL, similar to Runtime config.")
    
    lazy val launchAmmoniteRepl = taskKey[Unit]("Run Ammonite REPL")

    lazy val ammoniteVersion = settingKey[String]("Ammonite REPL Version")
  }

  import autoImport._

  override def globalSettings: Seq[Def.Setting[_]] = Seq(
    ammoniteVersion := "latest.release"
  )

  override def projectSettings: Seq[Def.Setting[_]] =
    classpathExportSettings ++
      ammoniteRunSettings(Ammonite, Compile) ++
      ammoniteRunSettings(AmmoniteTest, Test) ++
      ammoniteRunSettings(AmmoniteRuntime, Runtime)

  private val allClasspathKeys = Seq(fullClasspath, dependencyClasspath, managedClasspath, unmanagedClasspath)

  def classpathExportSettings: Seq[Def.Setting[_]] = {
    val configuration = !Each(Seq(Compile, Test, Runtime))
    val classpathKey = !Each(allClasspathKeys)

    Seq(
      configuration / classpathKey  / exportToAmmoniteScript := {
        val code = {
          def ammonitePaths = List {
            q"_root_.ammonite.ops.Path(${(!Each((configuration / classpathKey).value)).data.toString})"
          }

          def mkdirs = List {
            val ammonitePath = !Each(ammonitePaths)
            q"""
            if (!_root_.ammonite.ops.exists($ammonitePath)) {
              _root_.ammonite.ops.mkdir($ammonitePath)
            }
            """
          }

          q"""
          ..$mkdirs
          interp.load.cp(Seq(..$ammonitePaths))
          """
        }
        val file = (configuration / crossTarget).value / s"${classpathKey.key.label}-${configuration.id}.sc"
        IO.write(file, code.syntax)
        file
      }
    )
  }

  def ammoniteRunSettings(ammConf: Configuration, backingConf: Configuration): Seq[Def.Setting[_]] =
    inConfig(ammConf)(
      Defaults.compileSettings ++
        Classpaths.ivyBaseSettings ++
        Seq(
          libraryDependencies     := Seq(("com.lihaoyi" %% "ammonite" % (ammConf / ammoniteVersion).value).cross(CrossVersion.full)),
          connectInput            := true,
          console                 := runTask(ammConf, backingConf, fullClasspath, ammConf / run / runner).value
        ) ++
        allClasspathKeys.map(classpathKey =>
          classpathKey / console  := runTask(ammConf, backingConf, classpathKey, ammConf / run / runner).value
        )
    ) ++ Seq(
      backingConf / launchAmmoniteRepl := (ammConf / console).value,
      backingConf / launchAmmoniteRepl / initialCommands := (ammConf / console / initialCommands).value
    ) ++ 
      allClasspathKeys.map(classpathKey =>
        backingConf / classpathKey / launchAmmoniteRepl := (ammConf / classpathKey / console).value
      )

  private def runTask(
      ammConf: Configuration,
      backingConf: Configuration,
      classpath: TaskKey[Classpath],
      scalaRun: Def.Initialize[Task[ScalaRun]],
  ): Def.Initialize[Task[Unit]] = {
    import Def.parserToInput
    val parser = Def.spaceDelimited()
    Def.task {
      val mainClass = "ammonite.Main"
      val args = Seq(
        "--predef",       (backingConf / classpath / exportToAmmoniteScript).value.absolutePath,
        "--predef-code",  (backingConf / launchAmmoniteRepl / initialCommands).value
      )
      val ammoniteOnlyClasspathFiles = sbt.Attributed.data((ammConf / managedClasspath).value)
      scalaRun.value.run(mainClass, ammoniteOnlyClasspathFiles, args, streams.value.log).get
    }
  }

}
