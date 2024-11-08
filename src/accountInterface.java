//Author: Theodora Radisic

//Secure Login Interface

//This project is an account creation and login interface that is resistant to SQL injections and 
//uses password salting. All the data inputed by the user is checked if it's an SQL query (look at 
//method inputChecker()). Password salting is done by the method salter().

//The main program (current file) implements the account class (Account.java) to create user accounts

//Interface has two hashmaps: username:salt hashmap, and salt:account (account object) hashmap. The
//file dictInfo.txt contains all information for every account created. It's read into the two hashmaps
//in the beginning of the program, and hashmaps are written into the same file at the end of the program.

//Account creation: It prompts user to input a username and password. The inputed username must not
//be used by another account and the inputed password must meet the requirements. 

//Login: User is prompted to input their username and password. The inputed username and password is
//compared to the corresponding account's (which was retrieved) username and password. If they match,
//user is logged in and given the options to change their username or password, or log out.

//for more information on this project, look at the READ ME file in the project folder.


import java.util.Scanner;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

public class accountInterface {
    public static void main(String[] args) throws IOException {

        //The two hashmaps that make up the databases for the system
        HashMap<String, String> usernameSalt = new HashMap<>(); //holds kvs username:salt
        HashMap<String, Account> saltAccount = new HashMap<>(); //holds kvs salt:account

        Scanner reader = new Scanner(new File("src/dictInfo.txt"));
        readAccounts(usernameSalt, saltAccount, reader); //reading account info into hashmaps
        //the dictInfo file is formatted in the following way
        //each line is info for one account
        //line format: username, salt, salt, account info (username, salted password)
        //each word (in line) is separated by a space

        Scanner scanner = new Scanner(System.in); 
 
        while (true){ //while loop ensures that user can be reprompted if need be
            //prompting user to either create a new account or login
            System.out.println("Enter 1 to create a new account, enter 2 to login");
            String q1 = scanner.nextLine();

            if (inputChecker(q1)){ //checking if user input is an SQL query
                System.out.println("\nInvalid input");
                continue;
            }
            else if (q1.equals("1")){
                //create account functions
                String username;
                String password;
                while (true){ //while loop ensures that user can be reprompted for username if need be
                    System.out.println("\nPlease enter username: ");
                    username = scanner.nextLine();
    
                    if (inputChecker(username)){ //checking that user input isn't an SQL query
                        System.out.println("\nInvalid input");
                        continue;
                    }
                    if (usernameChecker(username, usernameSalt)){ //checking that username isn't taken
                        System.out.println("\nUsername "+username+" is already taken");
                        continue;
                    }
                    break;
                }

                while (true){ //ensuring that user can be reprompted if need be
                    System.out.println("\nPassword Requirements: \nBetween 8 and 64 characters long \nContain at least one number \nContain at least one punctuation mark");
                    
                    System.out.println("\nPlease enter password: "); //entering and re-entering password
                    password = scanner.nextLine();

                    if (inputChecker(password)){
                        //checking to ensure last two prompts aren't SQL queries
                        System.out.println("\nInvalid input");
                        continue;
                    }
                    
                    System.out.println("\nPlease confirm password: ");
                    String conpass = scanner.nextLine();

                    if (inputChecker(conpass)){ 
                        //checking to ensure last two prompts aren't SQL queries
                        System.out.println("\nInvalid input");
                        continue;
                    }
                    if (!passwordChecker(password)){ //checking to ensure passwords meets requirements
                        System.out.println("\nPassword does not meet requirements");
                        continue;
                    }
                    if (!(password.equals(conpass))){ //checking to ensure passwords match
                        System.out.println("\nPasswords to do match");
                    }
                    break;
                }

                password = salter(password); //salting password
                Account acc = new Account(username, password);
                recordAccount(usernameSalt, saltAccount, acc); //putting new account into hashmaps

                System.out.println("Account successfully created!");
                System.out.println("\nRestart program to login!");
                
                break;
            }
            else if (q1.equals("2")){
                //login
                while (true) {//ensures user can be reprompted for username and password if need be
                    
                    System.out.println("\nPlease enter username: ");
                    String un = scanner.nextLine();

                    if (inputChecker(un)){
                        //checking that last two inputs aren't SQL queries
                        System.out.println("\nInvalid input");
                        continue;
                    }

                    System.out.println("\nPLease enter password: ");
                    String pw = scanner.nextLine();

                    if (inputChecker(pw)){
                        //checking that last two inputs aren't SQL queries
                        System.out.println("\nInvalid input");
                        continue;
                    }
                    

                    //checking that username inputed exists. By extension checking that intended account exists
                    if (!(usernameChecker(un, usernameSalt))){
                        System.out.println("\nUsername or password incorrect. Please try again");
                        continue;
                    }
                    
                    String salt = usernameSalt.get(un);
                    //checking that inputed account credentials are correct for the intended user account
                    if (!(accountVerifier(un, pw, salt, saltAccount))){
                        System.out.println("\nUsername or password incorrect. Please try again");
                        continue;
                    }
                    else {
                        System.out.println("\nYou're logged in!");
                        Account acc = saltAccount.get(salt);
                        accountManager(acc, scanner, saltAccount, usernameSalt); //entering account managing
                        break;
                    }
                }
                break; 
            }
            else { //if any other number other than 1 or 2 is inputed
                System.out.println("\nInvalid input");
                continue;
            }
        }

        //writing info from hashmaps back into account info file
        FileWriter writer = new FileWriter("src/dictInfo.txt");
        writeAccounts(usernameSalt, saltAccount, writer);

        reader.close();
        scanner.close();
        writer.close();

    }

