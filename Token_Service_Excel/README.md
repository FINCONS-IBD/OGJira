# Token_Service_Excel

Token_Service_Excel has two main functionalities:  
* To provide to the Excel file the token necessary to execute the embedded macros.
* To provide user's information encoded into the token itself.  

For both the tasks it uses Encryption Algorithms such as RSA and AES.  
The RSA Encryption is done using a certificate as public key and a JKS file as private key. Both references consist of two absolute paths that points to the certificate file and JKS file. These paths are defined in the configuration file available in the "config" folder.  
About the AES Encryption, it is done using a random key generated in the Excel file through a macro.  

After a Maven Build of the project, you will have the .war file that has to be deployed on an application server (e.g. Apache Tomcat). Before the deployment it is recommended to rename the .war file in order to remove any version indication from the file name. 
