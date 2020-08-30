sys.props.get("plugin.version") match {
  case Some(x) => addSbtPlugin("com.thoughtworks.deeplearning" % "sbt-ammonite-classpath" % x)
  case _       => sys.error("""|The system property 'plugin.version' is not defined.
                               |Specify this property using the scriptedLaunchOpts -D.""".stripMargin)
}
addSbtPlugin("com.github.tkawachi" % "sbt-doctest" % "0.9.7")
