package application.persistance.repository;

import application.persistance.UnitOfWork;
import application.persistance.DataAccessException;
import application.model.Card;
import application.service.BaseService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class PackageRepository implements PackageRepositoryInterface{
    private final UnitOfWork unitOfWork;
    public PackageRepository(UnitOfWork unitOfWork){
        this.unitOfWork = unitOfWork;
    }

    BaseService service;

    @Override
    public boolean createPackage(Card[] cards){
        if(cards.length < 5){
            return false;
        }

        //1. create cards to enter into DB
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                INSERT INTO public.packages (id, cards) VALUES (DEFAULT, {?,?,?,?,?})
                """)) {

            for(Card card : cards){
                try{
                    String json = service.getObjectMapper().writeValueAsString(card);
                    preparedStatement.setString(1, json);
                }
                catch(JsonProcessingException e){
                    throw new RuntimeException(e);
                }
            }

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                this.unitOfWork.commitTransaction();
                System.out.println("Creation successful!");
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
