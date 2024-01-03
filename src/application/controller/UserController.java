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

        // CURL - login users
        if(request.getMethod() == Method.POST && Objects.equals(request.getPathname(), "/sessions")){
            return this.userService.loginUser(request);
        }

        // CURL - show userdata
        else if (request.getMethod() == Method.GET
                && request.getPathParts().size() > 1
                && Objects.equals(request.getPathParts().get(0), "users")
        ){
            return this.userService.showUser(request, request.getPathParts().get(1));
        }

        // CURL - create users
        else if (request.getMethod() == Method.POST && Objects.equals(request.getPathname(), "/users")) {
            return this.userService.createUser(request);
        }

        //CURL - edit users
        else if (request.getMethod() == Method.PUT && request.getPathParts().size() > 1) {
            return this.userService.editUser(request, request.getPathParts().get(1));
        }

        /////////////////////////////////////////////////////////////////////

        //FAILSAFE
        return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "[Problem handling route]");
    }
}
