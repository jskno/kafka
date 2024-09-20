package com.jskno.greetings.exceptionhandler;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;

@Slf4j
public class StreamProcessorCustomErrorHandler implements StreamsUncaughtExceptionHandler {

    @Override
    public StreamThreadExceptionResponse handle(Throwable throwable) {
        log.error("Exception in the Application: {}", throwable.getMessage(), throwable);
        if (throwable.getCause().getMessage().equals("Error Occurred")) {
//            return StreamThreadExceptionResponse.REPLACE_THREAD;
            return StreamThreadExceptionResponse.SHUTDOWN_CLIENT;
        }
        return StreamThreadExceptionResponse.SHUTDOWN_APPLICATION;
    }
}
