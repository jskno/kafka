package com.jskno.topology.e_my_exercises;

import com.jskno.constants.OrdersConstants;
import com.jskno.domain.booking.GwyBooking;
import com.jskno.domain.booking.SigBooking;
import com.jskno.serdes.SerdesFactory;
import java.time.Duration;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.ValueJoiner;

public class B_KStreamJoinKStreamTopology {

    //The join is key-based, that is, with the join predicate leftRecord.key == rightRecord.key,
    // and window-based, meaning two input records are joined if and only if their timestamps are “close” to each other
    // as defined by the user-supplied JoinWindows, meaning the window defines an additional join predicate over the record timestamps.

    // Stream-stream joins combine two event streams into a new stream. The streams are joined based on a common key, so keys are necessary.
    // You define a time window, and records on either side of the join need to arrive within the defined window.

    // For that reason as we want to just count the booking for time slot we re-key both topics to the same key for all events (???)

    public static Topology buildTopology() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        KStream<String, GwyBooking> gwyStream = streamsBuilder.stream(
            OrdersConstants.GWY_BOOKINGS,
            Consumed.with(Serdes.String(), SerdesFactory.jsonSerdes(GwyBooking.class)))
            .selectKey((key, value) -> "BOOKING");
        gwyStream.print(Printed.<String, GwyBooking>toSysOut().withLabel(OrdersConstants.GWY_BOOKINGS));

        KStream<String, SigBooking> sigStream = streamsBuilder.stream(
            OrdersConstants.SIG_BOOKINGS,
            Consumed.with(Serdes.Long(), SerdesFactory.jsonSerdes(SigBooking.class)))
            .selectKey((key, value) -> "BOOKING");;
        sigStream.print(Printed.<String, SigBooking>toSysOut().withLabel(OrdersConstants.SIG_BOOKINGS));

        ValueJoiner<GwyBooking, SigBooking, String> valueJoiner =
            (gwyBooking, sigBooking) -> gwyBooking.id() + sigBooking.bookingId();
//        Joined<String, String, String> joinedParams = Joined.with(
//            Serdes.String(),
//            SerdesFactory.jsonSerdes(GwyBooking.class),
//            SerdesFactory.jsonSerdes(SigBooking.class));
//

        KStream<String, String> join = gwyStream.join(
            sigStream,
            valueJoiner,
            JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofSeconds(5)));

        join.print(Printed.<String, String>toSysOut().withLabel(OrdersConstants.GWY_SIG_BOOKINGS_STREAM));

        return streamsBuilder.build();
    }


}
