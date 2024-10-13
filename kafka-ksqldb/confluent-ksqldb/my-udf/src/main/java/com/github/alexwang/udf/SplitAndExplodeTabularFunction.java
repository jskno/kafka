package com.github.alexwang.udf;

import io.confluent.ksql.function.udf.UdfParameter;
import io.confluent.ksql.function.udtf.Udtf;
import io.confluent.ksql.function.udtf.UdtfDescription;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@UdtfDescription(
        name = "split_and_explode",
        author = "Alex Wang",
        version = "1.0.0",
        description = "split and explode int one function"
)
public class SplitAndExplodeTabularFunction {
    //split('kafka ksqldb',' ')==>Array
    //explode(Array)==>Tabular

    @Udtf(description = "Takes an string and delimiter then split and return list of records")
    public List<String> splitAndExplode(@UdfParameter("The string/varchar, do not be null") String str,
                                        @UdfParameter("The delimiter") String delimiter) {
        if (str == null || str.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.asList(str.split(delimiter));
    }
}
