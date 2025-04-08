-- Drop database if it exists
DROP DATABASE IF EXISTS Samudrala;
CREATE DATABASE Samudrala;
USE Samudrala;

-- Drop the Event table if it exists
DROP TABLE IF EXISTS Event;

-- Create the Event table
CREATE TABLE Event (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name TEXT NOT NULL,
    event_date TEXT NOT NULL,
    event_time TEXT NOT NULL,
    description TEXT NOT NULL
);

-- Drop users if they already exist
DROP USER IF EXISTS 'event_admin'@'localhost';
DROP USER IF EXISTS 'event_user'@'192.168.56.1';

-- Create an admin user with full privileges on Samudrala
CREATE USER 'event_admin'@'localhost' IDENTIFIED BY 'pass';
GRANT ALL PRIVILEGES ON Samudrala.* TO 'event_admin'@'localhost';

-- Create a user with read/write access on Samudrala.Event
CREATE USER 'event_user'@'192.168.56.1' IDENTIFIED BY 'user';
GRANT SELECT, INSERT ON Samudrala.Event TO 'event_user'@'192.168.56.1';

-- Apply the privilege changes
FLUSH PRIVILEGES;
