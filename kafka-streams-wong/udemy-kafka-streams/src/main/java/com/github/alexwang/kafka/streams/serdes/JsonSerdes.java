package com.github.alexwang.kafka.streams.serdes;

import com.github.alexwang.kafka.streams.model.*;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

public class JsonSerdes {

    public static TransactionPatternWrapSerde TransactionPattern() {
        return new TransactionPatternWrapSerde(new JsonSerialization<>(), new JsonDeserialization<>(TransactionPattern.class));
    }

    public final static class TransactionPatternWrapSerde extends WrapSerde<TransactionPattern> {
        private TransactionPatternWrapSerde(Serializer<TransactionPattern> serializer, Deserializer<TransactionPattern> deserializer) {
            super(serializer, deserializer);
        }
    }

    public static TransactionRewardWrapSerde TransactionReward() {
        return new TransactionRewardWrapSerde(new JsonSerialization<>(), new JsonDeserialization<>(TransactionReward.class));
    }

    public final static class TransactionRewardWrapSerde extends WrapSerde<TransactionReward> {
        private TransactionRewardWrapSerde(Serializer<TransactionReward> serializer, Deserializer<TransactionReward> deserializer) {
            super(serializer, deserializer);
        }
    }

    public static TransactionKeyWrapSerde TransactionKey() {
        return new TransactionKeyWrapSerde(new JsonSerialization<>(), new JsonDeserialization<>(TransactionKey.class));
    }

    public final static class TransactionKeyWrapSerde extends WrapSerde<TransactionKey> {
        private TransactionKeyWrapSerde(Serializer<TransactionKey> serializer, Deserializer<TransactionKey> deserializer) {
            super(serializer, deserializer);
        }
    }

    public static TransactionWrapSerde Transaction() {
        return new TransactionWrapSerde(new JsonSerialization<>(), new JsonDeserialization<>(Transaction.class));
    }

    public final static class TransactionWrapSerde extends WrapSerde<Transaction> {
        private TransactionWrapSerde(Serializer<Transaction> serializer, Deserializer<Transaction> deserializer) {
            super(serializer, deserializer);
        }
    }

    public static SalesWrapSerde SalesSerde() {
        return new SalesWrapSerde(new JsonSerialization<>(), new JsonDeserialization<>(Sales.class));
    }

    public final static class SalesWrapSerde extends WrapSerde<Sales> {
        private SalesWrapSerde(Serializer<Sales> serializer, Deserializer<Sales> deserializer) {
            super(serializer, deserializer);
        }
    }

    public static SalesStatsWrapSerde SalesStatsSerde() {
        return new SalesStatsWrapSerde(new JsonSerialization<>(), new JsonDeserialization<>(SalesStats.class));
    }

    public final static class SalesStatsWrapSerde extends WrapSerde<SalesStats> {
        private SalesStatsWrapSerde(Serializer<SalesStats> serializer, Deserializer<SalesStats> deserializer) {
            super(serializer, deserializer);
        }
    }

    public static NetTrafficWrapSerde NetTrafficSerde() {
        return new NetTrafficWrapSerde(new JsonSerialization<>(), new JsonDeserialization<>(NetTraffic.class));
    }

    public final static class NetTrafficWrapSerde extends WrapSerde<NetTraffic> {
        private NetTrafficWrapSerde(Serializer<NetTraffic> serializer, Deserializer<NetTraffic> deserializer) {
            super(serializer, deserializer);
        }
    }

    public static PatientWrapSerde PatientSerde() {
        return new PatientWrapSerde(new JsonSerialization<>(), new JsonDeserialization<>(Patient.class));
    }

    public final static class PatientWrapSerde extends WrapSerde<Patient> {
        private PatientWrapSerde(Serializer<Patient> serializer, Deserializer<Patient> deserializer) {
            super(serializer, deserializer);
        }
    }

    public static SickRoomWrapSerde SickRoomSerde() {
        return new SickRoomWrapSerde(new JsonSerialization<>(), new JsonDeserialization<>(SickRoom.class));
    }

    public final static class SickRoomWrapSerde extends WrapSerde<SickRoom> {
        private SickRoomWrapSerde(Serializer<SickRoom> serializer, Deserializer<SickRoom> deserializer) {
            super(serializer, deserializer);
        }
    }

    public static PatientWithSickRoomWrapSerde PatientWithSickRoomSerde() {
        return new PatientWithSickRoomWrapSerde(new JsonSerialization<>(), new JsonDeserialization<>(PatientWithSickRoom.class));
    }

    public final static class PatientWithSickRoomWrapSerde extends WrapSerde<PatientWithSickRoom> {
        private PatientWithSickRoomWrapSerde(Serializer<PatientWithSickRoom> serializer, Deserializer<PatientWithSickRoom> deserializer) {
            super(serializer, deserializer);
        }
    }

    public static EmployeeWrapSerde EmployeeSerde() {
        return new EmployeeWrapSerde(new JsonSerialization<>(), new JsonDeserialization<>(Employee.class));
    }

    public final static class EmployeeWrapSerde extends WrapSerde<Employee> {
        private EmployeeWrapSerde(Serializer<Employee> serializer, Deserializer<Employee> deserializer) {
            super(serializer, deserializer);
        }
    }

    public static EmployeeStatsWrapSerde EmployeeStatsSerde() {
        return new EmployeeStatsWrapSerde(new JsonSerialization<>(), new JsonDeserialization<>(EmployeeStats.class));
    }

    public final static class EmployeeStatsWrapSerde extends WrapSerde<EmployeeStats> {
        private EmployeeStatsWrapSerde(Serializer<EmployeeStats> serializer, Deserializer<EmployeeStats> deserializer) {
            super(serializer, deserializer);
        }
    }

    public static ShootStatsWrapSerde ShootStatsSerde() {
        return new ShootStatsWrapSerde(new JsonSerialization<>(), new JsonDeserialization<>(ShootStats.class));
    }

    public final static class ShootStatsWrapSerde extends WrapSerde<ShootStats> {
        private ShootStatsWrapSerde(Serializer<ShootStats> serializer, Deserializer<ShootStats> deserializer) {
            super(serializer, deserializer);
        }
    }

    public static ShootWrapSerde ShootSerde() {
        return new ShootWrapSerde(new JsonSerialization<>(), new JsonDeserialization<>(Shoot.class));
    }

    public final static class ShootWrapSerde extends WrapSerde<Shoot> {
        private ShootWrapSerde(Serializer<Shoot> serializer, Deserializer<Shoot> deserializer) {
            super(serializer, deserializer);
        }
    }

    private static class WrapSerde<T> implements Serde<T> {
        private final Serializer<T> serializer;
        private final Deserializer<T> deserializer;

        private WrapSerde(Serializer<T> serializer, Deserializer<T> deserializer) {
            this.serializer = serializer;
            this.deserializer = deserializer;
        }

        @Override
        public Serializer<T> serializer() {
            return serializer;
        }

        @Override
        public Deserializer<T> deserializer() {
            return deserializer;
        }
    }
}
