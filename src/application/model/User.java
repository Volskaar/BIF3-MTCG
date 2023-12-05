package application.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public class User {

    @JsonAlias({"Username"})
    private String username;
    @JsonAlias({"Password"})
    private String password;
    @JsonAlias({"AuthToken"})
    private String authToken;
    @JsonAlias({"Coins"})
    private int coins;
    @JsonAlias({"Stack"})
    private Card[] stack;


    /////////////////////////////////////////////////////////////

    public User(){
        //default constructor for jackson
    }

    public User(String username, String password, String authToken, Card[] stack){
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

    public Card[] getStack(){
        return stack;
    }

    public String getToken(){
        return this.authToken;
    }
    public int getCoins(){return this.coins;}

    public Card getCardFromStack(int nr){
        return stack[nr];
    }

    public Card getCardFromStack(String cardname){
        int nrInStack = 0;

        for(int i=0; i< this.stack.length; i++){
            if(this.stack[i].getCardname().equals(cardname)){
                nrInStack = i;
            }
        }

        return stack[nrInStack];
    }

    /////////////////////////////////////////////////////////////

    public void setUsername(String username){
        this.username = username;
    }
    public void setPassword(String password){
        this.password = password;
    }

    public void setCardInStack(Card card, int nr){
        this.stack[nr] = card;
    }

    public void setToken(String authToken){
        this.authToken = authToken;
    }
    public void setCoins(int amount){this.coins = amount;}
}
