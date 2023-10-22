package httpclient.persistance.repository;

import httpclient.model.User;

public interface UserRepositoryInterface {
    User findById(int id);
}
