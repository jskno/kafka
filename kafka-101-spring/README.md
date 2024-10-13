
Produce Events

Run the following command to run the Spring Boot application for the Producer.

    #gradle bootRun --args='--producer'
    ./gradlew bootRun --args='--producer'


You should see output resembling this:

    2021-08-27 13:09:50.287  INFO 73259 --- [ad | producer-1] examples.Producer                        : Produced event to topic purchases: key = awalther   value = t-shirts
    2021-08-27 13:09:50.303  INFO 73259 --- [ad | producer-1] examples.Producer                        : Produced event to topic purchases: key = htanaka    value = t-shirts
    2021-08-27 13:09:50.318  INFO 73259 --- [ad | producer-1] examples.Producer                        : Produced event to topic purchases: key = htanaka    value = batteries
    2021-08-27 13:09:50.334  INFO 73259 --- [ad | producer-1] examples.Producer                        : Produced event to topic purchases: key = eabara     value = t-shirts
    2021-08-27 13:09:50.349  INFO 73259 --- [ad | producer-1] examples.Producer                        : Produced event to topic purchases: key = htanaka    value = t-shirts
    2021-08-27 13:09:50.362  INFO 73259 --- [ad | producer-1] examples.Producer                        : Produced event to topic purchases: key = jsmith     value = book
    2021-08-27 13:09:50.377  INFO 73259 --- [ad | producer-1] examples.Producer                        : Produced event to topic purchases: key = awalther   value = t-shirts
    2021-08-27 13:09:50.391  INFO 73259 --- [ad | producer-1] examples.Producer                        : Produced event to topic purchases: key = jsmith     value = batteries
    2021-08-27 13:09:50.405  INFO 73259 --- [ad | producer-1] examples.Producer                        : Produced event to topic purchases: key = jsmith     value = gift card
    2021-08-27 13:09:50.420  INFO 73259 --- [ad | producer-1] examples.Producer                        : Produced event to topic purchases: key = eabara     value = t-shirts

Consume Events

Run the following command to run the Spring Boot application for the Consumer.

    #gradle bootRun --args='--consumer'
    ./gradlew bootRun --args='--consumer'


The consumer application will start and print any events it has not yet consumed and then wait for more events to arrive. On startup of the consumer, you should see output resembling this:
 
    2021-08-27 13:09:54.129  INFO 73259 --- [yConsumer-0-C-1] examples.Consumer                        : Consumed event from topic purchases: key = awalther   value = t-shirts
    2021-08-27 13:09:54.129  INFO 73259 --- [yConsumer-0-C-1] examples.Consumer                        : Consumed event from topic purchases: key = htanaka    value = t-shirts
    2021-08-27 13:09:54.129  INFO 73259 --- [yConsumer-0-C-1] examples.Consumer                        : Consumed event from topic purchases: key = htanaka    value = batteries
    2021-08-27 13:09:54.129  INFO 73259 --- [yConsumer-0-C-1] examples.Consumer                        : Consumed event from topic purchases: key = eabara     value = t-shirts
    2021-08-27 13:09:54.129  INFO 73259 --- [yConsumer-0-C-1] examples.Consumer                        : Consumed event from topic purchases: key = htanaka    value = t-shirts
    2021-08-27 13:09:54.129  INFO 73259 --- [yConsumer-0-C-1] examples.Consumer                        : Consumed event from topic purchases: key = jsmith     value = book
    2021-08-27 13:09:54.129  INFO 73259 --- [yConsumer-0-C-1] examples.Consumer                        : Consumed event from topic purchases: key = awalther   value = t-shirts
    2021-08-27 13:09:54.129  INFO 73259 --- [yConsumer-0-C-1] examples.Consumer                        : Consumed event from topic purchases: key = jsmith     value = batteries
    2021-08-27 13:09:54.129  INFO 73259 --- [yConsumer-0-C-1] examples.Consumer                        : Consumed event from topic purchases: key = jsmith     value = gift card
    2021-08-27 13:09:54.129  INFO 73259 --- [yConsumer-0-C-1] examples.Consumer                        : Consumed event from topic purchases: key = eabara     value = t-shirts


Rerun the producer to see more events, or feel free to modify the code as necessary to create more or different events.

Once you are done with the consumer, enter Ctrl-C to terminate the consumer application.