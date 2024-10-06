package com.jskno.producer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ProducerClientWithSR {
    private final static Logger LOG = LoggerFactory.getLogger(ProducerClientWithSR.class);
    private final static String BOOTSTRAP_SERVER = "127.0.0.1:9092";
    private final static String TOPIC = "employee";

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Properties conf = new Properties();
        conf.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        conf.put(ProducerConfig.ACKS_CONFIG, "all");
        conf.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        conf.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializer");
        conf.put("schema.registry.url", "http://127.0.0.1:8081");

        KafkaProducer<String, Employee> producer = new KafkaProducer<>(conf);
        long currentTime = System.currentTimeMillis();
        LOG.info("*********** event time:{}", currentTime);
        Employee employee = new Employee("E001", "John", 30, "VP", new BigDecimal(188.5),
                ImmutableMap.of("X", 3, "Y", 6), ImmutableList.of("Alex", "Alice"), new Address("China", "GD", "GZ"));
        ProducerRecord<String, Employee> producerRecord = new ProducerRecord<>(TOPIC, null, currentTime, null, employee);

        Future<RecordMetadata> future = producer.send(producerRecord, (metadata, exception) -> {
            if (exception != null)
                LOG.info("The record is sent:{}", metadata);
        });
        producer.flush();
        future.get();
        producer.close();
    }
}

class Employee {
    private String emp_id;
    private String name;
    private int age;
    private String title;
    private BigDecimal height;
    private Map<String, Integer> children;
    private List<String> friends;
    private Address address;

    public Employee() {
    }

    public Employee(String emp_id, String name, int age, String title, BigDecimal height,
                    Map<String, Integer> children, List<String> friends, Address address) {
        this.emp_id = emp_id;
        this.name = name;
        this.age = age;
        this.title = title;
        this.height = height;
        this.children = children;
        this.friends = friends;
        this.address = address;
    }

    public String getEmp_id() {
        return emp_id;
    }

    public void setEmp_id(String emp_id) {
        this.emp_id = emp_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    public Map<String, Integer> getChildren() {
        return children;
    }

    public void setChildren(Map<String, Integer> children) {
        this.children = children;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}

class Address {
    private String country;
    private String province;
    private String city;

    public Address() {
    }

    public Address(String country, String province, String city) {
        this.country = country;
        this.province = province;
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}