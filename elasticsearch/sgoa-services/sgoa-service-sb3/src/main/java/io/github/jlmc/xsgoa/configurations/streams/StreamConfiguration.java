package io.github.jlmc.xsgoa.configurations.streams;

import io.github.jlmc.xsgoa.infrastructure.bpm.consumer.dtos.DeployResponseDto;
import io.github.jlmc.xsgoa.infrastructure.bpm.consumer.dtos.ExternalTaskDto;
import io.github.jlmc.xsgoa.infrastructure.bpm.consumer.dtos.ResponseMessageDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class StreamConfiguration {
    // deployBpmResponse;receiveExternalTaskLoadOperation;receiveExternalTaskLoadSingleOperation

    /**
     *  <code>@StreamListener(value = DEPLOY_BPM_RESPONSE)</code>
     */
    @Bean
    Consumer<ResponseMessageDto<DeployResponseDto>> deployBpmResponse() {

        // to test, push the message  to the ex: bpm.deploy.workflow.response
        // { "data": { "name": "123" }}

        return t -> System.out.println("deployBpmResponse::: >>>>> " + t);
    }

    /**
     *  <code>@StreamListener(value = EXTERNAL_TASK_LOAD_OPERATION)</code>
     */
    @Bean
    Consumer<ExternalTaskDto> receiveExternalTaskLoadOperation() {
        // Ex: bpm.notify.externaltask
        // RK: sgoa_LoadOperation
        // msg: {"title": "123"}

        // loadOperation
        return t -> System.out.println("receiveExternalTaskLoadOperation::: >>>>> " + t);
    }

    /**
     *  <code>@StreamListener(value = EXTERNAL_TASK_LOAD_SINGLE_OPERATION)</code>
     */
    @Bean
    Consumer<ExternalTaskDto> receiveExternalTaskLoadSingleOperation() {
        // Ex: bpm.notify.externaltask
        // RK: sgoa_LoadSingleOperation
        // msg: {"title": "123"}
        // loadSingleOperation
        return t -> System.out.println("receiveExternalTaskLoadSingleOperation::: >>>>> " + t);
    }
}
