package httpclient.service;

import httpserver.http.HttpStatus;
import httpserver.server.Request;
import httpserver.server.Response;

public class UserService{

    public UserService(){

    }

    // GET /user
    public Response getUser(){
        return new Response(HttpStatus.OK);
    }

    // GET /user/id
    public Response getUser(String id){
        return new Response(HttpStatus.NOT_IMPLEMENTED);
    }

    // POST /user
    public Response addUser(Request request){
        return new Response(HttpStatus.NOT_IMPLEMENTED);
    }
}
