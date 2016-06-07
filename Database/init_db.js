// create database
use chessapp_db

// add administrator accoount

db.users.insert({username : "admin", password : "$2a$10$RDuIMXLNFqiCLBK43WCTzOki6JHoAmSL0PkByAGoRvww6GwAtDrh2", role : 1, "isRegistrationConfirmed": true});
db.users.find().pretty();

// Your administrator account will be created, with login data:
// Login: admin
// password: Abcde1234%

