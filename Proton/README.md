# Proton
</br>
## Content
 * "com" folder: contains the "fincons" folder which has to be added into "com" folder of IBM Proton Engine Java Project </br>
 * "sample" folder: contains the .json file that represents the processing logic of the engine </br>
 * "config" folder: contains all the configuration files needed by the engine </br>
 * "launchProton.bat": batch file that starts the Proton Engine </br>
 * "pom.xml": maven pom with all the dependecies needed </br>
</br>
###Configuration files </br>
The only configuration file that has to be modified is the "config.properies" file. It contains: </br>
 * RabbitMQ information, such as username, password, name of the queue, server address. </br>
 * Address and port of OGZilla. </br>
 * Protocol, address and port of Jira. </br>
 * Protocol, address and port of Token_Service_Excel. </br>
About "logging.properties", the creation of the folders C:\ProtonEngine\logs is suggested. </br>
After a Maven Build of the project, you should have a .zip file called IBMProton. Include the "config" folder inside the unzipped folder, replace "sample" folder and the batch launcher with the one in the repository.