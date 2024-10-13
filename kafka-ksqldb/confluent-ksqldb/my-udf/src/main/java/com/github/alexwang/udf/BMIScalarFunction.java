package com.github.alexwang.udf;

import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;
import io.confluent.ksql.function.udf.UdfParameter;

import java.text.NumberFormat;

@UdfDescription(
        name = "bmi",
        author = "Alex Wang",
        version = "1.0.0",
        description = "custom scalar function and formula for bmi compute"
)
public class BMIScalarFunction {
    @Udf(description = "The formula used for compute bmi")
    public String bmi(@UdfParameter("the weight of user,unit is KG") int weight,
                      @UdfParameter("the height of user, unit is M") int height) {
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(2);
        return format.format(weight / Math.pow(height, 2));
    }

    @Udf(description = "The formula used for compute bmi")
    public String bmi(@UdfParameter("the weight of user,unit is KG") double weight,
                      @UdfParameter("the height of user, unit is M") double height) {
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(2);
        return format.format(weight / Math.pow(height, 2));
    }
}
