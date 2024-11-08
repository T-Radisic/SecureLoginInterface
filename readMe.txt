Secure Login Interface

Creator and Owner: Theodora Radisic tradisic@torontomu.ca

This project is an account creation and login interface that is resistant to SQL injections and uses password salting. All the data inputed by the user is read in as a string and checked if it's an SQL query. The program examines all user data input for common keywords - "SELECT", "FROM", and ";" combined with the previous two - that are found in SQL queries. This is to make the interface more secure.

The project has three main files (in src folder):
- Account class file: the class that represents a user account. The account object consists of two strings; username and password. In this project, each account object that's created consists of the username and salted password.
- Main program file: the interface program
- dictInfo.txt: text file that contains all the information from the accounts created. Each line in file represents one account. Each line has the following format; *username* *salt* *salt* *username salted password (account info)*. 

The interface database consists of two main hashmaps. One hashmap holds key value (kv) pairs of username:salt (salt from password corresponding to username), and the other hashmap holds kv pairs of salt:account (account object). The data in the two hashmaps are read in from the file dictInfo.txt in the beginning of the program. The data is then written back into the file (from the two hashmaps) before the program ends. dictInfo.text is overwritten but the two hashmaps contain all the data that is being overwritten and any new data that's been saved while the program was running.

Passwords are salted with a 20 character long salt consisting of random letters, numbers, and punctuation marks. The following static methods perform this operation: salter() and helper methods randomLetter(), randomPunct(). Salts are also used as secure keys for the interface hashmaps.

Account creation: It prompts user to input a username and password. The username cannot be taken by another account. The password must meet the following requirements: between 8 and 64 chars long (inclusive), has at least 1 digit, has at least one punctuation mark.

Login: User is prompted for their username and password. The user's account (object) is searched for using the inputed username and subsequently retrieved. The inputed username is compared to the account object username and the inputed password has the corresponding salt appended to it and then compared to the account object's salted password. If the inputed username and password match the account object username and password, the user logs in. When the user is logged in, they're given the options to change their username, change their password, or log out.

Running the Project:
to run this project, install the "Account Managing" folder from GITHUB and open the folder on VSCode or any other Java IDE. Then run the program in the IDE terminal as usual.

This project will be subject to updates and new versions of the project will be published accordingly. Updates will be created, managed, and published by Theodora Radisic.
