package application.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public class User {

    private Card[] stack;

    @JsonAlias({"Username"})
    private String username;
    @JsonAlias({"Password"})
    private String password;

    /////////////////////////////////////////////////////////////

    public User(){
        //default constructor for jackson
    }

    public User(String username, String password){
        this.username = username;
        this.password = password;
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
}
