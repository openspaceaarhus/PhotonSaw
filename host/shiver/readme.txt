Shiver is the host-side code that drives the laserctrl board through USB.

The code is written in java and it's built using maven.

To build eclipse project, run: 
  mvn eclipse:eclipse

To build excutable, run:
  mvn package

After mvn package there will be a large (41 MB) jar with all dependencies at:
target/shiver-0.0.1-SNAPSHOT.jar

To start the server run:
java -jar target/shiver-0.0.1-SNAPSHOT.jar server config.yaml

