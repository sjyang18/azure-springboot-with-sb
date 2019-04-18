# azure-springboot-with-sb
## 1. Architectural Diagram
![](./diagram1.jpg)

## 2. Pre-requisites/Azure Platform setup
- Create a namespace called 'car-registry' by applying [deploymentconfigtemplate/1.namespace.json](deploymentconfigtemplate/1.namespace.json).

- Setup a Azure Postgresql server and create mydb DB inside. And, add a new login account with a password to share with the datatier app. Update [deploymentconfigtemplate/2.postressql-endpoint.yml](deploymentconfigtemplate/2.postressql-endpoint.yml) and [deploymentconfigtemplate/3.postgres-secret.yml](deploymentconfigtemplate/3.postgres-secret.yml) file with your instance. Apply the yaml to your kubernetes cluster.
- Setup a Azure Service bus. And, create one topic with a subscription, and create a SAS account. Update [deploymentconfigtemplate/5.topicsb-secret.yml](deploymentconfigtemplate/5.topicsb-secret.yml) file with the topic and SAS connection string. Apply the yaml to your kubernetes cluster.
- Create one queue in the same Azure Service Bus and create an another SAS account for this queue. Update [deploymentconfigtemplate/4.outputqueue-secret.yml](deploymentconfigtemplate/4.outputqueue-secret.yml) file with the queue name and SAS connection string. Apply the yaml to your kubernetes cluster.
