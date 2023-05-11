package com.example.demo.control;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.example.demo.config.PublishConfig.PubSubPublisher;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class WebAppController {

    private final PubSubPublisher publisher;

    @PostMapping("/publishMessage")
    public RedirectView publishMessage(@RequestParam("message") String message) {
        
        publisher.sendToPubsub(message);

        return new RedirectView("/");
    }
}
