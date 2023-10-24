package httpclient.persistance.repository;

import httpclient.persistance.DataAccessException;
import httpclient.persistance.UnitOfWork;
import httpclient.model.User;
import net.bytebuddy.dynamic.scaffold.MethodRegistry;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class UserRepository implements UserRepositoryInterface{
    private UnitOfWork unitOfWork;

    public UserRepository(UnitOfWork unitOfWork){
        this.unitOfWork = unitOfWork;
    }

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
}
