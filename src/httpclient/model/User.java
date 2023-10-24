package httpclient.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public class User {
    @JsonAlias({"id"})
    private Integer id;
    @JsonAlias({"username"})
    private String username;
    @JsonAlias({"password"})
    private String password;

    //default constructor for jackson
    public User(){

    }

    public User(Integer id, String username, String password){
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public Integer getId() {
        return this.id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername(){
        return this.username;
    }
    public void setUsername(String username){
        this.username = username;
    }

    public String getPassword(){
        return this.password;
    }
    public void setPassword(String password){
        this.password = password;
    }
}
