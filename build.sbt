sbtPlugin := true

organization in ThisBuild := "com.thoughtworks.deeplearning"

libraryDependencies += "com.thoughtworks.dsl" %% "keywords-each" % "latest.release"

addCompilerPlugin("com.thoughtworks.dsl" %% "compilerplugins-bangnotation" % "latest.release")

addCompilerPlugin("com.thoughtworks.dsl" %% "compilerplugins-reseteverywhere" % "latest.release")

libraryDependencies += "org.scalameta" %% "scalameta" % "1.8.0"
