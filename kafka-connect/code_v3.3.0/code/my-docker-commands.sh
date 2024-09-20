
docker-compose -f ./my-docker-compose.yml up my-kafka-cluster elasticsearch postgres
docker run --rm -it --net=host lensesio/fast-data-dev:2.5.0 bash
kafka-topics --create --topic demo-3-twitter --partitions 3 --replication-factor 1 --zookeeper 127.0.0.1:2181
# Start a console consumer on that topic
kafka-console-consumer --topic demo-3-twitter --bootstrap-server 127.0.0.1:9092