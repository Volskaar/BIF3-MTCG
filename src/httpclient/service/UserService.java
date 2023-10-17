package httpclient.service;

import httpserver.http.ContentType;
import httpserver.http.HttpStatus;
import httpserver.server.Request;
import httpserver.server.Response;
import httpclient.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;

public class UserService extends BaseService{

    public UserService(){

    }

    // GET /user
    public Response getUser(){
        return new Response(HttpStatus.OK);
    }

    // GET /user/id
    public Response getUser(String id){
        //static test user for call
        User testUser = new User(1, "Mario", "passwort123");
        String json = null;
        try{
            json = this.getObjectMapper().writeValueAsString(testUser);
        }
        catch(JsonProcessingException e){
            throw new RuntimeException(e);
        }

        return new Response(HttpStatus.NOT_IMPLEMENTED, ContentType.JSON, json);
    }

    public Response loginUser(Request request, String params){

        String json = null;

        if(params != null){
            try{
                json = this.getObjectMapper().writeValueAsString(params);
            }
            catch(JsonProcessingException e){
                throw new RuntimeException(e);
            }
        }

        return new Response(HttpStatus.NOT_IMPLEMENTED, ContentType.JSON, json);
    }

    //POST /user
    public Response addUser(Request request){
        return new Response(HttpStatus.CREATED);
    }
}
