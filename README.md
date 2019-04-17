# azure-springboot-with-sb

## 1. Pre-requisites/Azure Platform setup
1. Setup a Azure Postgresql server and create mydb DB inside. And, add a new login account with a password to share with the datatier app. Update deploymentconfigtemplate/2.postressql-endpoint.yml and postgres-secrete.yml file with your instance.
1. Setup a Azure Service bus. And, create one topic with a subscription, and create a SAS account. Update deploymentconfigtemplate/5.topic-secrete.yml file with the topic and SAS connection string.
2. Create one queue in the same Azure Service Bus and create an another SAS account for this queue. Update deploymentconfigtemplate/4.topic-secrete.yml file with the queue name and SAS connection string.
