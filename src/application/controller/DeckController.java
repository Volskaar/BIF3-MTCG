package application.controller;

import application.service.DeckService;

import httpserver.server.Request;
import httpserver.server.Response;
import httpserver.server.RestController;
import httpserver.http.Method;
import httpserver.http.HttpStatus;
import httpserver.http.ContentType;

import java.util.Objects;

public class DeckController implements RestController{
    private final DeckService deckService;

    public DeckController(){
        this.deckService = new DeckService();
    }

    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.GET && Objects.equals(request.getPathname(), "/deck")) {
            return this.deckService.showDeck(request);
        }

        else if (request.getMethod() == Method.PUT && Objects.equals(request.getPathname(), "/deck")) {
            return this.deckService.configureDeck(request);
        }

        /////////////////////////////////////////////////////////////////////

        //FAILSAFE
        return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "[]");
    }
}
