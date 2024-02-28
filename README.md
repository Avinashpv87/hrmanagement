 ## Build Application 
 This repository uses gradle as the build tool; Executing the following command will generate two jars
 
 - hrmanagement-<VERSION>-boot.jar
 - hrmanagement-<VERSION>-plain.jar
 
 ## Build Docker Image 
 
 The gradle build uses a plugin called `jib` from
 google. Jib is a fast and simple container image builder that handles all the steps of packaging your application 
 into a container image. It does not require you to write a Dockerfile or have docker installed.
 
 If you are building to  a private registry, make sure to configure Jib with credentials for your registry.

 To Build Docker Image to deploy to a docker registry of your choice , use the below command :
  `./gradlew build` will include the task jibDockerBuild and will produce  a image with tag `056824153965.dkr.ecr.us-east-1.amazonaws.com/hrapps:1.0.2`
 `./gradlew jibDockerBuild` (Default mode) - This will produce a image with tag `056824153965.dkr.ecr.us-east-1.amazonaws.com/hrapps:1.0.2`
 
 `gradle jib --image=<MY IMAGE>` (to provide your own tag) at the moment you can not use this
 
 
 ## ECR Login and Manual docker tagging
 `aws ecr get-login-password --region us-east-1 --profile saika-dev | docker login --username AWS --password-stdin 056824153965.dkr.ecr.us-east-1.amazonaws.com`
 `docker tag timesheet-backend:1.0.0 056824153965.dkr.ecr.us-east-1.amazonaws.com/hrapps:1.0.0`
 `docker push 056824153965.dkr.ecr.us-east-1.amazonaws.com/hrapps:1.0.0`
 
 ### Running Docker image locally
 
  `docker run -e SPRING_DATA_MONGODB_URI=mongodb://user:pwd@mongoIP:27017 -p 8888:8888 056824153965.dkr.ecr.us-east-1.amazonaws.com/hrapps:1.0.2`

aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 056824153965.dkr.ecr.us-east-1.amazonaws.com