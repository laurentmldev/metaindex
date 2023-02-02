
select '######## CREATING PROD DB ENTRIES ########' as '';

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

USE metaindex;

INSERT INTO `users` (`user_id`, `email`, `nickname`, `password`, `enabled`, `guilanguage_id`, `guitheme_id`, `lastUpdate`) VALUES
(1, 'laurentmlcontact-metaindex@yahoo.fr', 'MXADMIN', 'reset me', 1, 1, 1, '1981-06-11 18:00:00');


INSERT INTO `user_roles` (`user_role_id`, `user_id`, `role`, `lastUpdate`) VALUES
(1, 1, 'ROLE_ADMIN', '2016-02-21 21:15:19');


INSERT INTO `plans` ( `plan_id`, `name`, `availableForPurchase`, `category`,
                    `quotaCreatedCatalogs`, `quotaNbDocsPerCatalog`, `quotaDriveMBytesPerCatalog`, `yearlyCostEuros`) VALUES

/*
Dinos
Régiments romains (Cohortes,Manipules, ...)
Pokemon
Monaies (sersterces,livre tournoi, franc, euro,bitcoin)
Ages romains en latin
Planètes

*/
/* ALL */

/* PERSONAL */
(100,'Free Personal', false,'PERSONAL',                1, 100,      5,0),
(110,'Ant', true,'PERSONAL',                         1, 500,     500,14),
(120,'Bee', true,'PERSONAL',                         1, 5000,   1000,34),
(130,'Poney', true,'PERSONAL',                       1, 15000,  2000,57),
(140,'Panda', true,'PERSONAL',                       1, 30000,  5000,79),
(150,'Elephant',true,'PERSONAL',                     1, 50000, 10000,99),

/* STUDENT_SEARCHER */
(200,'Free Academic', false,'STUDENT_SEARCHER',      1, 250,      15,0),
(210,'Bilbo', true,'STUDENT_SEARCHER',               1, 500,      500,9),
(220,'Aragorn', true,'STUDENT_SEARCHER',             1, 5000,    1000,29),
(230,'Elrond', true,'STUDENT_SEARCHER',              1, 15000,   2000,39),
(240,'Galadriel', true,'STUDENT_SEARCHER',           1, 30000,   5000,59),
(250,'Gandalf',true,'STUDENT_SEARCHER',              1, 100000, 10000,89),

/* NONPROFIT */
(300,'Free Non-profit', false,'NONPROFIT',            1, 100,      5,0),
(310,'Pico', true,'NONPROFIT',                       1, 500,      500,29),
(320,'Micro', true,'NONPROFIT',                      1, 5000,    1000,89),
(330,'Kilo', true,'NONPROFIT',                       1, 30000,   5000,149),
(340,'Mega', true,'NONPROFIT',                       2, 100000, 10000,239),
(350,'Giga',true,'NONPROFIT',                        3, 500000, 20000,399),

/* ADMINISTRATION */
(400,'Free Administration', false,'ADMINISTRATION',  1, 100,      5,0),
(410,'Bonzai', true,'ADMINISTRATION',                  1, 500,      500,29),
(420,'Yucca', true,'ADMINISTRATION',                  1, 5000,    1000,89),
(430,'Eucalyptus', true,'ADMINISTRATION',                  1, 30000,   5000,149),
(440,'Baobab', true,'ADMINISTRATION',                  2, 100000, 10000,239),
(450,'Sequoia',true,'ADMINISTRATION',                  3, 500000, 20000,399),

/* BUSINESS */
(500,'Free Business', false,'BUSINESS',              1, 100,      5,0),
(510,'Starter', true,'BUSINESS',                     1, 500,      500,29),
(520,'Small', true,'BUSINESS',                       1, 5000,    1000,99),
(530,'Medium', true,'BUSINESS',                      1, 30000,   5000,229),
(540,'Big', true,'BUSINESS',                         2, 100000, 10000,349),
(550,'Huge',true,'BUSINESS',                         3, 500000, 20000,499),

(1000,'Admin-Demo',false,'PERSONAL', 50, 1000000, 5000,999999999);

INSERT INTO `user_plans` ( `user_id`, `plan_id`, `startDate`, `endDate` ) VALUES
(1,1000,'1970-01-01','2099-12-31');
