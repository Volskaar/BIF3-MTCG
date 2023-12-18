package application.service;

import application.model.Card;
import application.persistance.UnitOfWork;
import application.persistance.repository.CardRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import httpserver.http.ContentType;
import httpserver.http.HttpStatus;
import httpserver.server.Response;
import httpserver.server.Request;

import java.util.UUID;

public class CardService extends BaseService{
    private final CardRepository cardRepository;

    public CardService(){
        this.cardRepository = new CardRepository(new UnitOfWork());
    }

    // GET /cards - show all acquired cards of user with token
    public Response showUserCards(Request request){

        String token = request.getHeaderMap().getHeader("Authorization");

        // check for authorization

        if(!cardRepository.checkAuthentication(token)){
            System.out.println("user unauthorized");
            request.getHeaderMap().print();
            return new Response(HttpStatus.FORBIDDEN);
        }

        //retrieve cardUUIDs from DB

        UUID[] cardUUIDs = cardRepository.showUserCards(token);

        //make cards from card uuids

        Card[] cards;
        cards = new Card[cardUUIDs.length];

        for(int i=0; i<cardUUIDs.length; i++){
            cards[i] = cardRepository.generateCard(cardUUIDs[i]);
        }

        //turn cards into json text

        String json = null;

        try{
            json = this.getObjectMapper().writeValueAsString(cards);
        }
        catch(JsonProcessingException e){
            throw new RuntimeException(e);
        }

        //attach json to response body

        return new Response(HttpStatus.OK, ContentType.JSON, json);
    }
}
