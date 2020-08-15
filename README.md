# sbt-ammonite-classpath

**sbt-ammonite-classpath** is an sbt plug-in to export classpath of an sbt project to Ammonite Script, which can be then used in [Ammonite](https://ammonite.io/) or [Almond](http://almond.sh/). Also supports running Ammonite REPL directly with desired classpath.

## Usage

``` sbt
// project/plugins.sbt
addSbtPlugin("com.thoughtworks.deeplearning" % "sbt-ammonite-classpath" % "latest.release")
```

``` scala
// src/main/scala/mypackage/MyObject.scala
package mypackage

object MyObject {
  def hello() = println("Hello, World!")
}
```

### Exporting Classpath for Almond or Ammonite

``` bash
$ sbt Compile/fullClasspath/exportToAmmoniteScript && amm --predef target/scala-2.12/fullClasspath-Compile.sc
...
...
...
[success] Total time: 1 s, completed Apr 17, 2018 10:11:08 AM
Loading...
Compiling /private/tmp/example/target/scala-2.12/fullClasspath-Compile.sc
Welcome to the Ammonite Repl 1.1.0
(Scala 2.12.4 Java 1.8.0_162)
If you like Ammonite, please support our development at www.patreon.com/lihaoyi
@ mypackage.MyObject.hello() 
Hello, World!
```

Alternatively the classpath can be dynamically loaded by an `import $file` statement, too:

``` bash
$ amm
```

```
Loading...
Welcome to the Ammonite Repl 1.1.0
(Scala 2.12.4 Java 1.8.0_162)
If you like Ammonite, please support our development at www.patreon.com/lihaoyi
@ import $file.target.`scala-2.12`.`fullClasspath-Compile` 
Compiling /private/tmp/example/target/scala-2.12/fullClasspath-Compile.sc
import $file.$                                          

@ mypackage.MyObject.hello() 
Hello, World!
```

### Launching Ammonite REPL

This plugin also supports directly running Ammonite REPL from sbt. Similar to using above scopes you may launch the Ammonite REPL with desired classpath and compile scope as follows:

``` bash
sbt "{scope}:{classpath}::launchAmmoniteRepl"
```

Where **`scope`** can be one of `compile`, `test` and `runtime`, while **`classpath`** can be one of `fullClasspath`, `dependencyClasspath`, `managedClasspath`, `unmanagedClasspath`.

Example:

``` bash
sbt "test:dependencyClasspath::launchAmmoniteRepl"
```

If you would like to run Ammonite REPL with full classpath, you can simply use `launchAmmoniteRepl` task within `compile` (or any other) scope without having to specify classpath task scope:

``` bash
sbt "compile:launchAmmoniteRepl" 
# or simply (without scope, compile will be implied)
sbt launchAmmoniteRepl
```

`initialCommands` setting is also supported. If your `initialCommands` or `launchAmmoniteRepl / initialCommands` setting is not appropriate for a given scope, you can override it in one of this plugin's scopes. For example if you would like to only have `import ammonite.ops._` in your Ammonite REPL but not Scala REPL, you can do as follows:
``` scala
...

console / initialCommands := "println(\"Hello Console\")",

Compile / launchAmmoniteRepl / initialCommands += "\nimport ammonite.ops._"

// or simply launchAmmoniteRepl / initialCommands
...
```

When you run `sbt launchAmmoniteRepl`, both commands will be in effect:

``` bash
sbt launchAmmoniteRepl
...
[info] running ammonite.Main --predef /private/tmp/example/target/scala-2.13/fullClasspath-Compile.sc --predef-code "println("Hello Console")
[info] import ammonite.ops._"
Loading...
Hello Console
Welcome to the Ammonite Repl 2.2.0-4-4bd225e (Scala 2.13.3 Java 1.8.0_252)
@ ls! pwd 
res2: LsSeq = 
".bloop"          ".gitignore"      ".vscode"         "build.sbt"       'target
".git"            ".metals"         'LICENCE          'project          'test
".github"         ".scalafmt.conf"  "README.md"       'src

@
```

By default it will use the `"latest.release"` version of Ammonite, but if you would like to change it, you can override `ammoniteVersion` setting, e.g.:

``` scala
ammoniteVersion := "2.1.4"
Test / ammoniteVersion := "2.2.0"
```

## Related work

[sbt-ammonite](https://github.com/alexarchambault/sbt-ammonite) is an sbt 0.13 plug-in to launch Ammonite. It automatically passes the classpath instead of creating a `sc` file. However, it does not support Almond.

## Requirements

* Sbt 1.x
