# Token_Service_Excel
==================
Token_Service_Excel has two main functionalities: </br>
	1. Provide the token to the Excel file, used to perform any operation with macros.</br>
	2. Provide user informations starting from the token.</br></br>
For both the tasks it uses Encryption Algorithms such as RSA and AES. 
The RSA Encryption is done using a certificate as a public key and a JKS file as a private key. Both references consist of two absolute paths that points to the certificate file and JKS file. They are stored in the configuration file inside "config" folder.</br></br>
About the AES Encryption, it is done using a random key generated inside the Excel file through a macro.
