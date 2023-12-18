package application.persistance.repository;

import application.model.Card;
import application.persistance.DataAccessException;
import application.persistance.UnitOfWork;
import httpserver.server.Request;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.UUID;

public class DeckRepository implements DeckRepositoryInterface{
    private final UnitOfWork unitOfWork;

    public DeckRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    @Override
    public Card[] showDeck(String token) {
        Card[] cards = new Card[4];
        UUID[] cardUUIDs;

        //1. get card UUIDs from deck
        cardUUIDs = getDeckUUIDs(token);
        if(cardUUIDs == null){
            return new Card[0];
        }

        //2. turn UUIDs into cards
        for(int i=0; i < cardUUIDs.length; i++){
            cards[i] = generateCard(cardUUIDs[i]);
        }

        //3. return card array
        return cards;
    }

    @Override
    public boolean configureDeck(String token, UUID[] cardUUIDs) {
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                UPDATE public.users SET deck = ? WHERE authtoken = ?
                """
        )) {

            preparedStatement.setObject(1, cardUUIDs);
            preparedStatement.setString(2, token);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected <= 0) {
                // Handle the case where the update fails for one or more UUIDs
                return false;
            }

            this.unitOfWork.commitTransaction();
            System.out.println("Deck updated for user " + token);
            return true;
        } catch (SQLException e) {
            System.out.println("Updating deck in user failed");
            throw new RuntimeException(e);
        }
    }

    /* ------------------------ external allowance functions ------------------------ */
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
    public boolean checkIfCardsInStack(String token, UUID[] uuids){

        //1. get all cards from stack
        UUID[] stackCardUUIDs = null;

        try(PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                SELECT stack FROM public.users WHERE authtoken = ?
                """
        )){
            preparedStatement.setString(1, token);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()){
                //2. turn array object into array of UUIDs
                Array array = resultSet.getArray("stack");
                Object[] arrayData = (Object[]) array.getArray();

                stackCardUUIDs = new UUID[arrayData.length];

                for (int i = 0; i < arrayData.length; i++) {
                    stackCardUUIDs[i] = (UUID) arrayData[i];
                }
            } else{
                System.out.println("No cards in stack of user with token: " + token);
                return false;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Couldn't query for package count", e);
        }

        //3. loop through deck and see if all cards are present in stack
        for(UUID deckCardUUID : uuids){
            if(!Arrays.asList(stackCardUUIDs).contains(deckCardUUID)){
                System.out.println("Card with uuid: " + deckCardUUID + " not found!");
                return false;
            }
        }

        System.out.println("All cards for deck found in stack");
        return true;
    }
    @Override
    public boolean removeCardsFromStack(String token, UUID[] deckCardUUIDs){
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
            """
                UPDATE public.users
                SET stack = array(
                    SELECT unnest(stack)
                    EXCEPT
                    SELECT unnest(?)
                )
                WHERE authtoken = ?;
                """
        )) {

            preparedStatement.setObject(1, deckCardUUIDs);
            preparedStatement.setString(2, token);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected <= 0) {
                // Handle the case where the update fails for one or more UUIDs
                return false;
            }

            this.unitOfWork.commitTransaction();
            System.out.println("Deck cards removed from stack " + token);
            return true;
        } catch (SQLException e) {
            System.out.println("Updating stack/deck in user failed");
            throw new RuntimeException(e);
        }
    }
    @Override
    public boolean addCardsToStack(String token, UUID[] deckCardUUIDs){
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                UPDATE public.users SET stack = stack || ? WHERE authtoken = ?
                """
        )) {
            for (UUID cardUUID : deckCardUUIDs) {
                preparedStatement.setObject(1, new UUID[]{cardUUID});
                preparedStatement.setString(2, token);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected <= 0) {
                    // Handle the case where the update fails for one or more UUIDs
                    System.out.println("Adding cards to user stack failed");
                    return false;
                }
            }

            this.unitOfWork.commitTransaction();
            System.out.println("Cards added to stack");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public boolean checkCurrentDeck(String token, UUID[] uuids){
        UUID[] cardUUIDs = getDeckUUIDs(token);

        if(cardUUIDs == null){
            return false;
        }

        return cardUUIDs.length == 4;
    }

    /* ------------------------ internal assistance functions ------------------------ */

    private UUID[] getDeckUUIDs(String token){

        UUID[] cards = null;

        try(PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                SELECT deck FROM public.users WHERE authtoken = ?
                """
        )){
            preparedStatement.setString(1, token);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()){
                Array array = resultSet.getArray("deck");

                //failsafe if empty
                if(array == null){
                    System.out.println("Deck is empty, return null");
                    return null;
                }

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

    private Card generateCard(UUID uuid) {

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
