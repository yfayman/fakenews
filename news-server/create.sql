CREATE DATABASE `fakenews` /*!40100 DEFAULT CHARACTER SET latin1 */;

CREATE TABLE `user_type` (
  `user_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `ref_code` varchar(255) NOT NULL,
  PRIMARY KEY (`user_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

INSERT INTO user_type(ref_code) VALUES('ADMIN');
INSERT INTO user_type(ref_code) VALUES('USER');


CREATE TABLE `user` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_type_id` int(11) NOT NULL,
  `email` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `auth_token` varchar(255) DEFAULT NULL,
  `auth_token_exp` datetime DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email_UNIQUE` (`email`),
  KEY `user_type_id` (`user_type_id`),
  CONSTRAINT `user_ibfk_1` FOREIGN KEY (`user_type_id`) REFERENCES `user_type` (`user_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

CREATE TABLE `article_status` (
  `article_status_id` int(11) NOT NULL AUTO_INCREMENT,
  `ref_code` varchar(255) NOT NULL,
  PRIMARY KEY (`article_status_id`),
  UNIQUE KEY `ref_code` (`ref_code`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


INSERT INTO article_status (ref_code) VALUES ('ACTIVE');
INSERT INTO article_status (ref_code) VALUES ('PENDING');
INSERT INTO article_status (ref_code) VALUES ('DELETED');

CREATE TABLE `article` (
  `article_id` int(11) NOT NULL AUTO_INCREMENT,
  `article_url` varchar(2047) NOT NULL,
  `article_html` mediumtext NOT NULL,
  `article_status_id` int(11) NOT NULL,
  `user_id` int(11),
  `title` varchar(255) NOT NULL,
  PRIMARY KEY (`article_id`),
  UNIQUE (`article_url`),
  KEY `fk_article_1_idx` (`article_status_id`),
  KEY `fk_article_2_idx` (`user_id`),
  CONSTRAINT `fk_article_1` FOREIGN KEY (`article_status_id`) REFERENCES `article_status` (`article_status_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_article_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE `article_rating` (
  `article_rating_id` int(11) NOT NULL AUTO_INCREMENT,
  `ref_code` varchar(255) NOT NULL,
  PRIMARY KEY (`article_rating_id`),
  UNIQUE KEY `ref_code` (`ref_code`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO article_rating (ref_code) VALUES ('REAL');
INSERT INTO article_rating (ref_code) VALUES ('FAKE');

CREATE TABLE `fakenews`.`user_article_rating` (
  `user_article_rating_id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `article_id` INT NOT NULL,
  `article_rating_id` INT NOT NULL,
  PRIMARY KEY (`user_article_rating_id`),
  UNIQUE (`user_id`, `article_id`),
  INDEX `fk_user_article_rating_1_idx` (`user_id` ASC),
  INDEX `fk_user_article_rating_2_idx` (`article_id` ASC),
  INDEX `fk_user_article_rating_3_idx` (`article_rating_id` ASC),
  CONSTRAINT `fk_user_article_rating_1`
    FOREIGN KEY (`user_id`)
    REFERENCES `fakenews`.`user` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_article_rating_2`
    FOREIGN KEY (`article_id`)
    REFERENCES `fakenews`.`article` (`article_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_article_rating_3`
    FOREIGN KEY (`article_rating_id`)
    REFERENCES `fakenews`.`article_rating` (`article_rating_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

ALTER TABLE `fakenews`.`article` 
ADD COLUMN `short_description` VARCHAR(255) NOT NULL AFTER `title`;

