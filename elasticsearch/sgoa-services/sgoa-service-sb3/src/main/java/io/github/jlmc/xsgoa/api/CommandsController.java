package io.github.jlmc.xsgoa.api;

import io.github.jlmc.xsgoa.infrastructure.bpm.producers.BpmProducer;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Commands", description = "Commands to push messages to rabbitMq using spring cloud streams.")

@RestController
@RequestMapping("/api/commands")
public class CommandsController {

    private final BpmProducer producer;

    public CommandsController(BpmProducer producer) {
        this.producer = producer;
    }

    //curl -XPOST localhost:8080/api/commands/start-process
    @PostMapping("/start-process")
    public String startProcess() {
        long l = System.currentTimeMillis();
        producer.startProcess("Example->" + l);

        return "hello - " + l;
    }

    // curl -XPOST localhost:8080/api/commands/correlate-message
    @PostMapping("/correlate-message")
    public String correlateMessage() {
        producer.correlateMessage("CorrelateMessageDto( "+System.currentTimeMillis()+" )");

        return "hello - " + System.currentTimeMillis();
    }


    // curl -XPOST localhost:8080/api/commands/subscribe-topics
    @PostMapping("/subscribe-topics")
    public String subscribeTopics() {
        producer.subscribeTopics("SubscribeWorkerDto("+System.currentTimeMillis()+")");

        return "hello - " + System.currentTimeMillis();
    }

    // curl -XPOST localhost:8080/api/commands/deploy-bpm
    @PostMapping("/deploy-bpm")
    public String deployBpm() {
        producer.deployBpm("DeployDto("+ System.currentTimeMillis()+")");

        return "hello - " + System.currentTimeMillis();
    }
}
