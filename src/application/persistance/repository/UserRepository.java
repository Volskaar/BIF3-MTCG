package application.persistance.repository;

import application.persistance.DataAccessException;
import application.persistance.UnitOfWork;
import application.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository implements UserRepositoryInterface {
    private final UnitOfWork unitOfWork;

    public UserRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    public boolean checkIfUserExists(User user) {
        boolean userExisting;

        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                        select * from public.users where username = ?
                        """
        )) {
            preparedStatement.setString(1, user.getUsername());
            ResultSet resultSet = preparedStatement.executeQuery();

            //returns boolean weather resultSet contains more rows (false if not -> no entry existing)
            userExisting = resultSet.next();
        } catch (SQLException e) {
            throw new DataAccessException("Couldn't query for user", e);
        }

        return userExisting;
    }

    /////////////////////////////////////////////////////////////////////

    @Override
    public boolean createNewUser(User user) {
        //1. check if username already existing
        boolean userExisting = checkIfUserExists(user);

        //2. if user not existing -> create new User
        if (!userExisting) {
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
                    System.out.println("Creation failed!");
                    return false;
                }
            } catch (SQLException e) {
                throw new DataAccessException("Couldn't create new user", e);
            }
        } else {
            System.out.println("User already exists!");
            return false;
        }
    }

    /////////////////////////////////////////////////////////////////////

    @Override
    public boolean checkLogonInformation(User user) {
        //1. check if username already existing
        boolean userExisting = checkIfUserExists(user);

        //2. if user existing -> check if password correct
        if (userExisting) {
            try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                    """
                            SELECT (password) FROM public.users WHERE username = ?
                            """
            )) {
                preparedStatement.setString(1, user.getUsername());
                ResultSet resultSet = preparedStatement.executeQuery();

                String dbPassword = "unfunctional";

                if (resultSet.next()) {
                    dbPassword = resultSet.getString(1);
                }

                //3. compare dbPassword and given Password from http body
                return dbPassword.equals(user.getPassword());
            } catch (SQLException e) {
                throw new DataAccessException("Couldn't query for user", e);
            }
        } else {
            System.out.println("User not existing!");
            return false;
        }
    }

    /////////////////////////////////////////////////////////////////////

    @Override
    public void setUserToken(User user) {
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

    /////////////////////////////////////////////////////////////////////

    @Override
    public String getUsernameByToken(String token) {
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                        SELECT (username) FROM public.users WHERE authtoken = ?
                        """
        )) {
            preparedStatement.setString(1, token);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return(resultSet.getString(1));
            }else{
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Couldn't query for username", e);
        }
    }

    /////////////////////////////////////////////////////////////////////

    @Override
    public boolean updateUserData(String username, String name, String bio, String image) {
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                    UPDATE users SET name = ?, bio = ?, image = ? WHERE username = ?
                    """
        )) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, bio);
            preparedStatement.setString(3, image);
            preparedStatement.setString(4, username);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                this.unitOfWork.commitTransaction();
                System.out.println("User updated!");
                return true;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Couldn't update user", e);
        }

        return false;
    }

    /////////////////////////////////////////////////////////////////////

    @Override
    public User getUser(String username){

        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                        SELECT name, bio, image FROM public.users WHERE username = ?
                        """
        )) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                User user = new User();

                user.setUsername(username);
                user.setName(resultSet.getString(1));
                user.setBio(resultSet.getString(2));
                user.setImage(resultSet.getString(3));

                return user;
            }else{
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Couldn't query for username", e);
        }
    }
}
