
confluent kafka topic list

confluent kafka topic delete inventory-purchases
confluent kafka topic delete inventory-purchases-stream
confluent kafka topic delete inventory-purchases-less-50
confluent kafka topic delete inventory-purchases-greater-50
confluent kafka topic delete inventory-purchases-quantity-greater-250
confluent kafka topic delete flatmap-topic
confluent kafka topic delete flatmap-male
confluent kafka topic delete flatmap-female
confluent kafka topic delete flatmap-no-gender
confluent kafka topic delete flatmap-output
confluent kafka topic delete inventory-aggregate


confluent kafka topic create inventory-purchases --partitions 3
confluent kafka topic create inventory-purchases-stream --partitions 3
confluent kafka topic create inventory-purchases-less-50 --partitions 3
confluent kafka topic create inventory-purchases-greater-50 --partitions 3
confluent kafka topic create inventory-purchases-quantity-greater-250 --partitions 3
confluent kafka topic create flatmap-topic --partitions 3
confluent kafka topic create flatmap-male --partitions 3
confluent kafka topic create flatmap-female --partitions 3
confluent kafka topic create flatmap-no-gender --partitions 3
confluent kafka topic create flatmap-output --partitions 3
confluent kafka topic create inventory-aggregate --partitions 3
confluent kafka topic create inventory-count --partitions 3
confluent kafka topic create inventory-reduce --partitions 3
confluent kafka topic create active-years-by-gender --partitions 3

oranges:{"id": 1, "product": "oranges", "quantity":  10}
apples:{"id": 1, "product": "apples", "quantity":  20}
bananas:{"id": 1, "product": "bananas", "quantity": 35}
kiwi:{"id": 1, "product": "kiwi", "quantity": 315}

1322:{"vehicle_id": 1322,"location": {"latitude": 0.41420080431773376,"longitude": 0.7571994830560752},"ts": 1609541400000}
1943:{"vehicle_id": 1943,"location": {"latitude": 0.6064425224095542,"longitude": 0.9527825504856324},"ts": 1609649300000}
1325:{"vehicle_id": 1325,"location": {"latitude": 0.41420080431773376,"longitude": 0.7571994830560752},"ts": 1609541400000}
1326:{"vehicle_id": 1326,"location": {"latitude": 0.41420080431773376,"longitude": 0.7571994830560752},"ts": 1609541400000}
1327:{"vehicle_id": 1327,"location": {"latitude": 0.41420080431773376,"longitude": 0.7571994830560752},"ts": 1609541400000}
1527:{"vehicle_id": 1527,"location": {"latitude": 0.41420080431773376,"longitude": 0.7571994830560752},"ts": 1609541400000}
1943:{"vehicle_id": 1943,"location": {"latitude": 0.41420080431773376,"longitude": 0.7571994830560752},"ts": 1609541400000}

1324:{"vehicle_id": 1324,"engine_temperature": 179,"average_rpm": 4230}
1325:{"vehicle_id": 1325,"engine_temperature": 179,"average_rpm": 4230}
1326:{"vehicle_id": 1326,"engine_temperature": 179,"average_rpm": 4230}
1327:{"vehicle_id": 1327,"engine_temperature": 179,"average_rpm": 4230}
1943:{"vehicle_id": 1943,"engine_temperature": 231,"average_rpm": 2181}

