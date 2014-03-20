package com.icoin.trading.service;

import com.icoin.trading.model.ExecutedTrade;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * This interface describes a JAX-RS root resource. All the JAXRS annotations (except those overridden) will
 * be inherited by classes implementing it.
 */
@Path("/commissionservice/")
public interface CommissionService {

//    @GET
//    @Path("/customers/{id}/")
//    Customer getCustomer(@PathParam("id") String id);
//
//    @PUT
//    @Path("/customers/")
//    Response updateCustomer(Customer customer);

    @POST
    @Path("/add/")
    Response addExecutedTrade(
            ExecutedTrade executedTrade);

}