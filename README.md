# WarehouseCamelIntegration

A project explores possibilities of Apache Camel Integration Framework. </br>
The purpose of this framework is to support a capability of automated message routing between connected modules or applications
with a possibility of adapting a communication protocols and translating a message formats used by different technologies and standards. 
It also provide a predefined architectural patterns (EIP) to solve a integration problems in a common way. 
The framework main feature is a Java DSL (Domain-Specific Language) which support a system components configuration through program code in regard to robust XML configuration. 
The all mentioned advantages enable simplification of a development process where a developers are focused only on providing a corresponding parameters. 
This framework represents a basic code-level integration libraries which has used as base for implementing a more roboust GUI and Cloud based software integration solutions. 

Developed software system mostly rely on a Java and platform-independent standards (like REST and SOAP), but the Apache Camel framework also support proprietary a communication protocols and mesage formats used by other platforms in same manner.
The all applications are deployable to Java EE server implementations, with note that <a href="./wildfly-camel-cdi">'wildfly-camel-integration'</a> application require installation of Camel subsystem on corresponding server. 

This software system should support a logistics chain. 
Its purpose is to connect a internal workers in a three separate departments of warehouse as well as a third-party participants like a manufacturers, markets, online buyers, carriers, and remote warehouse management. 
The all participants and internal workers access a system services through a integration application which provide compatibility and security mechanisms.

Since the warehouse management system operates with a lot of reports and notes, a database of this system is based on a MongoDB (Document-oriented NoSQL database).

Application include a five functional units (warehouse management, commodity stocks management, reporting, ordering, and delivery) placed in a three application modules of the system (analysis, warehouse, and shipping).
The all mentioned modules above are developed as a standalone application and they are intended to be deployed on a separate virtual or physical servers. 
A integration logic, developed via Camel Framowork, is also the standalone application which is deployable on the existing or separate server.
It should be mentioned that the Apache Camel Framowork also support development of the integration logic as part of the existing application.  

Detailed logical descripton of the system can be viewed in <a href="Diagrams">'Diagrams'</a> folder.