    public static boolean inputChecker (String str){
        //parameters: string - any data that was inputed by user
        //returns true if the input str contains "select", "from", or ";". which means that the str is most likely an sql query.
        //else returns false
        return (str.contains(";") & ((str.toLowerCase()).contains("select") || (str.toLowerCase()).contains("from")));

    }

    public static boolean usernameChecker (String str, HashMap usdict){
        //parameters: username (str) hashmap with kvs username:salt (usdict)
        //returns true if the username is in the system. false otherwise
        Set<String> usernames = usdict.keySet();
        return usernames.contains(str);
    }

    public static boolean passwordChecker (String str){
        //parameters: password (str)
        //returns true if the password meets the following requirements
        //between 8 and 64 chars long (inclusive)
        //has at least 1 digit
        //has at least one punctuation mark
        return ((8 <= str.length() & str.length() <= 64) & (str.matches(".*\\d.*")) & (str.matches(".*[\\p{Punct}].*"))); 

    }

    public static Character randomLetter (){
        //parameters: none
        //returns a randomly generated upper or lower case letter
        //used in salter function
        Random random = new Random();
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        
        return alphabet.charAt(random.nextInt(52));
        //upperbound is 52 bc there's 26 letters in the alphabet. 26 x 2 = 52 cause the var alphabet
        //consists of the alphabet twice

    }

    public static Character randomPunct (){
        //parameters: none
        //returns a randomly generated punctuation mark
        //used in salter function
        Random random = new Random();

        String puncts = "!\\\"#$%&'()*+,-./:;<=>?@[\\\\]^_`{|}~";
        return puncts.charAt(random.nextInt(puncts.length()));
        //upper bound for random is the length of the string puncts
    }

    public static String salter (String password){
        //parameters: password
        //takes in an unsalted password and returns the salted version of it
        //The salt is 20 chars long consisting of random characters and numbers that is to be 
        //appended to the end of the password

        Random random = new Random();

        String salt = "";

        int count = 0;

        while (count < 20){ //creates the salt that's 20 chars long
            int r = random.nextInt(3); //randomly select if next char of the salt is a letter, punct, or number
            // 0 for random letter, 1 for random punctuation mark, 2 for random number
            if (r == 0){
                salt +=  randomLetter();
            }
            else if (r == 1){
                salt +=  randomPunct();
            }
            else {
                salt +=  String.valueOf(random.nextInt(9));
            }
            count ++; //incrementing counter
        }

        return password + salt; //returning the salted password
    }

    public static void readAccounts(HashMap<String, String> usdict, HashMap<String, Account> saltacc, Scanner reader){
        //parameters: username:salt hashmap (usdict), salt:account hashmap (saltacc), reader
        //returns: none
        //reads in account information into the two hashmaps word by word
        while (reader.hasNext()){
            usdict.put(reader.next(), reader.next()); //putting kv username:salt
            saltacc.put(reader.next(), (new Account (reader.next(), reader.next()))); //putting kv salt:account
        }
    }

