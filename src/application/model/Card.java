package application.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public class Card {
    @JsonAlias({"Id"})
    private String id;
    @JsonAlias({"Name"})
    private String cardname;

    @JsonAlias({"Cardtype"})
    private String cardtype;

    @JsonAlias({"Damage"})
    private int damage;

    @JsonAlias({"Rarity"})
    private int rarity;

    /////////////////////////////////////////////////////////////

    public Card(){
        //default constructor for jackson
    }

    public Card(String id, String cardname, int damage){
        this.id = id;
        this.cardname = cardname;
        this.damage = damage;
    }

    /////////////////////////////////////////////////////////////

    public String getId(){
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

    public int getRarity(){return this.rarity;}

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

    public void setRarity(int rarity) {
        this.rarity = rarity;
    }
}
