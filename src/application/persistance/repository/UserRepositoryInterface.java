package application.persistance.repository;

import application.model.User;

public interface UserRepositoryInterface {
    public boolean createNewUser(User user);
    public boolean checkIfUserExists(User user);
    public boolean checkLogonInformation(User user);
    public void setUserToken(User user);
    public String getUsernameByToken(String token);
    public boolean updateUserData(String username, String name, String bio, String image);
    public User getUser(String username);
}
