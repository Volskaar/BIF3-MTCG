package application.service;

import application.persistance.repository.ScoreRepository;
import application.persistance.repository.UserRepository;
import httpserver.server.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import application.persistance.UnitOfWork;
import httpserver.http.ContentType;
import httpserver.http.HttpStatus;
import httpserver.server.Request;
import httpserver.server.Response;

public class ScoreService extends BaseService{
    private ScoreRepository scoreRepository;

    public ScoreService(){
        this.scoreRepository = new ScoreRepository(new UnitOfWork());
    }

    /////////////////////////////////////////////////////////////////////

    public Response getUserStats(Request request){
        //1. authenticate
        String token = request.getHeaderMap().getHeader("Authorization");
        String username = scoreRepository.getUsernameByToken(token);

        //authentication --> when no user for given token returns null
        if(username == null){
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "User unauthorized");
        }

        //2. retreive wins and losses
        int losses = scoreRepository.getLosses(username);
        int wins = scoreRepository.getWins(username);

        String output = username + ": +" + wins + " | -" + losses;

        //return REsponse with text body
        return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, output);
    }

    /////////////////////////////////////////////////////////////////////

    public Response getScoreboard(Request request){

        //1. authenticate user
        String token = request.getHeaderMap().getHeader("Authorization");
        if(!scoreRepository.authenticateUser(token)){
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "User unauthorized");
        }

        //2. build scoreboard
        String[] scoreboard = this.scoreRepository.buildScoreboard();

        //3. build plain string
        String scoreboard_plain = null;
        for(String x : scoreboard){
            scoreboard_plain = scoreboard_plain + x + '\n';
        }

        return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, scoreboard_plain);
    }
}
