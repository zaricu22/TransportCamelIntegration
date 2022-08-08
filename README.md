# WarehouseCamelIntegration

The project explores possibilities of Apache Camel Integration Framework. </br>
Purpose of this framework is to support capability of automated message routing between connected modules or applications
with possibility of adapting communication protocols and translating message formats used by different technologies and standards. 
It also provide predefined architectural patterns (EIP) to solve integration problems in a common way. 
A great framework's feature is Java DSL (Domain-Specific Language) which support system components configuration through program code in regard to robust XML configuration. 
All mentioned advantages enable simplification of development process where developers are focused only on providing corresponding parameters. 
This framework represents basic code-level integration libraries which was used as base for implementing more roboust GUI and Cloud based software integration solutions. 

Developed software system mostly rely on Java and platform-independent standards (like REST and SOAP), but Apache Camel also support proprietary communication protocols and mesage formats used by other platforms in same manner. </br>
All applications are deployable to Java EE server implementations, with note that <a href="./wildfly-camel-cdi">'wildfly-camel-integration'</a> application require installation of Camel subsystem on corresponding server. 

This software system should support logistics chain. 
Its purpose is to connect internal workers in three separate departments of warehouse as well as third-party participants like manufacturers, markets and online buyers, carriers, and remote warehouse management. 
All participants and internal workers access system services through integration application which provide compatibility and security features.

Because warehouse management system operates with lot of reports and notes, database of this system is based on MongoDB (Document-oriented NoSQL database).

Application include five functional units (warehouse management, commodity stocks management, reporting, ordering, and delivery) placed in three application modules of system (analysis, warehouse, and shipping).
All mentioned modules are developed as standalone application and they are intended to be deployed on separate virtual or physical servers. 
Integration logic, developed via Camel Framowork, is also standalone application which is deployable on existing or separate server.  
It should be mentioned that Apache Camel Framowork also support development of integration logic as part of some existing application.  

Detailed logical descripton of system can be viewed in <a href="Diagrams">'Diagrams'</a> folder.

