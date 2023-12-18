package application.persistance.repository;

import application.model.Card;
import application.persistance.DataAccessException;
import application.persistance.UnitOfWork;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CardRepository implements CardRepositoryInterface{
    private final UnitOfWork unitOfWork;
    public CardRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    @Override
    public boolean checkAuthentication(String token) {
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                SELECT (username) FROM public.users WHERE authtoken = ?
                """)) {

            preparedStatement.setString(1, token);

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            throw new DataAccessException("Couldn't authenticate user", e);
        }
    }

    @Override
    public UUID[] showUserCards(String token) {
        UUID[] cards = null;

        try(PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                SELECT stack FROM public.users WHERE authtoken = ?
                """
        )){
            preparedStatement.setString(1, token);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()){
                Array array = resultSet.getArray("stack");
                Object[] arrayData = (Object[]) array.getArray();

                cards = new UUID[arrayData.length];

                for (int i = 0; i < arrayData.length; i++) {
                    cards[i] = (UUID) arrayData[i];
                }
            } else{
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Couldn't query for package count", e);
        }

        return cards;
    }

    @Override
    public Card generateCard(UUID uuid) {

        Card card = new Card();

        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                SELECT * FROM public.cards WHERE cardid = ?
                """
        )) {

            preparedStatement.setObject(1, uuid);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                card.setCardid(uuid);
                card.setCardname(resultSet.getString("cardname"));
                card.setCardtype(resultSet.getString("cardtype"));
                card.setDamage(resultSet.getInt("damage"));

                return card;
            } else{
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Selecting random package with cards from DB failed");
            throw new RuntimeException(e);
        }
    }
}
