package application.persistance.repository;

import application.model.Card;

import java.util.UUID;

public interface CardRepositoryInterface {
    public UUID[] showUserCards(String token);
    public boolean checkAuthentication(String token);
    public Card generateCard(UUID uuid);
}
