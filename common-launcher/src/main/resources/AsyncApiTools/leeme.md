#AsyncApi
https://www.asyncapi.com/
#AsyncapiGenerator
https://hub.docker.com/r/asyncapi/generator

docker pull asyncapi/generator:1.9.4

# Spring Cloud Streams
````
cd common-launcher/src/main/resources/AsyncApiTools
docker run --rm -v $PWD:/local asyncapi/generator:1.9.4 --version

# From Street Ligths (Asyncapi example)
docker run --rm \
    -v $PWD:/local asyncapi/generator:1.9.4 /local/streetLigths.yml @asyncapi/java-spring-cloud-stream-template \
    -o /local/code/rabbit/streetligth --debug -p binder=rabbit

# Rabbit
docker run --rm \
    -v $PWD:/local asyncapi/generator:1.9.4 /local/integerTest.yml @asyncapi/java-spring-cloud-stream-template \
    -o /local/code/rabbit/in --debug -p binder=rabbit

docker run --rm \
    -v $PWD:/local asyncapi/generator:1.9.4 /local/integerTest.yml @asyncapi/java-spring-cloud-stream-template \
    -o /local/code/rabbit/out --debug -p binder=rabbit -p view=provider

# Kafka
docker run --rm \
    -v $PWD:/local asyncapi/generator:1.9.4 /local/integerTest.yml @asyncapi/java-spring-cloud-stream-template \
    -o /local/code/kafka/in --debug -p binder=kafka

docker run --rm \
    -v $PWD:/local asyncapi/generator:1.9.4 /local/integerTest.yml @asyncapi/java-spring-cloud-stream-template \
    -o /local/code/kafka/out --debug -p binder=kafka -p view=provider
````

# Rabbit MQ
````
docker pull rabbitmq:3-management

docker run --rm -d -p 15672:15672 -p 5672:5672 --name my_rabbit rabbitmq:3-management

#Web interface on 
localhost:15672 with guest/guest
````