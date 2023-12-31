package application.persistance.repository;

import application.persistance.DataAccessException;
import application.persistance.UnitOfWork;
import application.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository implements UserRepositoryInterface{
    private final UnitOfWork unitOfWork;
    public UserRepository(UnitOfWork unitOfWork){
        this.unitOfWork = unitOfWork;
    }

    /*///////////////////////////////////////////////////////////////////
    // initial development testing


    @Override
    public User findById(int id){
        try(PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                select * from public.users where id = ?
                """
        )){
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            User user = null;

            while(resultSet.next()) {
                user = new User(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3));
            }

            return user;
        }
        catch (SQLException e) {
            throw new DataAccessException("Select nicht erfolgreich", e);
        }
    }

    ///////////////////////////////////////////////////////////////////*/

    public boolean checkIfUserExists(User user){
        boolean userExisting;

        try(PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                select * from public.users where username = ?
                """
        )){
            preparedStatement.setString(1, user.getUsername());
            ResultSet resultSet = preparedStatement.executeQuery();

            //returns boolean weather resultSet contains more rows (false if not -> no entry existing)
            userExisting = resultSet.next();
        }
        catch (SQLException e) {
            throw new DataAccessException("Couldn't query for user", e);
        }

        return userExisting;
    }

    /////////////////////////////////////////////////////////////////////

    @Override
    public boolean createNewUser(User user){
        //1. check if username already existing
        boolean userExisting = checkIfUserExists(user);

        //2. if user not existing -> create new User
        if(!userExisting){
            System.out.println("Input: " + user.getUsername() + " | " + user.getPassword());

            try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                    """
                    INSERT INTO public.users (username, password) VALUES (?, ?)
                    """)) {
                preparedStatement.setString(1, user.getUsername());
                preparedStatement.setString(2, user.getPassword());

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    this.unitOfWork.commitTransaction();
                    System.out.println("Creation successful!");
                    return true;
                } else {
                    return false;
                }
            } catch (SQLException e) {
                throw new DataAccessException("Couldn't create new user", e);
            }
        }
        else{
            System.out.println("User already exists!");
            return false;
        }
    }

    /////////////////////////////////////////////////////////////////////

    @Override
    public boolean checkLogonInformation(User user){
        //1. check if username already existing
        boolean userExisting = checkIfUserExists(user);

        //2. if user existing -> check if password correct
        if(userExisting){
            try(PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                    """
                    SELECT (password) FROM public.users WHERE username = ?
                    """
            )){
                preparedStatement.setString(1, user.getUsername());
                ResultSet resultSet = preparedStatement.executeQuery();

                String dbPassword = "unfunctional";

                if(resultSet.next()){
                    dbPassword = resultSet.getString(1);
                }

                //3. compare dbPassword and given Password from http body
                return dbPassword.equals(user.getPassword());
            }
            catch (SQLException e) {
                throw new DataAccessException("Couldn't query for user", e);
            }
        }
        else{
            System.out.println("User not existing!");
            return false;
        }
    }

    @Override
    public void setUserToken(User user){
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                UPDATE users SET authtoken = ? WHERE username = ?
                """)) {

            String token = "Bearer " + user.getUsername() + "-mtcgToken";
            preparedStatement.setString(1, token);
            preparedStatement.setString(2, user.getUsername());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                this.unitOfWork.commitTransaction();
                System.out.println("Token added!");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Couldn't add token", e);
        }
    }
}
