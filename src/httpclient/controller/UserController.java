package httpclient.controller;

import httpclient.service.UserService;
import httpserver.server.Request;
import httpserver.server.Response;
import httpserver.server.RestController;
import httpserver.http.Method;
import httpserver.http.HttpStatus;
import httpserver.http.ContentType;

public class UserController implements RestController {

    private final UserService userService;

    //Constructor for UserController
    public UserController(){
        this.userService = new UserService();
    }

    @Override
    public Response handleRequest(Request request) {
        System.out.println("Request received");

        //GET request with e.g. id parameter -> /user/1
        if (request.getMethod() == Method.GET && request.getPathParts().size() > 1) {
            return this.userService.getUser(request.getPathParts().get(1));
        }

        //GET request
        else if (request.getMethod() == Method.GET) {
            return this.userService.getUser();
        }

        //POST user/login request
        else if(request.getMethod() == Method.POST && request.getPathname().equals("/user/login")){
            System.out.println("login post");
            return this.userService.loginUser(request, request.getParams());
        }

        //POST request
        else if (request.getMethod() == Method.POST) {
            return this.userService.addUser(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
