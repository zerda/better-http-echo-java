package com.vzerda.better.http.echo;

import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.metrics.web.servlet.DefaultWebMvcTagsProvider;
import org.springframework.boot.actuate.metrics.web.servlet.WebMvcTags;
import org.springframework.boot.actuate.metrics.web.servlet.WebMvcTagsContributor;
import org.springframework.boot.actuate.metrics.web.servlet.WebMvcTagsProvider;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootApplication
public class BetterHttpEchoApplication {

    public static void main(String[] args) {
        SpringApplication.run(BetterHttpEchoApplication.class, args);
    }

    @Bean
    public WebMvcTagsProvider webMvcTagsProvider() {
        return new DefaultWebMvcTagsProvider() {
            @Override
            public Iterable<Tag> getTags(HttpServletRequest request, HttpServletResponse response, Object handler, Throwable exception) {
                return Tags.of(WebMvcTags.method(request), Tag.of("uri", request.getRequestURI()),
                        WebMvcTags.exception(exception), WebMvcTags.status(response), WebMvcTags.outcome(response));
            }
        };
    }
}
