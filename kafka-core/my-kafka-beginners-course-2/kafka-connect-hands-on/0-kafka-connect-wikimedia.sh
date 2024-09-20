connect-standalone config/connect-standalone.properties config/wikimedia.properties
bin/connect-standalone.sh ../connect-hands-on/config/connect-standalone.properties ../connect-hands-on/config/wikimedia.properties


# Sample data returned in 
# 0-kafka-connect-wikimedia-sample.json

sudo apt install jq
sudo apt install net-tools

bin/kafka-console-consumer.sh \
    		  --bootstrap-server localhost:9092 \
    		  --topic wikimedia.recentchange.connect \
    		  --formatter kafka.tools.DefaultMessageFormatter \
    		  --property print.offset=true \
    		  --property print.partition=true \
    		  --property print.headers=true \
    		  --property print.timestamp=true \
    		  --property print.key=true



bin/kafka-console-consumer.sh \
    		  --bootstrap-server localhost:9092 \
    		  --topic wikimedia.recentchange.connect | jq