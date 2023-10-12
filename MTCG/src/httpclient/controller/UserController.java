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
        System.out.println("user request received");
        //GET request with e.g. id parameter -> /user/1
        if (request.getMethod() == Method.GET && request.getPathParts().size() > 1) {
            return this.userService.getUser(request.getPathParts().get(1));
        }
        else if (request.getMethod() == Method.GET) {
            return this.userService.getUser();
        }
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
