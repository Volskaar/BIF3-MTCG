package application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import httpserver.http.ContentType;
import httpserver.http.HttpStatus;
import httpserver.server.Request;
import httpserver.server.Response;
import application.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;

import application.persistance.UnitOfWork;
import application.persistance.repository.UserRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.HttpCookie;
import java.util.Map;

public class UserService extends BaseService{

    private UserRepository userRepository;

    public UserService(){
        this.userRepository = new UserRepository(new UnitOfWork());
    }

    /*///////////////////////////////////////////////////////////////////
    // initial development testing

    // GET /user
    public Response getUser(){
        return new Response(HttpStatus.OK);
    }

    // GET /user/id
    public Response getUser(String id){
        System.out.println("get user for id: " + id);
        User user = userRepository.findById(Integer.parseInt(id));

        String json = null;
        try{
            json = this.getObjectMapper().writeValueAsString(user);
        }
        catch(JsonProcessingException e){
            throw new RuntimeException(e);
        }

        return new Response(HttpStatus.NOT_IMPLEMENTED, ContentType.JSON, json);
    }

    ///////////////////////////////////////////////////////////////////*/

    //POST /users - create user
    public Response createUser(Request request){
        String requestBody = request.getBody();

        User newUser = null;
        try{
            newUser = getObjectMapper().readValue(requestBody, User.class);
        }
        catch(JsonProcessingException e){
            throw new RuntimeException(e);
        }

        if(userRepository.createNewUser(newUser)){
            System.out.println(newUser.getUsername() + ": user created!");
            return new Response(HttpStatus.CREATED);
        }
        else{
            return new Response(HttpStatus.FORBIDDEN);
        }
    }

    /////////////////////////////////////////////////////////////////////

    //POST /sessions - login user
    public Response loginUser(Request request){
        String requestBody = request.getBody();

        User user = null;

        try{
            user = getObjectMapper().readValue(requestBody, User.class);
        }
        catch(JsonProcessingException e){
            throw new RuntimeException(e);
        }

        if(userRepository.checkLogonInformation(user)){
            System.out.println(user.getUsername() + ": login successful!");
            //set session cookies

            HttpCookie cookie = new HttpCookie("user", user.getUsername());
            cookie.setDomain("localhost");

            //???????????????????
            return new Response(HttpStatus.OK, ContentType.JSON,"{'Set-Cookie':"+cookie+"}");
        }
        else{
            System.out.println(user.getUsername() + ": login denied!");
            return new Response(HttpStatus.UNAUTHORIZED);
        }
    }

    /////////////////////////////////////////////////////////////////////

    //POST /users/{username} - edit userdata
    public Response editUser(String username){
        return new Response(HttpStatus.NOT_IMPLEMENTED);
    }
}
