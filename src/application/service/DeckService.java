package application.service;

import application.controller.DeckController;
import application.model.Card;
import application.persistance.UnitOfWork;
import application.persistance.repository.DeckRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import httpserver.http.ContentType;
import httpserver.http.HttpStatus;
import httpserver.server.Request;
import httpserver.server.Response;

import java.util.Objects;
import java.util.UUID;

public class DeckService extends BaseService{

    private DeckRepository deckRepository;

    public DeckService(){
        this.deckRepository = new DeckRepository(new UnitOfWork());
    }

    public Response showDeck(Request request){
        String params = request.getParams();
        String token = request.getHeaderMap().getHeader("Authorization");

        //1. authenticate user with token
        if(!this.deckRepository.checkAuthentication(token)){
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "User unauthorized");
        }

        //2. get cards from user deck based on auth
        Card[] cards = this.deckRepository.showDeck(token);

        //check if no cards in response
        if(cards.length == 0){
            return new Response(HttpStatus.NO_CONTENT, ContentType.PLAIN_TEXT, "The users deck is empty");
        }

        //3. turn card array into json string and attach to response
        String json = null;

        try{
            json = this.getObjectMapper().writeValueAsString(cards);
        }
        catch(JsonProcessingException e){
            throw new RuntimeException(e);
        }

        System.out.println("Params: " + params);

        // if params for plaintext given return as plaintext
        if (Objects.equals(params, "format=plain")) {
            return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, json);
        }

        return new Response(HttpStatus.OK, ContentType.JSON, json);
    }

    public Response configureDeck(Request request){
        // authenticate user
        String token = request.getHeaderMap().getHeader("Authorization");

        if(!this.deckRepository.checkAuthentication(token)){
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "User unauthorized");
        }

        // get and deserialize JSON in request body
        String requestBody = request.getBody();
        UUID[] cardUUIDs;

        try{
            cardUUIDs = getObjectMapper().readValue(requestBody, new TypeReference<UUID[]>(){});
        }
        catch(JsonProcessingException e){
            throw new RuntimeException(e);
        }

        // should fail with less than 4 cards
        if(cardUUIDs.length < 4){
            return new Response(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, "The provided deck did not include the required amount of cards");
        }

        // cards need to be in stack to be used in deck
        if(!this.deckRepository.checkIfCardsInStack(token, cardUUIDs)){
            return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, "At least one of the provided cards does not belong to the user or is not available");
        }

        // check if current deck empty, if not, add current cards back to stack
        if(this.deckRepository.checkCurrentDeck(token, cardUUIDs)){
            this.deckRepository.addCardsToStack(token,cardUUIDs);
        }

        // call function to insert into database
        if(this.deckRepository.configureDeck(token, cardUUIDs)){
            // if successfully inserted into deck, remove from stack
            if(this.deckRepository.removeCardsFromStack(token, cardUUIDs)){
                return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, "The deck has been successfully configured");
            }
        }

        //failsafe
        return new Response(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

