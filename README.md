# WarehouseCamelIntegration

Project explores possibilities of Apache Camel Integration Framowork
which facilitate connection between modules or applications developed using different technologies.
Developed software system mostly rely on Java and platform-independent standards (like REST and SOAP), but Apache Camel also support proprietary communication protocols from other platform in same manner. All applications are deployable to Java EE servers, with note that 'wildfly-camel-integration' application require installation of Camel subsystem (more details on https://wildfly-extras.github.io/wildfly-camel/). 

This software should support logistics chain. 
Its purpose is to connect internal workers in three separate departments of warehouse as well as third-party participants like manufacturers, markets and online buyers, carriers, and remote warehouse management.
All participants and internal workers access system services through integration application which provide compatibility and security features.

Because warehouse management system operates with lot of reports and notes, database of this system is based on MongoDB (Document-oriented NoSQL database).

Application include five functional units (warehouse management, commodity stocks management, reporting, ordering, and delivery) placed in three application modules of system (analysis, warehouse, and shipping).
All mentioned modules are developed as standalone application and they are intended to be deployed on separate virtual or physical servers.
Integration logic, developed via Camel Framowork, is also standalone application which is deployable on existing or separate server. 
It should be mentioned that Apache Camel Framowork also support development of integration logic as part of some existing application.  

Detailed logical descripton of system can be viewed in 'Diagrams' folder.

