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
      "Export classpath as a .sc file, which can be loaded by another ammonite script or a Jupyter Scala notebook")
  }
  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] = Seq {
    val configuration = !Each(Seq(Compile, Test, Runtime, Provided))
    val classpathKey = !Each(Seq(fullClasspath, dependencyClasspath, managedClasspath, unmanagedClasspath))

    exportToAmmoniteScript in classpathKey in configuration := {
      val code = {
        def ammonitePaths =
          List(q"_root_.ammonite.ops.Path(${(!Each((classpathKey in configuration).value)).data.toString})")

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
      val file = (crossTarget in configuration).value / s"${classpathKey.key.label}-${configuration.id}.sc"
      IO.write(file, code.syntax)
      file
    }

  }
}
