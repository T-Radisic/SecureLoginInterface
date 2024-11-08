//Author: Theodora Radisic

//Account object

//This is the class for the object Account. Account is an object that consists of string pairs.
//The first string being a username and the second being a password. The object has multiple helper
//methods that allow a user to create an account, modify the username and password of an account, and 
//retrieve account information.

//In accountManaging.java (main program of project), only salted passwords are stored in account objects.


public class Account {
    //object variables
    private String accountId;
    private String password;

    public Account(String id, String pw){ //object constructor
        accountId = id;
        password = pw;
    }

    public void changeUsername(String nacc){ //helper function that changes the account's username
        accountId = nacc;
    }

    public void changePassword(String npw){ //helper function that changes the account's password
        password = npw;
    }

    public String getUsername(){ //retrieves the account's username
        return this.accountId;
    }

    public String getPassword(){ //retrieve's the account's password
        return this.password;
    }

    public String getInfo(){ //returns a string consisting of the account's username and password seperated by a space
        return this.accountId + " " + this.password;
    }


}
