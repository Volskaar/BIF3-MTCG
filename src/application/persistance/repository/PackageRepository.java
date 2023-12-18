package application.persistance.repository;

import application.model.Package;
import application.persistance.UnitOfWork;
import application.persistance.DataAccessException;
import application.model.Card;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

public class PackageRepository implements PackageRepositoryInterface {
    private final UnitOfWork unitOfWork;

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

    public PackageRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    @Override
    public boolean createPackage(Card[] cards) {
        if (cards.length < 5) {
            return false;
        }

        //1. create cards to enter into DB
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                INSERT INTO public.packages (card1, card2, card3, card4, card5) VALUES (?,?,?,?,?)
                """)) {

            int cnt = 1;
            for (Card card : cards) {
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

    //////////////////////////////////////////////////////////////////////////////

    private boolean adjustUserCoins(String token){
        int amount = 0;

        // 1. check if user has enough coins
        try(PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                SELECT (coins) FROM public.users WHERE authtoken = ?
                """
        )){
            preparedStatement.setString(1, token);

            ResultSet resultSet = preparedStatement.executeQuery();

            // return false if not
            if (resultSet.next()){
                amount = resultSet.getInt(1);
            }

            if(amount < 5) {
                System.out.println("not enough coins from " + token);
                return false;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Couldn't query for coins", e);
        }

        // 2. if enough coins -> co(i)ntinue and update user table
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                UPDATE public.users SET coins = ? WHERE authtoken = ?
                """
        )) {

            preparedStatement.setInt(1, amount - 5);
            preparedStatement.setString(2, token);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected <= 0) {
                // Handle the case where the update fails for one or more UUIDs
                return false;
            }

            this.unitOfWork.commitTransaction();
            System.out.println("Coins updated for user " + token);
            return true;
        } catch (SQLException e) {
            System.out.println("Updating coins in user failed");
            throw new RuntimeException(e);
        }

    }

    //////////////////////////////////////////////////////////////////////////////

    private boolean checkIfPackagesAvailable(){
        int entryCnt = 0;

        try(PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                SELECT COUNT(*) AS entry_count FROM public.packages
                """
        )){

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()){
                entryCnt = resultSet.getInt("entry_count");
            } else{
                return false;
            }

            if(entryCnt < 1) {
                System.out.println("no packages left");
                return false;
            }

            return true;
        } catch (SQLException e) {
            throw new DataAccessException("Couldn't query for package count", e);
        }
    }

    //////////////////////////////////////////////////////////////////////////////

    private boolean removePackage(int packageID){
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                DELETE FROM public.packages WHERE packageid = ?
                """
        )) {

            preparedStatement.setInt(1, packageID);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected <= 0) {
                // Handle the case where the delete fails
                return false;
            }

            this.unitOfWork.commitTransaction();
            System.out.println("Package deleted after consumption");
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean acquirePackage(String token) {
        UUID[] cardUUIDs = new UUID[5];
        Card[] cards = new Card[5];
        int packageId = 0;

        //0. check if user has enough coins and if yes, subtract 5
        //0. check if enough packages available

        if(!checkIfPackagesAvailable()){
            return false;
        }

        if(!adjustUserCoins(token)){
            return false;
        }

        //1. pick random package from Package-DB and extract card-UUIDs
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                    """
                    SELECT * FROM public.packages ORDER BY RANDOM() LIMIT 1
                    """
        )) {

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                //set class variables
                packageId = resultSet.getInt(6);
                for(int i = 0; i<5; i++){
                    cardUUIDs[i] = (UUID) resultSet.getObject(i+1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Selecting random package with cards from DB failed");
            throw new RuntimeException(e);
        }

        //2. insert card-UUIDs into user-DB stack
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                UPDATE public.users SET stack = stack || ? WHERE authtoken = ?
                """
        )) {
            for (UUID cardUUID : cardUUIDs) {
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

        //3. remove package from DB
        return removePackage(packageId);
    }
}