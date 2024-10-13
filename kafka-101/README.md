Produce Events

To build a JAR that we can run from the command line, first run:

    gradle shadowJar

And you should see:

    BUILD SUCCESSFUL

Run the following command to execute the producer application, which will produce some random events to the purchases topic.

    java -cp build/libs/kafka-java-getting-started-0.0.1.jar io.confluent.developer.ProducerExample

You should see output resembling this:

    Produced event to topic purchases: key = awalther   value = t-shirts
    Produced event to topic purchases: key = htanaka    value = t-shirts
    Produced event to topic purchases: key = htanaka    value = batteries
    Produced event to topic purchases: key = eabara     value = t-shirts
    Produced event to topic purchases: key = htanaka    value = t-shirts
    Produced event to topic purchases: key = jsmith     value = book
    Produced event to topic purchases: key = awalther   value = t-shirts
    Produced event to topic purchases: key = jsmith     value = batteries
    Produced event to topic purchases: key = jsmith     value = gift card
    Produced event to topic purchases: key = eabara     value = t-shirts
    10 events were produced to topic purchases