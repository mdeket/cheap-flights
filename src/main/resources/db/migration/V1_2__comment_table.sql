CREATE TABLE IF NOT EXISTS comment(
   id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
   author INT NOT NULL,
   city INT NOT NULL,
   text varchar(1000) NOT NULL,
   timestamp DATETIME NOT NULL,
   FOREIGN KEY (author) REFERENCES user(id),
   FOREIGN KEY (city) REFERENCES city(id)
);