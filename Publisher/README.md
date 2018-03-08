# Publisher  

Publisher is a NodeJS component that takes events sent by OGJira.xlsm and forwards them to an active instance of RabbitMQ.  
The Publisher requires NodeJS already installed.  
In order to install the necessary dependencies, the user has to perform a simple task after the correct installation of NodeJS:  
 
* Move into the Publisher directory;  
* type the command "npm install" in the command line: this command will add a folder named "node_modules" in the main directory, which will contain all the dependencies of Publisher.js.  

## Content 

* config.properties: configuration file with port number and RabbitMQ connection info.   
* Publisher.js: the source code file of the script.  
* package.json - package-lock.json: .json files generated through "npm" command on Publisher.js.  
* run_Publisher.bat: batch file that starts the publisher.  
