package com.github.alexwang.udf;

import io.confluent.ksql.function.udaf.TableUdaf;
import io.confluent.ksql.function.udaf.UdafDescription;
import io.confluent.ksql.function.udaf.UdafFactory;

@UdafDescription(name = "max2",
        author = "Alex Wang",
        version = "1.0.0",
        description = "max the value after group, support table")
public class MaxTableColumnValueFunction {

    @UdafFactory(description = "max the value since group")
    public static TableUdaf<Double, Double, Double> createUdaf() {
        return new TableUdaf<Double, Double, Double>() {
            @Override
            public Double undo(Double valueToUndo, Double aggregateValue) {
                return valueToUndo > aggregateValue ? valueToUndo : aggregateValue;
            }

            @Override
            public Double initialize() {
                return 0.0D;
            }

            @Override
            public Double aggregate(Double current, Double aggregate) {
                return current > aggregate ? current : aggregate;
            }

            @Override
            public Double merge(Double aggOne, Double aggTwo) {
                return aggTwo;
            }

            @Override
            public Double map(Double agg) {
                return agg;
            }
        };
    }
}
