/*
 * #%L
 * Wildfly Camel :: Example :: Camel CXF JAX-WS
 * %%
 * Copyright (C) 2013 - 2016 RedHat
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package camel.wildfly_camel_cdi;

import javax.jws.WebMethod;
import javax.jws.WebService;


/**
 * SOAP works with XML only
 * WSDL is an XML based document that provides technical details about the web service.
 * UDDI is a directory of web services where client applications can lookup for web services.
 * JAX-WS is the Java API for SOAP web services
 * JAX-WS implementation built into the JDK really is just the basic soap stuff, 
 	if you need any of the more complex WS-* things like WS-Security, WS-RM, WS-Policy, etc..., 
 	you need to use one of the alternatives like CXF or Metro or Axis2
 * SOAP binding gives four style/use models:
 * 	 RPC/encoded
 *   RPC/literal
 *   Document/encoded (not used in practice)
 *   Document/literal
 * Document style: there are no SOAP formatting rules for what the body contains
 * 		it contains whatever the sender and the receiver agrees upon
 * RPC style: implies that SOAP body contains an element with the name of the method
 * 		this element contains an element for each parameter
 * Encoding serialization format: contains data type information within the SOAP message
 * 		This makes serialization (data translation) easier since the data type of each parameter is denoted with the parameter.  
 * Literal serialization format: SOAP message does not directly contain any data type information, just a reference (namespace) to the schema that is used
 * 		sender and the receiver, must know the schema and must use the same rules for translating data
 * Annotations:
 *   @WebService marks a Java class as implementing a Web service or marks a service endpoint interface (SEI) as implementing a web service interface.
 *   @WebMethod denotes a method that is a web service operation.
 *   @Oneway denotes a method as a web service one-way operation that only has an input message and no output message.
 *   @WebParam customizes the mapping of an individual parameter to a web service message part and XML element.
 *   @WebResult customizes the mapping of a return value to a WSDL part or XML element.
 *   @SOAPBinding annotation specifies the mapping of the web service onto the SOAP message protocol.
 *   @WebServiceRef defines a reference to a web service invoked by the client.
 */

@WebService(name = "narucivanje")
public interface NarudzbaServiceWS {
    @WebMethod(operationName = "slanje", action = "urn:slanje")
    String slanjeMetoda(/*@WebParam(name ="narudzba")*/ String narudzba);
    
    @WebMethod(operationName = "prijem", action = "urn:prijem")
    String prijemMetoda();
}
