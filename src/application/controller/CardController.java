package application.controller;

import httpserver.http.ContentType;
import httpserver.http.HttpStatus;
import httpserver.http.Method;
import httpserver.server.Request;
import httpserver.server.Response;
import httpserver.server.RestController;
import application.service.CardService;

import java.util.Objects;

public class CardController implements RestController {
    private final CardService cardService;

    public CardController(){
        this.cardService = new CardService();
    }
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.GET && Objects.equals(request.getPathname(), "/cards")) {
            return this.cardService.showUserCards(request);
        }

        /////////////////////////////////////////////////////////////////////

        //FAILSAFE
        return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "[]");
    }
}
