package application.controller;

import application.service.ScoreService;
import httpserver.http.ContentType;
import httpserver.http.HttpStatus;
import httpserver.http.Method;
import httpserver.server.Request;
import httpserver.server.Response;
import httpserver.server.RestController;

import java.util.Objects;

public class ScoreController implements RestController {

    public ScoreService scoreService;

    public ScoreController(){
        this.scoreService = new ScoreService();
    }

    @Override
    public Response handleRequest(Request request) {
        if(request.getMethod() == Method.GET && Objects.equals(request.getPathname(), "/stats")){
            return this.scoreService.getUserStats(request);
        }
        else if(request.getMethod() == Method.GET && Objects.equals(request.getPathname(), "/scoreboard")){
            return this.scoreService.getScoreboard(request);
        }

        return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "[Problem handling route]");
    }
}
