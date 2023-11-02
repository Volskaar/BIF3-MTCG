package application.persistance.repository;

import application.persistance.UnitOfWork;
import application.persistance.DataAccessException;
import application.model.Card;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PackageRepository {
    private final UnitOfWork unitOfWork;
    public PackageRepository(UnitOfWork unitOfWork){
        this.unitOfWork = unitOfWork;
    }

    public static boolean createPackage(Card[] cards){
        return true;
    }

    public boolean acquirePackage(){
        return true;
    }
}
