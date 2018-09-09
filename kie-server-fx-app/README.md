# Kie Server JavaFX Client

A JavaFX client for the Kie Server APIs.

### Steps to run

Make sure you are using Java 8  and fter building the project using `mvn package`, run `mvn exec:java"`.

To run the built version go to target and run:

~~~
java -cp '.:./lib/*:kie-server-fx.jar' org.fxapps.kieserverclient.cdi.CDIApplication
~~~
