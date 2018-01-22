name := "lzwpack"

version := "0.1"

scalaVersion := "2.12.4"

scalacOptions += "-Ypartial-unification"
scalacOptions += "-language:higherKinds"

libraryDependencies += "org.typelevel" %% "cats-core" % "1.0.1"
libraryDependencies += "com.chuusai" %% "shapeless" % "2.3.3"
libraryDependencies += "com.github.scopt" %% "scopt" % "3.7.0"
libraryDependencies += "co.fs2" %% "fs2-core" % "0.10.0-RC1"
libraryDependencies += "co.fs2" %% "fs2-io" % "0.10.0-RC1"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.4"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"