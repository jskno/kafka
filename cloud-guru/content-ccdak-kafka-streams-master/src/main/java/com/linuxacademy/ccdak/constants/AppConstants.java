package com.linuxacademy.ccdak.constants;

public class AppConstants {

    public static final String INVENTORY_TOPIC = "inventory-purchases";
    public static final String INVENTORY_OUTPUT_STREAM = "inventory-purchases-stream";
    public static final String INVENTORY_OUTPUT_LESS_50 = "inventory-purchases-less-50";
    public static final String INVENTORY_OUTPUT_GREATER_50 = "inventory-purchases-greater-50";
    public static final String INVENTORY_OUTPUT_FILTER = "inventory-purchases-quantity-greater-250";
    public static final String INVENTORY_AGGREGATE = "inventory-aggregate";
    public static final String INVENTORY_COUNT = "inventory-count";
    public static final String TOTAL_PURCHASES_TOPIC = "total-purchases";
    public static final String TOTAL_PURCHASES_REDUCE_TOPIC = "total-purchases-reduce";
    public static final String FLATMAP_TOPIC = "flatmap-topic";
    public static final String FLATMAP_FEMALE = "flatmap-female";
    public static final String FLATMAP_MALE = "flatmap-male";
    public static final String FLATMAP_NO_GENDER = "flatmap-no-gender";
    public static final String FLATMAP_OUTPUT = "flatmap-output";
    public static final String MAP_OUTPUT = "map-output";
    public static final String ACTIVE_YEARS_BY_GENDER_REDUCE = "active-years-by-gender";
    public static final String MERGE_OUTPUT = "merge-unrelated-topics";
    public static final String INSURANCE_CUSTOMER_TOPIC = "topic-datagen-customer-data";
    public static final String INSURANCE_CUSTOMER_ACTIVITY_TOPIC = "topic-datagen-insurance";
    public static final String INNER_JOIN_TOPIC = "topic-inner-join";
    public static final String LEFT_JOIN_TOPIC = "topic-left-join";
    public static final String OUTER_JOIN_TOPIC = "topic-outer-join";

    public static final String TOPIC_VEHICLE_LOCATION = "topic-44";
    public static final String TOPIC_VEHICLE_INDICATORS = "topic-45";

    public static final String CONFIG_FILE = "C:\\My_Folder\\courses\\KafkaProjects\\kafka\\cloud-guru\\content-ccdak-kafka-streams-master\\src\\main\\resources\\cluster.properties";

}
