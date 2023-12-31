package application.controller;

import application.service.UserService;
import httpserver.server.Request;
import httpserver.server.Response;
import httpserver.server.RestController;
import httpserver.http.Method;
import httpserver.http.HttpStatus;
import httpserver.http.ContentType;

import java.util.Objects;

public class UserController implements RestController {

    private final UserService userService;

    //Constructor for UserController
    public UserController(){
        this.userService = new UserService();
    }

    @Override
    public Response handleRequest(Request request) {
        System.out.println("Request received");

        /*///////////////////////////////////////////////////////////////////
        // initial development testing

        //GET request with e.g. id parameter -> /users/1
        if (request.getMethod() == Method.GET && request.getPathParts().size() > 1) {
            return this.userService.getUser(request.getPathParts().get(1));
        }

        //GET request
        else if (request.getMethod() == Method.GET) {
            return this.userService.getUser();
        }

        ///////////////////////////////////////////////////////////////////*/

        if(request.getMethod() == Method.POST && Objects.equals(request.getPathname(), "/sessions")){
            return this.userService.loginUser(request);
        }

        // CURL - create users
        else if (request.getMethod() == Method.POST && Objects.equals(request.getPathname(), "/users")) {
            return this.userService.createUser(request);
        }

        //CURL - edit users
        else if (request.getMethod() == Method.POST && request.getPathParts().size() > 1) {
            return this.userService.editUser(request.getPathParts().get(1));
        }

        /////////////////////////////////////////////////////////////////////

        //FAILSAFE
        return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "[]");
    }
}
