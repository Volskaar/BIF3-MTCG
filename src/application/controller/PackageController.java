package application.controller;

import application.service.PackageService;
import httpserver.server.Request;
import httpserver.server.Response;
import httpserver.server.RestController;
import httpserver.http.Method;
import httpserver.http.HttpStatus;
import httpserver.http.ContentType;

import java.util.Objects;

public class PackageController implements RestController{

    private final PackageService packageService;

    public PackageController(){
        this.packageService = new PackageService();
    }
    public Response handleRequest(Request request) {
        if(request.getMethod() == Method.POST && Objects.equals(request.getPathname(), "/packages")){
            return this.packageService.createPackage(request);
        }

        else if(request.getMethod() == Method.POST && Objects.equals(request.getPathname(), "/transactions/packages"))
            return this.packageService.acquirePackage(request);

        else{
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
