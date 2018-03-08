# Cryptography Server  

The Cryptography Server refers to the java part of an Excel Add-in called "xlloop".  

## Content

The following classes have to be added into the "org.boris.xlloop.util" package:  

* classes/AES.java: Java class that defines AES-based encryption and decryption methods invoked by a macro embedded in OGJira.xlsm along with a random key it generates.  
* classes/RSA.java: Java class that defines RSA-based encryption and decryption methods invoked by a macro embedded in OGJira.xlsm along with a Public Key (.cer file) and a a Private Key (.jks file)
* classes/PropertiesHelper.java: Java class that loads and manages the configuration file .  
* classes/ServerExample.java: Java class that exposes the previous Encryption functions and creates the connection with OGJira.xlsm (replace the existing class whit this one).  
* CryptographyServer-config.properties: configuration file.  
* CryptographyServer.bat: batch file that starts the server.  

For a correct working of the server, you have to import the Xlloop.xll file in OGJira.xlsm and restart it.  