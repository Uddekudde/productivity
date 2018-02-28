/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.tudd.productivity.resource;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import se.kth.id1212.tudd.productivity.controller.Controller;

/**
 * Contains REST endpoints relating to registration and authentication.
 * 
 * @author udde
 */
@Path("")
public class Endpoint {

    @Context
    private UriInfo context;
    
    @EJB
    private Controller controller;
    
    @Path("categories")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String login() {
        return controller.getProductivity();
    }
    @Path("start")
    @GET
    public String start() {
        controller.start();
        return "starting";
    }
    

}
