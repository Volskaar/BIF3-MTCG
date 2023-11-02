package application.service;

import application.model.Card;
import application.model.User;
import application.persistance.UnitOfWork;
import application.persistance.repository.PackageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import httpserver.http.HttpStatus;
import httpserver.server.Request;
import httpserver.server.Response;

import java.lang.reflect.Type;

public class PackageService extends BaseService{

    private PackageRepository packageRepository;

    public PackageService(){
        this.packageRepository = new PackageRepository(new UnitOfWork());
    }

    public Response createPackage(Request request){
        String requestBody = request.getBody();

        Card[] cards;
        try{
            cards = getObjectMapper().readValue(requestBody, new TypeReference<Card[]>(){});
        }
        catch(JsonProcessingException e){
            throw new RuntimeException(e);
        }

        if(PackageRepository.createPackage(cards)){
            //debug
            for(int i=0; i<5; i++){
                System.out.println(cards[i].getCardname());
            }
            return new Response(HttpStatus.CREATED);
        }
        else{
            return new Response(HttpStatus.FORBIDDEN);
        }
    }

    public Response acquirePackage(Request request){
        return new Response(HttpStatus.NOT_IMPLEMENTED);
    }
}
