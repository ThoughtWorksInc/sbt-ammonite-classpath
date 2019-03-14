sbtPlugin := true

organization in ThisBuild := "com.thoughtworks.deeplearning"

libraryDependencies += "com.thoughtworks.dsl" %% "keywords-each" % "1.0.0-RC22+35-5d731d45"

addCompilerPlugin("com.thoughtworks.dsl" %% "compilerplugins-bangnotation" % "1.0.0-RC22+35-5d731d45")

addCompilerPlugin("com.thoughtworks.dsl" %% "compilerplugins-reseteverywhere" % "1.0.0-RC22+35-5d731d45")

libraryDependencies += "org.scalameta" %% "scalameta" % "1.7.0"
