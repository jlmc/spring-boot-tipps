package io.github.jlmc.docsprocessing.service.infrastructure.bpm.messaging.consumer;

import io.github.jlmc.docsprocessing.service.commons.gateway.response.DeployResponseDto;
import io.github.jlmc.docsprocessing.service.commons.gateway.response.ExternalTaskDto;
import io.github.jlmc.docsprocessing.service.commons.gateway.response.ResponseMessageDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

/**
 * The interface Bpm gateway consumer.
 */

@Configuration
public class BpmConsumerConfiguration {


    public static final String DEPLOY_BPM_RESPONSE = "deployBpmResponse";
    public static final String EXTERNAL_TASK_CRC_VALIDATION = "receiveExternalTaskCrcValidation";
    public static final String EXTERNAL_TASK_INFORMATION_EXTRACTION = "receiveExternalTaskInformationExtraction";

    // push the in the queue: Queue bpm.deploy.workflow.response.docs-processing-service
    // the msg:  { "title": "sasas", "data": {"x": "1212"} }
    //
    // @StreamListener(value = BpmConsumerChannelNames.DEPLOY_BPM_RESPONSE)
    // @Input(DEPLOY_BPM_RESPONSE)
    // public void deployBpmResponse(ResponseMessageDto<DeployResponseDto> responseMessageDto) {
    @Bean
    Consumer<ResponseMessageDto<DeployResponseDto>> deployBpmResponse() {
        return t -> System.out.println("deployBpmResponse::: >>>>> " + t);
    }

    // push in x: bpm.notify.externaltask with rk: documentProcessing_crc_validation the message:  { "businessKey": "kkk123 -> receiveExternalTaskCrcValidation" }
    //@Input(EXTERNAL_TASK_CRC_VALIDATION)
    //SubscribableChannel receiveExternalTaskCrcValidation();
    //     @StreamListener(value = BpmConsumerChannelNames.EXTERNAL_TASK_CRC_VALIDATION)
    //    public void receiveExternalTaskCrcValidation(final ExternalTaskDto externalTaskDto) {
    @Bean
    Consumer<ExternalTaskDto> receiveExternalTaskCrcValidation(ExternalTaskCrcValidator validator) {
        return validator::accept;
    }


    //@Input(EXTERNAL_TASK_INFORMATION_EXTRACTION)
    //SubscribableChannel receiveExternalTaskInformationExtraction();
    //@StreamListener(value = BpmConsumerChannelNames.EXTERNAL_TASK_INFORMATION_EXTRACTION)
    //public void receiveExternalTaskInformationExtraction(final ExternalTaskDto externalTaskDto) {
    @Bean
    Consumer<ExternalTaskDto> receiveExternalTaskInformationExtraction() {
        return t -> System.out.println("receiveExternalTaskInformationExtraction::: >>>>> " + t);
    }

        /**
         * Deploy bpm response subscribable channel.
         *
         * @return the subscribable channel
         */
    //@Input(DEPLOY_BPM_RESPONSE)
    //SubscribableChannel deployBpmResponse();

    /**
     * Receive crc validation subscribable channel.
     *
     * @return the subscribable channel
     */
    //@Input(EXTERNAL_TASK_CRC_VALIDATION)
    //SubscribableChannel receiveExternalTaskCrcValidation();

    /**
     * Receive external task information extraction subscribable channel.
     *
     * @return the subscribable channel
     */
    //@Input(EXTERNAL_TASK_INFORMATION_EXTRACTION)
    //SubscribableChannel receiveExternalTaskInformationExtraction();
}

