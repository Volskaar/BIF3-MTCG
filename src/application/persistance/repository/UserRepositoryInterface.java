package application.persistance.repository;

import application.model.User;

public interface UserRepositoryInterface {
    public boolean createNewUser(User user);
}
