import play.sbt.routes.RoutesKeys

name := """oauth2-tester"""

version := "1.0.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

scalacOptions ++= Seq("-encoding", "UTF-8")

resolvers += "Sonatype OSS Release Repository" at "https://oss.sonatype.org/content/repositories/releases/"


libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  filters,
  "com.pauldijou" %% "jwt-core" % "0.9.2",
  "com.pauldijou" %% "jwt-play-json" % "0.9.2",
  "com.github.j5ik2o" %% "sw4jj" % "1.0.1",
  "org.scalatestplus.play"  %% "scalatestplus-play"                 % "2.0.0-M1"  % Test,
  "org.scalikejdbc"         %% "scalikejdbc-test"                   % "2.5.0"     % Test,
  "jp.t2v"                  %% "play2-auth-test"                    % "0.14.2"    % Test,
  "com.typesafe.akka"       %% "akka-testkit"                       % "2.4.16"    % Test,
  "com.typesafe.akka"       %% "akka-contrib"                       % "2.4.16",
  "com.github.j5ik2o"       %% "scala-product-advertising-api-core" % "1.0.2",
  "com.adrianhurt"          %% "play-bootstrap"                     % "1.1-P25-B3",
  "jp.t2v"                  %% "play2-auth"                         % "0.14.2",
  "jp.t2v"                  %% "play2-pager"                        % "0.1.0",
  "jp.t2v"                  %% "play2-pager-scalikejdbc"            % "0.1.0",
  "org.scalikejdbc"         %% "scalikejdbc-play-initializer"       % "2.5.+",
  "org.skinny-framework"    %% "skinny-orm"                         % "2.3.0",
  "com.github.t3hnar"       %% "scala-bcrypt"                       % "3.0",
  "org.flywaydb"            %% "flyway-play"                        % "3.0.1",
  "mysql"                   % "mysql-connector-java"                % "6.0.5"
)

RoutesKeys.routesImport ++= Seq(
  "jp.t2v.lab.play2.pager.Pager",
  "jp.t2v.lab.play2.pager.Bindables._",
  "models._"
)

TwirlKeys.templateImports ++= Seq(
  "jp.t2v.lab.play2.pager._",
  "models._"
)

routesGenerator := InjectedRoutesGenerator

scalariformSettings

flywayUrl := "jdbc:mysql://localhost/oauth2_tester?autoReconnect=true&useSSL=false"

flywayUser := "oauth2_tester"

flywayPassword := "phou8Igh"

flywayLocations := Seq("filesystem:conf/db/migration/db.migration.default")

scalacOptions in (Compile, doc) ++= Seq(
  "-no-link-warnings" // Suppresses problems with Scaladoc @throws links
)



