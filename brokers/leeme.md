# Spring Cloud Streams
````
java -jar brokers/target/brokers-0.0.1-SNAPSHOT.jar
````

# Rabbit MQ
````
docker pull rabbitmq:3-management

docker run --rm -d -p 15672:15672 -p 5672:5672 --name my_rabbit rabbitmq:3-management

#Web interface on 
localhost:15672 with guest/guest
````

# Kafka
Download from: https://kafka.apache.org/quickstart

Dockerfile
````
FROM azul/zulu-openjdk

ADD unixRun.sh /app/
ADD kafka_2.13-3.2.0 /app/kafka
````

docker build -t my-kafka:1.0.0 .

````
docker run -d -it --rm -p 9092:9092 --name my-kafka-bash my-kafka:1.0.0 bash

docker exec -it my-kafka-bash bash

bash /app/unixRun.sh

/app/kafka/bin/kafka-topics.sh --list --bootstrap-server localhost:9092
````

Add "container name" to /etc/hosts

