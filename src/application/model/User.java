package application.model;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.UUID;

public class User {

    @JsonAlias({"Username"})
    private String username;
    @JsonAlias({"Password"})
    private String password;
    @JsonAlias({"authtoken"})
    private String authToken;
    @JsonAlias({"coins"})
    private int coins;
    @JsonAlias({"stack"})
    private UUID[] stack;
    @JsonAlias({"deck"})
    private UUID[] deck;

    /////////////////////////////////////////////////////////////
    // only relevant for task 14 in CURL Script

    @JsonAlias({"Name"})
    private String name;
    @JsonAlias({"Bio"})
    private String bio;
    @JsonAlias({"Image"})
    private String image;


    /////////////////////////////////////////////////////////////

    public User(){
        //default constructor for jackson
    }

    public User(String username, String password, String authToken, UUID[] stack){
        this.username = username;
        this.password = password;
        this.authToken = authToken;
        this.stack = stack;
    }

    /////////////////////////////////////////////////////////////

    public String getUsername(){
        return this.username;
    }
    public String getPassword(){
        return this.password;
    }

    public String getToken(){
        return this.authToken;
    }
    public int getCoins(){return this.coins;}

    public UUID getCardFromStack(int nr){
        return stack[nr];
    }

    public UUID[] getStack(){
        return stack;
    }

    public UUID[] getDeck(){
        return deck;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getName() {
        return name;
    }

    public String getBio() {
        return bio;
    }

    public String getImage() {
        return image;
    }

    /////////////////////////////////////////////////////////////

    public void setUsername(String username){
        this.username = username;
    }
    public void setPassword(String password){
        this.password = password;
    }

    public void setCardInStack(UUID card, int nr){
        this.stack[nr] = card;
    }
    public void setStack(UUID[] stack){
        this.stack = stack;
    }
    public void setDeck(UUID[] deck){
        this.deck = deck;
    }

    public void setToken(String authToken){
        this.authToken = authToken;
    }
    public void setCoins(int amount){this.coins = amount;}

    public void setName(String name) {
        this.name = name;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

