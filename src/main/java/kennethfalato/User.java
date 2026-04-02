package kennethfalato;

import java.util.ArrayList;

public abstract class User {
    private String userName;
    private String password;
    private String type;



    public abstract String getType();

    User(String user, String pass){
        userName = user;
        password = pass;
    }

    public String getUserName(){
        return userName;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String p){
        password = p;
    }
    public void setUserName(String s){
        userName = s;
    }

}
