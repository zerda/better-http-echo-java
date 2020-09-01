package com.vzerda.better.http.echo.controllers;

import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ForkJoinPool;

@RestController
public class EchoController {
    private static class Info {
        public Info(String method, String url, int status, int delay, String content, String server) {
            this.method = method;
            this.url = url;
            this.status = status;
            this.delay = delay;
            this.content = content;
            this.server = server;
        }

        public String method;
        public String url;
        public int status;
        public int delay;
        public String content;
        public String server;
    }

    @RequestMapping(value = {"/api/v1/**", "/api/v2/**"})
    public DeferredResult<ResponseEntity<?>> echo(@RequestParam(value = "delay", defaultValue = "0") int delay,
                                                  @RequestParam(value = "status", defaultValue = "200") int status,
                                                  @RequestParam(value = "body", required = false) String body,
                                                  @RequestParam(value = "content", defaultValue = "DEFAULT CONTENT") String content,
                                                  HttpServletRequest request) throws InterruptedException, UnknownHostException {
        ResponseEntity.BodyBuilder builder = ResponseEntity.status(status);
        if (status == 301 || status == 302) {
            builder.header("Location", "https://www.baidu.com/");
        }

        ResponseEntity<?> result;
        if (Strings.isEmpty(body)) {
            result = builder.body(new Info(request.getMethod(), request.getRequestURI(),
                    status, delay, content, InetAddress.getLocalHost().getHostName()));
        } else {
            result = builder.body(body);
        }

        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        ForkJoinPool.commonPool().submit(() -> {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ignored) {
            }
            output.setResult(result);
        });
        return output;
    }
}
