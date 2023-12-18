package application.persistance.repository;

import application.model.Card;

import java.util.UUID;

public interface DeckRepositoryInterface {
    public Card[] showDeck(String token);
    public boolean configureDeck(String token, UUID[] cardUUIDs);

    // ------------------------------------------------------------------

    public boolean checkAuthentication(String token);
    public boolean checkIfCardsInStack(String token, UUID[] uuids);

    public boolean removeCardsFromStack(String token, UUID[] uuids);

    public boolean addCardsToStack(String token, UUID[] uuids);

    public boolean checkCurrentDeck(String token, UUID[] uuids);
}
