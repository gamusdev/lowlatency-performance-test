# Performance Test with Spring Cloud Streams and RabbitMQ | Kafka
Using Spring Cloud Streams (SCS), you can change between RabbitMQ and Kafka changing the SCS binders in the pom.xml.
If you have both binders in your pom.xml, you just need to change the configuration (application.yml).

This code is a simple performance test with both messaging brokers. The test consists of sending a fixed number of Integers 
through the brokers. Note that sending just one integer (4 bytes), implies sending alsa the TCP and IP headers,
which significantly increases the traffic sent.  

The test is done without any optimization, 
neither in the brokers nor in the Java Virtual Machines of the Publishers and Subscribers.

The test is executed with one standalone brokers.

---

## Publishers & Subscribers
The clients are using Spring Cloud Streams with Flux.
To execute them, you need to configure the following environment variables:
````
broker.sizeTest: The size of the test (the number of messages to be send & received)
broker.clientType:PUB for publisher, SUB for subscriber.

Example
broker.sizeTest=1000;broker.clientType=PUB
````

To execute the clients:
````
java -jar pathToTarget/brokers-0.0.1.jar
````

### About the code
Once you are using SCS, you can write code interacting with brokers with very simple classes. SCS abstracts you about 
the specific code of the brokers. You just create a Supplier<Flux< ? >> to publish data, 
and a Consumer<Flux< ? >> to create a subscriber. 

The stream sent/received is a Flux<Integers> with the configured sizeTest messages, finishing with a CLOSE signal to end the test.

The 3 most important classes are:
````
com.gamusdev.lowlatency.performance.tests.brokers.configuration.producer.Publisher
com.gamusdev.lowlatency.performance.tests.brokers.configuration.consumer.Subscriber
com.gamusdev.lowlatency.performance.tests.brokers.interceptor.PerformanceChannelInterceptor
````

- The Publisher class creates the Supplier<Flux< Integers >>.
- The Subscriber class creates the Consumer<Flux< Integers >>.
- The PerformanceChannelInterceptor interceptor measures the duration of the test, and ends the application when 
the CLOSE signal is received.

---

## RabbitMQ

To test the RabbitMQ broker, an image from dockerhub is used.
````
docker pull rabbitmq:3-management

docker run --rm -d -p 15672:15672 -p 5672:5672 --name my_rabbit rabbitmq:3-management

#Web interface on 
localhost:15672 with guest/guest
````

---

## Kafka

To test the Kafka broker, used the Kafka binary downloaded from: https://kafka.apache.org/quickstart.

Then, to created my own container with this simple Dockerfile:

Dockerfile
````
FROM azul/zulu-openjdk

ADD unixRun.sh /app/
ADD kafka_2.13-3.2.0 /app/kafka

CMD ["/bin/bash", "-x", "/app/unixRun.sh"]
````

unixRun.sh
````
#!/bin/bash

ZK='/app/kafka/bin/zookeeper-server-start.sh /app/kafka/config/zookeeper.properties' 
KAFKA='/app/kafka/bin/kafka-server-start.sh /app/kafka/config/server.properties'

echo "The hostname is:"
hostname

# Execute Zookeper in background
{
  echo "Executing $ZK"
  exec $ZK >&2
}&

# Execute Kafka in this thread
echo "Executing $KAFKA"
exec $KAFKA >&2
````

Some command of interest:
````
# To build the image
docker build -t my-kafka:1.0.0 .

docker run -d --rm -p 9092:9092 --name my-kafka-bash my-kafka:1.0.0

# To enter inside the container
docker exec -it my-kafka-bash bash

# To see the topics inside Kafka
/app/kafka/bin/kafka-topics.sh --list --bootstrap-server localhost:9092
````

If you are executing the test in localhost, and you get the exception
java.net.UnknownHostException: XXXXXXX (where is the container name of Kafka),
try to insert the "container name" into /etc/hosts:
````
127.0.0.1       8edb878dd0b6
````

---

## Author
http://www.gamusdev.com

dramirez@gamusdev.com