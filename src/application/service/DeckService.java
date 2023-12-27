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
            return new Response(HttpStatus.FORBIDDEN);
        }

        //2. get cards from user deck based on auth
        Card[] cards = this.deckRepository.showDeck(token);

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
            // Reformat json text as plain text
            String plainText = formatJsonAsPlainText(json);
            return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, plainText);
        }

        return new Response(HttpStatus.OK, ContentType.JSON, json);
    }

    public Response configureDeck(Request request){
        // authenticate user
        String token = request.getHeaderMap().getHeader("Authorization");

        if(!this.deckRepository.checkAuthentication(token)){
            return new Response(HttpStatus.FORBIDDEN);
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
            return new Response(HttpStatus.FORBIDDEN);
        }

        // cards need to be in stack to be used in deck
        if(!this.deckRepository.checkIfCardsInStack(token, cardUUIDs)){
            return new Response(HttpStatus.FORBIDDEN);
        }

        // check if current deck empty, if not, add current cards back to stack
        if(this.deckRepository.checkCurrentDeck(token, cardUUIDs)){
            this.deckRepository.addCardsToStack(token,cardUUIDs);
        }

        // call function to insert into database
        if(this.deckRepository.configureDeck(token, cardUUIDs)){
            // if successfully inserted into deck, remove from stack
            if(this.deckRepository.removeCardsFromStack(token, cardUUIDs)){
                return new Response(HttpStatus.OK);
            }
        }

        return new Response(HttpStatus.FORBIDDEN);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private String formatJsonAsPlainText(String json) {
        return json.replaceAll("[{}\",]", "");
    }
}

