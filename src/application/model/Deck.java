package application.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public class Deck {
    @JsonAlias({"deck_id"})
    private int deck_id;
    @JsonAlias({"deckname"})
    private String deckname;
    @JsonAlias({"cards"})
    private Card[] cards;
    @JsonAlias({"owner"})
    private String owner;


    /////////////////////////////////////////////////////////////

    public Deck(){
        //default constructor for jackson
    }

    public Deck(int deck_id, String deckname, Card[] cards, String owner) {
        this.deck_id = deck_id;
        this.deckname = deckname;
        this.cards = cards;
        this.owner = owner;
    }

    /////////////////////////////////////////////////////////////


    public int getDeck_id() {
        return deck_id;
    }

    public String getDeckname() {
        return deckname;
    }

    public Card getCardFromDeck(int nr){
        return cards[nr];
    }

    public Card[] getCards(){
        return cards;
    }

    public String getOwner() {
        return owner;
    }

    /////////////////////////////////////////////////////////////


    public void setDeck_id(int deck_id) {
        this.deck_id = deck_id;
    }

    public void setDeckname(String deckname) {
        this.deckname = deckname;
    }

    public void setCards(Card[] cards) {
        this.cards = cards;
    }

    public void setCardInDeck(Card card, int nr){
        this.cards[nr] = card;
    }
}
