package application.persistance.repository;

import application.persistance.UnitOfWork;
import application.persistance.DataAccessException;
import application.model.Card;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class PackageRepository implements PackageRepositoryInterface{
    private final UnitOfWork unitOfWork;

    public boolean checkAuthentication(String token){
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                SELECT (username) FROM public.users WHERE authtoken = ?
                """)) {

            preparedStatement.setString(1, token);

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            throw new DataAccessException("Couldn't create new package", e);
        }
    }

    public PackageRepository(UnitOfWork unitOfWork){
        this.unitOfWork = unitOfWork;
    }

    @Override
    public boolean createPackage(Card[] cards){
        if(cards.length < 5) {
            return false;
        }

        //1. create cards to enter into DB
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                INSERT INTO public.packages (card1, card2, card3, card4, card5) VALUES (?,?,?,?,?)
                """)) {

            int cnt = 1;
            for(Card card : cards){
                preparedStatement.setObject(cnt, card.getId());
                cnt++;
            }

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                this.unitOfWork.commitTransaction();
                System.out.println("Package creation successful!");
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Couldn't create new package", e);
        }
    }

    @Override
    public boolean acquirePackage(){
        return true;
    }
}
