/*
 * #%L
 * Wildfly Camel :: Example :: Camel CXF JAX-RS
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

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


/**
 * REST web services request and response types can be XML, JSON, text etc.
 * JAX-RS is the Java API for REST web services
 * Two major impl of JAX-RS API: Jersey(Sun) and RESTEasy(JBoss, included in WildFly)
 * Annotations:
 *   @Path: used to specify the relative path of class and methods. We can get the URI of a webservice by scanning the Path annotation value.
 *   @GET, @PUT, @POST, @DELETE and @HEAD: used to specify the HTTP request type for a method.
 *   @Produces, @Consumes: used to specify the request and response types.
 *   @PathParam: used to bind the method parameter to path value by parsing it.
 *   @QueryParam: binds the parameter passed to method to a query parameter in the path.
 *   @HeaderParam: binds the parameter passed to the method to a HTTP header.
 *   @FormParam: binds the parameter passed to the method to a form value.
 * 
 */

@Path("/narucivanje")
public interface NarudzbaServiceRS {

    @POST
    @Path("/slanje")
    @Produces(MediaType.APPLICATION_JSON)
    String slanjeNarudzbe(String narudzba);

    @GET
    @Path("/prijem")
    @Produces(MediaType.APPLICATION_JSON)
    String prijemOtpreme();
}
