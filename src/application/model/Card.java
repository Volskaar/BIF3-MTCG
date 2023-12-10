package application.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.util.UUID;

public class Card {
    @JsonAlias({"Id"})
    private UUID id;
    @JsonAlias({"Name"})
    private String cardname;

    @JsonAlias({"Cardtype"})
    private String cardtype;

    @JsonAlias({"Damage"})
    private int damage;

    /////////////////////////////////////////////////////////////

    public Card(){
        //default constructor for jackson
    }

    public Card(UUID id, String cardname, int damage){
        this.id = id;
        this.cardname = cardname;
        this.damage = damage;
    }

    /////////////////////////////////////////////////////////////

    public UUID getId(){
        return this.id;
    }
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
    public void setCardid(UUID uuid){this.id = uuid;}
}
