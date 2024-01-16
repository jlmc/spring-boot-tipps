package io.github.jlmc.docsprocessing.service.api;

import io.github.jlmc.docsprocessing.service.infrastructure.bpm.messaging.producer.DefaultBpmProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/commands")
public class CommandsController {

    final DefaultBpmProducer producer;

    public CommandsController(DefaultBpmProducer producer) {
        this.producer = producer;
    }

    @GetMapping
    public String hello() {
        return "hello - " + System.currentTimeMillis();
    }

    // curl -XPOST localhost:8080/api/commands/start-process
    @PostMapping("/start-process")
    public String startProcess() {
        long l = System.currentTimeMillis();
        producer.startProcess("Example->" + l);

        return "hello - " + l;
    }


    // curl -XPOST localhost:8080/api/commands/correlate-message
    @PostMapping("/correlate-message")
    public String correlateMessage() {
        producer.correlateMessage(new DefaultBpmProducer.CorrelateMessageDto());

        return "hello - " + System.currentTimeMillis();
    }

    // curl -XPOST localhost:8080/api/commands/subscribe-topics
    @PostMapping("/subscribe-topics")
    public String subscribeTopics() {
        producer.subscribeTopics(new DefaultBpmProducer.SubscribeWorkerDto());

        return "hello - " + System.currentTimeMillis();
    }

    @PostMapping("/deploy-bpm")
    public String deployBpm() {
        producer.deployBpm(new DefaultBpmProducer.DeployDto());

        return "hello - " + System.currentTimeMillis();
    }

}
