package application.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public class Card {

    @JsonAlias({"cardname"})
    private String cardname;

    @JsonAlias({"cardtype"})
    private String cardtype;

    @JsonAlias({"damage"})
    private int damage;

    /////////////////////////////////////////////////////////////

    public Card(){
        //default constructor for jackson
    }

    public Card(String cardname, String cardtype, int damage){
        this.cardname = cardname;
        this.cardtype = cardtype;
        this.damage = damage;
    }

    /////////////////////////////////////////////////////////////

    public String getCardname() {
        return cardname;
    }

    public String getCardtype() {
        return cardtype;
    }

    public int getDamage() {
        return damage;
    }

    /////////////////////////////////////////////////////////////

    public void setCardname(String cardname) {
        this.cardname = cardname;
    }

    public void setCardtype(String cardtype) {
        this.cardtype = cardtype;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }
}
