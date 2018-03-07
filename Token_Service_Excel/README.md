# Token_Service_Excel

Token_Service_Excel has two main functionalities:  
* Provide the token to the Excel file, used to perform any operation with macros.
* Provide user informations starting from the token.  

For both the tasks it uses Encryption Algorithms such as RSA and AES.  
The RSA Encryption is done using a certificate as a public key and a JKS file as a private key. Both references consist of two absolute paths that points to the certificate file and JKS file. They are stored in the configuration file inside "config" folder.  
About the AES Encryption, it is done using a random key generated inside the Excel file through a macro.  

After a Maven Build of the project, you should have a .war file (delete the version from the name, leaving only the name of the project) that has to be deployed on a Web Server like Apache Tomcat.
