package io.github.jlmc.xsgoa.infrastructure.bpm.consumer;

import io.github.jlmc.xsgoa.infrastructure.bpm.consumer.dtos.DeployResponseDto;
import io.github.jlmc.xsgoa.infrastructure.bpm.consumer.dtos.ExternalTaskDto;
import io.github.jlmc.xsgoa.infrastructure.bpm.consumer.dtos.ResponseMessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

@Component
public class BpmConsumerImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(BpmConsumerImpl.class);


    /**
     * Deploy bpm response.
     *
     * @param responseMessageDto the response message dto
     */
    @StreamListener(value = BpmConsumerChannelNames.DEPLOY_BPM_RESPONSE)
    public void deployBpmResponse(ResponseMessageDto<DeployResponseDto> responseMessageDto) {
        LOGGER.info("Deploy bpm response received: {}", responseMessageDto);
    }

    /**
     * Load operation.
     *
     * @param externalTaskDto the external task dto
     */
    @StreamListener(value = BpmConsumerChannelNames.EXTERNAL_TASK_LOAD_OPERATION)
    public void loadOperation(final ExternalTaskDto externalTaskDto) {
        LOGGER.info("ReceiveExternalTaskLoadOperation for Technical Sheet: {}", externalTaskDto.getTitle());
    }

    /**
     * Load single operation.
     *
     * @param externalTaskDto the external task dto
     */
    @StreamListener(value = BpmConsumerChannelNames.EXTERNAL_TASK_LOAD_SINGLE_OPERATION)
    public void loadSingleOperation(final ExternalTaskDto externalTaskDto) {
        LOGGER.info("ReceiveExternalTaskLoadOperation for Technical Sheet: {}", externalTaskDto.getTitle());

    }
}
