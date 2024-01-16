package io.github.jlmc.docsprocessing.service.infrastructure.bpm.messaging.producer;

//import org.springframework.cloud.stream.annotation.Output;

import org.springframework.messaging.MessageChannel;

/**
 * The interface Bpm gateway producer.
 */
@Deprecated(forRemoval = true)
public interface BpmProducer {

    /*
    @Output("myOutput") define um canal de saída chamado "myOutput".
        MessageChannel myOutput(); é o método que representa esse canal de saída.

        # no application properties
        # Configurações específicas do Spring Cloud Stream
        spring.cloud.stream.bindings.output.destination=sua-fila-de-saida

        Neste exemplo, output é o nome do canal de saída que você definiu em sua interface. sua-fila-de-saida é o tópico ou fila RabbitMQ correspondente.


     */

    /**
     * Start process message channel.
     *
     * @return the message channel
     */
    //@Output(START_PROCESS)
    MessageChannel startProcess();

    /**
     * Correlate message message channel.
     *
     * @return the message channel
     */
    //@Output(CORRELATE_MESSAGE)
    MessageChannel correlateMessage();

    /**
     * Subscribe topics message channel.
     *
     * @return the message channel
     */
    //@Output(SUBSCRIBE_TOPICS)
    MessageChannel subscribeTopics();

    /**
     * Deploy workflow resource message channel.
     *
     * @return the message channel
     */
    //@Output(DEPLOY_BPM)
    MessageChannel deployBpm();
}

