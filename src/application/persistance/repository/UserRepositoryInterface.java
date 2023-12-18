package application.persistance.repository;

import application.model.User;

public interface UserRepositoryInterface {
    public boolean createNewUser(User user);
    public boolean checkIfUserExists(User user);
    public boolean checkLogonInformation(User user);
    public void setUserToken(User user);
}
