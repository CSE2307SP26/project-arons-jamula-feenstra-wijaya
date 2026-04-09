# Banking Application Project

## Team Members:

* Harrison Arons
* Natalia Jamula
* Andrew Feenstra
* Clifford Wijaya

## User stories completed

1. A user should be able to login to their personal accounts before their bank accounts. (Andrew)
2. A user should be able to create different types of accounts (checking or savings). (Natalia)
3. A user should be able to change their password. (Andrew)
4. A user should be able to transfer money into another user's account. (Clifford)
5. An admin should be able to void inter-user transfer transactions. (Clifford)
6. A user should be able to be locked out of their account for mistyping their password multiple times. (Harrison)
7. A user should be able to view a summary of all of their accounts. (Natalia)
8. A user should be able to filter their transaction history. (Harrison)

## User stories intended to complete next iteration
1. A user should be able to view timestamps for each transaction in their transaction history.
2. An admin should be able to view an audit log for their past actions.
3. An admin should be able to freeze an account. 
4. A user should receive a warning when their account balance falls below a minimum threshold.
5. A user should be able to export their transaction history to a text file.
6. A user should be charged a small fee when transferring funds between accounts.
7. A user should be able to add a note to each of their transactions.
8. A user should be able to undo their most recent transaction.

## Is there anything that you implemented but doesn't currently work?
For the inter-user transfer transactions, the void action works if you select the sending username and sending account, but it seems to break when selecting the receiving username and corresponding receiving account.

## What commands are needed to compile and run your code from the command line?
1. cd into project-arons-jamula-feenstra-wijaya
2. ./runApp.sh
Note: Admin Username is admin, Admin password is 123
