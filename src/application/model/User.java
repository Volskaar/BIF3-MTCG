package application.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public class User {

    private Card[] stack;
    @JsonAlias({"Username"})
    private String username;
    @JsonAlias({"Password"})
    private String password;
    @JsonAlias({"Admin"})
    private boolean admin;


    /////////////////////////////////////////////////////////////

    public User(){
        //default constructor for jackson
    }

    public User(String username, String password, boolean admin){
        this.username = username;
        this.password = password;
        this.admin = admin;
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

    public boolean getAdmin(){
        return this.admin;
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

    public void setAdmin(boolean admin){
        this.admin = admin;
    }
}
