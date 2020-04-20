CREATE TABLE IF NOT EXISTS user(
   id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
   first_name varchar(30) NOT NULL,
   last_name varchar(50) NOT NULL,
   username varchar(50) NOT NULL,
   password varchar(128) NOT NULL,
   salt varchar(100) NOT NULL,
   role varchar(20) NOT NULL
)