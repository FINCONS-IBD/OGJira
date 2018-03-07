# Proton

## Content

* "com" folder: contains the "fincons" folder which has to be added into "com" folder of IBM Proton Engine Java Project
* "sample" folder: contains the .json file that represents the processing logic of the engine
* "config" folder: contains all the configuration files needed by the engine
* "launchProton.bat": batch file that starts the Proton Engine
* "pom.xml": maven pom with all the dependecies needed

## Configuration files

The only configuration file that has to be modified is the "config.properies" file. It contains:

* RabbitMQ information, such as username, password, name of the queue, server address.
* Address and port of OGZilla.
* Protocol, address and port of Jira.
* Protocol, address and port of Token_Service_Excel.

About "logging.properties", the creation of the folders C:\ProtonEngine\logs is suggested.
After a Maven Build of the project, you should have a .zip file called IBMProton. Include the "config" folder inside the unzipped folder, replace "sample" folder and the batch launcher with the one in the repository.