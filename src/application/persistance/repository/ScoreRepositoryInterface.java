package application.persistance.repository;

public interface ScoreRepositoryInterface {
    String[] buildScoreboard();

    String getUsernameByToken(String token);

    /////////////////////////////////////////////////////////////////////
    boolean authenticateUser(String token);

    /////////////////////////////////////////////////////////////////////
    int getWins(String username);

    int getLosses(String username);
}
