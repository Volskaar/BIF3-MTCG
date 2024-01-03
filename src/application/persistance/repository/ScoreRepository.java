package application.persistance.repository;

import application.persistance.DataAccessException;
import application.persistance.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ScoreRepository implements ScoreRepositoryInterface{
    private final UnitOfWork unitOfWork;
    public ScoreRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    /////////////////////////////////////////////////////////////////////

    @Override
    public String[] buildScoreboard() {

        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                SELECT username, wins, losses FROM public.users ORDER BY wins DESC
                """
        )) {

            ResultSet resultSet = preparedStatement.executeQuery();

            String[] scoreboard = new String[this.getRowCount()];
            int cnt = 0;

            while (resultSet.next()) {
                String username = resultSet.getString("username");
                int wins = resultSet.getInt("wins");
                int losses = resultSet.getInt("losses");

                String entry = String.format("Username: %s, Wins: %d, Losses: %d", username, wins, losses);
                scoreboard[cnt] = entry;
                cnt++;
            }

            return scoreboard;
        } catch (SQLException e) {
            throw new DataAccessException("Couldn't read wins", e);
        }
    }

    /////////////////////////////////////////////////////////////////////

    private int getRowCount() {
        int rowCount = 0;

        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                SELECT username FROM public.users
                """
        )) {

            ResultSet resultSet = preparedStatement.executeQuery();

            // Count the number of rows
            while (resultSet.next()) {
                rowCount++;
            }

        } catch (SQLException e) {
            throw new DataAccessException("Couldn't count user entries", e);
        }

        return rowCount;
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
    public boolean authenticateUser(String token){
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

    /////////////////////////////////////////////////////////////////////
    @Override
    public int getWins(String username){
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                SELECT wins FROM public.users WHERE username = ?
                """)) {

            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                return resultSet.getInt(1);
            }
            else{
                System.out.println("couldn't reach wins from user: " + username);
                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Couldn't read wins", e);
        }
    }

    @Override
    public int getLosses(String username){
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                SELECT losses FROM public.users WHERE username = ?
                """)) {

            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                return resultSet.getInt(1);
            }
            else{
                System.out.println("couldn't reach losses from user: " + username);
                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Couldn't read losses", e);
        }
    }
}
