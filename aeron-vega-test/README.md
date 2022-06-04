# Performance Test with Aeron and Vega

This code is a simple performance test with the low latency libraries Aeron and Vega. 

This code is a simple performance test with a producer and a subscriber. The test consists of sending a fixed number 
of Integers. Note that sending just one integer (4 bytes), implies sending alsa the UDP and IP headers,
which significantly increases the traffic sent.

The test is done without any optimization in the Java Virtual Machines of the Publishers and Subscribers.

# Aeron and Vega
In a nutshell, Aeron (https://github.com/real-logic/aeron) is a low latency messaging open source library, 
and Vega (https://github.com/BBVA-CIB/Vega-Messaging) is another open source library on top of Aeron to enrich 
it with additional features and simplify the configuration.

---

## Publishers & Subscribers
The clients are written in vanilla Java 8, to avoid use of heavy libraries. Although it is not used Spring or another 
Dependency Injection library, the code follows SOLID principles, and use the Java Service Loader to inject the required 
clients lazily.

To execute them, you need to pass the next parameters:
````
-c, clientType: (Optional) Defines the client as a publisher or a subscriber. 
Use pub for publisher, sub for subscriber. Default Value: subscriber.

-f, file: Configuration file path. Vega needs an xml file to configure the conections.

-s, size: Size of the test. Is the number of messages sent in this test.

Example
-f .resources/publisher.xml -c pub -s 100000
-f .resources/subcriber.xml -s 100000
````

---

## Vega Unicast Dameon
Vega can be executed on multicast and on unicast networks. One of the functionalities of Vega is an 
autodiscovery mechanism, that simplifies the discovery between clients and topics. In multicast, the mechanism is straightforward, 
but to work on unicast networks, Vega needs one daemon, called UnicastDaemon, to distribute all the discovery messages
between clients.

This test will be executed on an unicast network. 

To execute the AutoDiscovery Daemon:
````
java -cp target/aeron-vega-test-1.0.0-jar-with-dependencies.jar com.bbva.kyof.vega.autodiscovery.daemon.UnicastDaemonLauncher -ed -sn 192.168.1.0/24
````

To execute the clients:
````
java -cp target/aeron-vega-test-1.0.0-jar-with-dependencies.jar com.gamusdev.lowlatency.performance.tests.aeronvega.Launcher params

Example publisher:
java -cp target/aeron-vega-test-1.0.0-jar-with-dependencies.jar com.gamusdev.lowlatency.performance.tests.aeronvega.Launcher -f ./src/main/resources/publisher.xml -c pub -s 10000000

Example subscriber:
java -cp target/aeron-vega-test-1.0.0-jar-with-dependencies.jar com.gamusdev.lowlatency.performance.tests.aeronvega.Launcher -f ./src/main/resources/subscriber.xml -c sub -s 10000000
````

---

## About the code

This code uses the java ServiceLoader to load dynamically the desired implementations of the interfaces.
With ServiceLoader, it is easy to add the new implementations and configure the META-INF/services files to enable them.
The code is thought to easily add new performance tests with java 8.

The class that launches the application is:
````
com.gamusdev.lowlatency.performance.tests.aeronvega.Launcher
````

The Launcher executes the VegaPerformanceTestExecutor, that executes all the logic of the test:
````
com.gamusdev.lowlatency.performance.tests.aeronvega.executor.ITestExecutor
com.gamusdev.lowlatency.performance.tests.aeronvega.executor.VegaPerformanceTestExecutor
````

The code to read and validate the parameters is in the package: 
````
com.gamusdev.lowlatency.performance.tests.aeronvega.parser.*
````
Inside this package, the Template pattern is used in the interface ICommandLineParser, that implements the common 
methods needed to any required CommandLineParser.

The clients are in the package:
````
com.gamusdev.lowlatency.performance.tests.aeronvega.clients.*
````

ClientFactory is the Factory template used to create the clients lazily: 
````
com.gamusdev.lowlatency.performance.tests.aeronvega.clients.ClientFactory
````
Depending on the type of the desired client, a Publisher or a Subscriber is created.
As said before, because the use of the java ServiceLoader, it is very easy to add other different clients, without
changing the code. Just add the new classes and modify the META-INF/services.

---

To control the BackPressure, and do not lose data, the BackPressureManager implements
a simple control flow mechanism:
````
com.gamusdev.lowlatency.performance.tests.aeronvega.utils.BackPressureManager
````

## Author
http://www.gamusdev.com

dramirez@gamusdev.com