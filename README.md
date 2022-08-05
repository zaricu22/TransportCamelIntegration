# WarehouseCamelIntegration

Project explore possibilities of Apache Camel Integration Framowork
which facilitate connection between modules or applications of different technologies.
Developed software system mostly rely on Java and platform-independent(like REST and SOAP) tehnologies, but Apache Camel also support proprietary approaches from other platform in same manner. All applications are deployable to Java EE servers, but 'wildfly-camel-integration' application require installation of Camel subsystem (more details on https://wildfly-extras.github.io/wildfly-camel/) 

Application include five functional units (warehouse management, commodity stocks management, reporting, ordering, and delivery) placed in three application modules (analysis, warehouse, and shipping).
All three modules are developed as standalone application intended to be deployed on separate virtual or physical servers.
Integration logic is also developed by Camel Framowork as standalone application and it can be deployed on existing or separate server. 
Apache Camel Framowork also support development of integration logic as part of existing application.  

This software system provide and support logistics chain. 
Its purpose is to connect internal workers in three separate departments of warehouse as well as third-party participants like manufacturers, markets and online buyers, carriers, and remote warehouse management.
All participants and internal workers access system services through integration application which provide compatibility and security features.

Because warehouse management system operates with lot of reports and notes, database of this system is based on MongoDB (Document-oriented NoSQL database).

Detailed logical descripton of system can be viewed in 'Diagrams' folder.

