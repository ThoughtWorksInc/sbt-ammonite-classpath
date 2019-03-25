sbtPlugin := true

organization in ThisBuild := "com.thoughtworks.deeplearning"

libraryDependencies += "com.thoughtworks.dsl" %% "keywords-each" % "1.0.0-RC22-41-7fd570b9"

addCompilerPlugin("com.thoughtworks.dsl" %% "compilerplugins-bangnotation" % "1.0.0-RC22-41-7fd570b9")

addCompilerPlugin("com.thoughtworks.dsl" %% "compilerplugins-reseteverywhere" % "1.0.0-RC22-41-7fd570b9")

libraryDependencies += "org.scalameta" %% "scalameta" % "1.7.0"