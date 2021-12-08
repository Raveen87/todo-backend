package com.rajven.todo.converter;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import io.r2dbc.spi.Clob;
import org.springframework.core.convert.converter.Converter;
import reactor.core.publisher.Flux;

/**
 * Workaround for H2 having issues converting between Clob and String
 */
public class ClobConverter implements Converter<Clob, String> {
    public String convert(Clob source) {
        List<CharSequence> charSequence = null;
        try {
            charSequence = ((Flux<CharSequence>) source.stream()).collectList().toFuture().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return Objects.isNull(charSequence)
            ? ""
            : String.join("", charSequence);
    }
}
