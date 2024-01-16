# diagram 

## consumers

## deployBpmResponse

### in the application.yaml
  destination: bpm.deploy.workflow.response
  group: ${spring.application.name} -> the group will the name of the queue (the sufix name of the queue)

### In the RabbitMQ:

Exchange: bpm.deploy.workflow.response
type: topic
durable: true


Queue: bpm.deploy.workflow.response.docs-processing-service
args= x-queue-type: quorum, durable: true


## receiveExternalTaskCrcValidation

### in the application.yaml
          destination: bpm.notify.externaltask
          group: ${config.bpm.topic.CrcValidation}

### In the RabbitMQ:

Exchange: bpm.deploy.workflow.response
type: topic
durable: true


Exchange: bpm.notify.externaltask
type: topic
durable: true

Bindings:
Queue: bpm.notify.externaltask.documentProcessing_crc_validation
routingKey: documentProcessing_crc_validation
feature:
  - x-dead-letter-exchange:	DLX
  - x-dead-letter-routing-key:	bpm.notify.externaltask.documentProcessing_crc_validation
  - arguments: x-queue-type:	quorum
  - durable:	true



Queue: bpm.notify.externaltask.documentProcessing_informationExtraction
routingKey: documentProcessing_informationExtraction
feature:
 - x-dead-letter-exchange:	DLX
 - x-dead-letter-routing-key:	bpm.notify.externaltask.documentProcessing_informationExtraction
 - arguments: x-queue-type:	quorum
 - durable:	true


### receiveExternalTaskInformationExtraction

Queue: bpm.notify.externaltask.documentProcessing_informationExtraction
routingKey: documentProcessing_informationExtraction

## producers



