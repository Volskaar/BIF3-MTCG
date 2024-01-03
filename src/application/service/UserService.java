package application.service;

import application.model.Card;
import application.model.UserUpdater;
import com.fasterxml.jackson.core.type.TypeReference;
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
import java.util.UUID;

public class UserService extends BaseService{

    private UserRepository userRepository;

    public UserService(){
        this.userRepository = new UserRepository(new UnitOfWork());
    }

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
            return new Response(HttpStatus.CREATED, ContentType.PLAIN_TEXT, "User successfully created");
        }
        else{
            return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, "User with same username already registered");
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
            userRepository.setUserToken(user);

            return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, user.getUsername() + "login successful");
        }
        else{
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Invalid username/password provided");
        }
    }

    /////////////////////////////////////////////////////////////////////

    //PUT /users/{username} - edit userdata
    public Response editUser(Request request, String inputUsername){
        String requestBody = request.getBody();
        String token = request.getHeaderMap().getHeader("Authorization");

        //1. authenticate user by requesting username by AuthToken from db

        String dbUsername = this.userRepository.getUsernameByToken(token);

        System.out.println("DB: " + dbUsername + " | IP: " + inputUsername);

        if(dbUsername == null){
            return new Response(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "User not found.");
        }
        if(!dbUsername.equals(inputUsername)){
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "User not authorized.");
        }


        //2. generate userUpdater Object

        UserUpdater updater;

        try {
            updater = getObjectMapper().readValue(requestBody, UserUpdater.class);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        //3. update current userdata

        System.out.println("Username: " + inputUsername);
        System.out.println("Name: " + updater.getName());
        System.out.println("Bio: " + updater.getBio());
        System.out.println("Image: " + updater.getImage());

        if(this.userRepository.updateUserData(
                inputUsername,
                updater.getName(),
                updater.getBio(),
                updater.getImage()
        )){
            return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, "User successfully updated.");
        }

        //failsafe
        return new Response(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /////////////////////////////////////////////////////////////////////

    // GET /users/{username} - show UserData
    public Response showUser(Request request, String inputUsername){

        //1. authenticate user by requesting username by AuthToken from db
        String token = request.getHeaderMap().getHeader("Authorization");
        String dbUsername = this.userRepository.getUsernameByToken(token);

        if(dbUsername == null){
            return new Response(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "User not found.");
        }
        if(!dbUsername.equals(inputUsername)){
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "User not authorized.");
        }

        //2. get user based on username into object

        User user = this.userRepository.getUser(inputUsername);

        //3. serialize object into json and return in response body

        String json = null;

        try {
            json = getObjectMapper().writeValueAsString(user);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return new Response(HttpStatus.OK, ContentType.JSON, json);
    }
}
