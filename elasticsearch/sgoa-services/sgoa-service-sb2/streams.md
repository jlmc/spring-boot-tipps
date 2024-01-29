# Streams 

## consumers

### Exchanges

- bpm.deploy.workflow.response 
  - type: topic
  - durable: true
  - Bindings
    - [routing-key: #] -> bpm.deploy.workflow.response.${application-name}

- bpm.notify.externaltask
  - type: topic
  - durable: true
  - Bindings
      - [routing-key: sgoa_LoadOperation] -> bpm.notify.externaltask.sgoa_LoadOperation
      - [routing-key: sgoa_LoadSingleOperation] -> bpm.notify.externaltask.sgoa_LoadSingleOperation

### Queues

- bpm.deploy.workflow.response.${application-name}
  - arguments: 
    - x-queue-type=quorum
    - durable=true

- bpm.notify.externaltask.sgoa_LoadOperation
  - arguments:
    - x-queue-type=quorum
    - durable=true
  - Features  
    - x-dead-letter-exchange:	DLX 
    - x-dead-letter-routing-key: bpm.notify.externaltask.sgoa_LoadOperation

- bpm.notify.externaltask.sgoa_LoadOperation.dlq
  - arguments:
    - x-queue-type:	quorum
    - durable:	tru


- bpm.notify.externaltask.sgoa_LoadSingleOperation
    - arguments:
        - x-queue-type=quorum
        - durable=true
    - Features
        - x-dead-letter-exchange:	DLX
        - x-dead-letter-routing-key: bpm.notify.externaltask.sgoa_LoadSingleOperation

- bpm.notify.externaltask.sgoa_LoadSingleOperation.dlq
  - arguments:
    - x-queue-type:	quorum
    - durable:	tru


## Producers

### Exchanges

- bpm.correlate.message
  - type: fanout
  - durable: true
  - Bindings: [no-routing-key] -> bpm.correlate.message.backup

- bpm.deploy.workflow
  - type: fanout
  - durable: true
  - Bindings: [no-routing-key] -> bpm.deploy.workflow.backup

- bpm.start.process
  - type: fanout
  - durable: true
  - Bindings: [no-routing-key] -> bpm.start.process.backup

- bpm.subscribe.worker
  - type: fanout
  - durable: true
  - Bindings: [no-routing-key] -> bpm.subscribe.worker.backup


### Queues

- bpm.correlate.message.backup
  - Features: 
    - x-message-ttl:	1296000000
    - arguments:
      - x-queue-mode:	lazy
      - durable:	true

- bpm.deploy.workflow.backup
  - Features:
    - x-message-ttl:	1296000000
    - arguments:
      - x-queue-mode:	lazy
      - durable:	true

- bpm.start.process.backup
  - Features:
    - x-message-ttl:	1296000000
    - arguments:
      - x-queue-mode:	lazy
      - durable:	true

- bpm.subscribe.worker.backup
  - Features:
    - x-message-ttl:	1296000000
    - arguments:
      - x-queue-mode:	lazy
      - durable:	true