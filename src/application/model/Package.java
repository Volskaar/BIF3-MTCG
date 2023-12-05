package application.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public class Package {
    @JsonAlias({"Id"})
    private int id;
    @JsonAlias({"Cards"})
    private Card[] cards;

    public Package(int id, Card[] cards){
        this.id = id;
        this.cards = cards;
    }

    /////////////////////////////////////////////////////////////

    public int getId(){
        return this.id;
    }

    public Card[] getCards(){
        return this.cards;
    }

    public Card getCard(int cnt){
        if(cnt >= 0 && cnt < 5){
            return this.cards[cnt];
        }
        else{
            return null;
        }
    }

    /////////////////////////////////////////////////////////////

    public void setId(int id){this.id = id;}
    public void setCards(Card[] cards){
        this.cards = cards;
    }

    public void setCard(int cnt, Card card){
        if(cnt >= 0 && cnt < 5 && card != null){
            this.cards[cnt] = card;
        }
    }
}
