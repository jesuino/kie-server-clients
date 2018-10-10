Kie Server FX for  Kie Server 6.5+
--
**This is not for Kie Server 7.+**

Kie Server FX is a simple kie server client that can show us the actual HTTP request behind each use of the Kie Server Java API. 

Unzip `kie-server-fx-1.0.zip` and to run the application use the following command. It will use the java you configure using `JAVA_HOME`:
~~~
./run.sh
~~~

You can also run the JAR directly using:
~~~
java -cp '.:./lib/*:kie-server-fx.jar' org.fxapps.kieserverclient.cdi.CDIApplication
~~~
To change the marshalling format use the system property `kieserverfx.formatType`. Possible values are json, jaxb, xstream.
~~~
java -Dkieserverfx.formatType=xstream -cp '.:./lib/*:kie-server-fx.jar' org.fxapps.kieserverclient.cdi.CDIApplication
~~~