    public static void writeAccounts(HashMap<String, String> usdict, HashMap<String, Account> saltacc, FileWriter writer) throws IOException{
        //paramaters: username:salt hashmap (usdict), salt:account hashmap (saltacc), file writer (writer)
        //return: none
        //writing account info from the two hashmaps back into dictInfo file. The file gets overwritten with the hashmap info.
        //the hashmaps contain all info from file before overwriting and any new info from new accounts created.
        for (String key : usdict.keySet()){
            //reading info in line by line
            String salt = usdict.get(key);
            writer.write(key + " " + salt + " " + salt + " " + (saltacc.get(salt)).getInfo() + "\n");
            //line format: "*username* *salt* *salt* *account info (username password)*"
        }

    }
    public static void recordAccount(HashMap<String, String> usdict, HashMap<String, Account> saltacc, Account acc) {
        //parameters: username:salt hashmap (usdict), salt:account hashmap, account to be recorded (acc)
        //return none
        //Takes a new account that was created and logs it's info into the two hashmaps
        String user = acc.getUsername();
        String salt = (acc.getPassword()).substring((acc.getPassword()).length() - 20);

        usdict.put(user, salt);
        saltacc.put(salt, acc);

    }

    public static boolean accountVerifier (String user, String pass, String salt, HashMap<String, Account> saltacc){
        //parameters: username (user), password (pass), salt from password (salt), salt:account hashmap (saltacc)
        //returns true if the inputer username and password (user and pass) match the username and password of the user account
        Account acc = saltacc.get(salt);

        return (user.equals(acc.getUsername()) && (pass+salt).equals(acc.getPassword()));

    }

    public static void accountManager (Account acc, Scanner scanner, HashMap<String, Account> saltacc, HashMap<String, String> usdict){
        //parameters: user account that was logged into (acc), scanner to accept user input, salt:account hashmap (saltacc), username:salt hashmap (usdict)
        //return: none
        //this function manages any modifications a user wants to do to their account. Pretty much an account settings function.
        //The user has three options: change their username, change their password (a new salt is created for the new password), or log out

        while (true){ //ensures we can reprompt user for account modification options if need be
            System.out.println("\nEnter 1 to change username. \nEnter 2 to change password. \nEnter 3 to log out.");
            String input = scanner.nextLine();

            if (inputChecker(input) || !(input.equals("1") || input.equals("2") || (input.equals("3")))){ 
                //checking that user input isn't an SQL query and that it's the proper input that was prompted
                System.out.println("\nInvalid Input");
                continue;
            }
            else if (input.equals("1")){ //changing username of user account
                while (true){ //ensures user can be reprompted for new username if need be
                    System.out.println("\nPlease enter new username: ");
                    String nu = scanner.nextLine();

                    if (inputChecker(nu)){ //checking for SQL
                        System.out.println("\nInvalid Input");
                        continue;
                    }
                    else if (usernameChecker(nu, usdict)){ //checking that new username inputed isn't already taken
                        System.out.println("\nUsername "+nu+" is already taken");
                        continue;
                    }
                    else {
                        usdict.remove(acc.getUsername()); //remvoing the old username from the system hashmap
                        acc.changeUsername(nu); //updating username
                        recordAccount(usdict, saltacc, acc); //recording new username

                        System.out.println("\nUsername Successfully Changed!");
                        break;
                    }


                }
                continue;
            }
            else if (input.equals("2")){ //changing password
                while (true){ //ensures user can be reprompted indefinitely for new password if need be
                    System.out.println("\nPassword Requirements: \nBetween 8 and 64 characters long \nContain at least one number \nContain at least one punctuation mark");
                    System.out.println("\nPlease enter new password: ");
                    String np = scanner.nextLine();

                    if (inputChecker(np)){ //checking for SQL
                        System.out.println("\nInvalid Input");
                        continue;
                    }

                    System.out.println("Please confirm new password: ");
                    String conp = scanner.nextLine();

                    if (inputChecker(conp)){
                        System.out.println("\nInvalid Input");
                        continue;
                    }
                    else if (!passwordChecker(np)){ //checking that new password meets requirements
                        System.out.println("\nPassword does not meet requirements");
                        continue;
                    }
                    else if (!np.equals(conp)){
                        System.out.println("\nPasswords do not match");
                        continue;
                    }
                    else {
                        np = salter(np); //getting new salt for password
                        String oldS = usdict.get(acc.getUsername());

                        saltacc.remove(oldS); //removing old salt from system hashmap
                        acc.changePassword(np); //updating account password

                        recordAccount(usdict, saltacc, acc); //recording new password and corresponding new salt to hashmaps

                        System.out.println("\nPassword Successfully Changed!");
                        break;
                    }

                }
                continue;
            }
            else { //if user inputed 3, they exit the function to log out
                return;
            }
        }

    }
}
