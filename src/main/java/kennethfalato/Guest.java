package kennethfalato;


import java.util.*;

public class Guest extends User{
    private String name;
    private String address;

        private static User currentGuest;

       public static void setCurrentGuest(User user){
        currentGuest = user;
       }

       public static User getCurrentGuest(){
        return currentGuest;
       }

    public String getType(){
        return "User";
    }

    Guest(String u, String p, String n, String a){
        super(u,p);
        name = n;
        address = a;
    }


    public String getName(){
        return name;
    }

    public String getAddress(){
        return address;
    }

    public void setAddress(String a){
        address = a;
    }

    public void setName(String n){
        name = n;
    }


}
