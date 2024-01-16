package io.github.jlmc.docsprocessing.service.infrastructure.bpm.messaging.consumer;

import io.github.jlmc.docsprocessing.service.commons.gateway.response.DeployResponseDto;
import io.github.jlmc.docsprocessing.service.commons.gateway.response.ExternalTaskDto;
import io.github.jlmc.docsprocessing.service.commons.gateway.response.ResponseMessageDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import static io.github.jlmc.docsprocessing.service.infrastructure.bpm.messaging.consumer.BpmConsumerChannelNames.DEPLOY_BPM_RESPONSE;

@Slf4j
@AllArgsConstructor
@Component
public class BpmConsumerImpl {

    /**
     * Deploy bpm response.
     *
     * @param responseMessageDto the response message dto
     */
    @StreamListener(value = BpmConsumerChannelNames.DEPLOY_BPM_RESPONSE)
    public void deployBpmResponse(ResponseMessageDto<DeployResponseDto> responseMessageDto) {
        log.info("Deploy bpm response received: {}", responseMessageDto);

        // push the in the queue: Queue bpm.deploy.workflow.response.docs-processing-service
        // the msg:  { "title": "sasas", "data": {"x": "1212"} }
    }

    /**
     * Receive external task crc validation.
     *
     * @param externalTaskDto the external task dto
     */
    @StreamListener(value = BpmConsumerChannelNames.EXTERNAL_TASK_CRC_VALIDATION)
    public void receiveExternalTaskCrcValidation(final ExternalTaskDto externalTaskDto) {
        log.info("ReceiveExternalTaskCrcValidation for Technical Sheet: {}", externalTaskDto.getBusinessKey());

        //final String crcCode = processMapperVariableGetter.getCrcCode(externalTaskDto);
        //final String clientNumber = processMapperVariableGetter.getClientNumber(externalTaskDto);
        //final String technicalSheetCode = externalTaskDto.getBusinessKey();

        //crcService.getCrcValidationCode(crcCode, clientNumber, technicalSheetCode);
    }

    /**
     * Receive external task information extraction.
     *
     * @param externalTaskDto the external task dto
     */
    @StreamListener(value = BpmConsumerChannelNames.EXTERNAL_TASK_INFORMATION_EXTRACTION)
    public void receiveExternalTaskInformationExtraction(final ExternalTaskDto externalTaskDto) {

        log.info("receiveExternalTaskInformationExtraction for Technical Sheet: {}", externalTaskDto.getBusinessKey());

        /*
        final DocumentsToInformationExtractionBpmDto documentsToInformationExtraction =
                processMapperVariableGetter.getDocumentsToInformationExtraction(externalTaskDto);
        final String technicalSheetCode = externalTaskDto.getBusinessKey();

        informationExtractionService.handleInformationExtractionRequest(
                bpmMapper.to(documentsToInformationExtraction), technicalSheetCode);

         */
    }
}
