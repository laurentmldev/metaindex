


--
-- METAINDEX Database structure
-- ATTENTION : running this script will ERASE existing contents
--

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>
*/


select '######## CREATING METAINDEX SQL DATABASE STRUCTURE ########' as '';

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

CREATE DATABASE IF NOT EXISTS metaindex;

USE metaindex;

--
-- Base de données :  `metaindextest`
--


-- Cleaning if any

SET FOREIGN_KEY_CHECKS=0;

-- catalog tables
DROP TABLE if exists  catalog_vocabulary, 
            catalog_terms_vocabulary,
            catalog_terms,
            catalog_perspectives,
            filters
            ;


-- generic tables
DROP TABLE if exists user_roles, user_plans, plans, user_catalogs_rights, user_catalogs_customization, catalogs, users, guilanguages, guithemes;


SET FOREIGN_KEY_CHECKS=1;

SET @MX_DB_VERSION = 2;
-- --------------------------------------------------------

--
-- Structure de la table `catalogs`
--

CREATE TABLE `catalogs` (
`catalog_id` int(32) NOT NULL,
  `shortname` varchar(45) NOT NULL,
  `creator_id` int(32) NOT NULL,
  `thumbnailUrl` varchar(1024) DEFAULT '',
  
  /* which fields (coma-separated) shall be used (concatenated) in order
     to build the thumbails name */
  `itemNameFields` varchar(1024) DEFAULT '',
  
  /* which field shall be used to build the items thumbails URLs */
  `itemThumbnailUrlField` varchar(1024) DEFAULT '',
  
  /* urlPrefix to be applied to relative URLs (i.e. not starting with http */
  `urlPrefix` varchar(1024) DEFAULT '',

  /* field used to automaically activate perspective matching the name of given field */
  `perspectiveMatchField` varchar(1024) DEFAULT '',
  
  /* maximum of number allowed within this catalog */
  `quotaNbDocs` int(64) NOT NULL DEFAULT '5000',
  
  /* maximum disk space used by this catalogs's FTP data in bytes */
  `quotaFtpDiscSpaceBytes` int(64) NOT NULL DEFAULT '10000000',
  
  /* FTP port to be used to access userdata for this catalog */
  `ftpPort` int(32) NOT NULL DEFAULT '0',

  /* the (date) field to be used as default time reference in Kibana */
  `timeField_term_id` int(32) NULL,

  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;



-- --------------------------------------------------------

--
-- Structure de la table `catalog_terms`
--

CREATE TABLE `catalog_terms` (
`catalog_term_id` int(32) NOT NULL,
  `catalog_id` int(32) NOT NULL,
  -- this list shall be coherent with ICatalogTerm.TERM_DATATYPE enum definition.
  `datatype` ENUM('TINY_TEXT', 'RICH_TEXT', 'DATE', 'INTEGER', 'FLOAT', 'PAGE_URL', 'IMAGE_URL', 'AUDIO_URL', 'VIDEO_URL', 'GEO_POINT','LINK') ,
  `name` varchar(45) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `enumsList` varchar(1024) NOT NULL DEFAULT "",-- only meaningful if some enums are defined for this term
  `isMultiEnum` boolean NOT NULL DEFAULT false,-- only meaningful if some enums are defined for this term
  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;


-- --------------------------------------------------------

--
-- Structure de la table `catalog_perspectives`
--

CREATE TABLE `catalog_perspectives` (
  `catalog_perspective_id` int(32) NOT NULL,
  `catalog_id` int(32) NOT NULL,
  `name` varchar(45) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `perspective_json_string` varchar(32768) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

--
-- Contenu de la table `catalog_perspectives`
--

-- perspective_json_string= {
-- tabs: [
--    {
--      title:"my tab",
--      sections: [
--           {
--             title : "my section",
--             type: "mozaic", (mozaic|table)
--             align : "left", (left|center|right)
--             fields: [ 
--                nom : {
--                  size:"small", (small,medium,big)
--                  color:"normal",  (normal, black, red, yellow, green, orange, blue, purple)
--                  showTitle:"true"
--                  weight:"normal", (normal|bold|italic)
--                }
--              ]
--           }
--        ]
--      }
--  ]
-- }   
 

-- no default perspective defined, otherwise would need to create associated terms
-- INSERT INTO `catalog_perspectives` (`catalog_perspective_id`, `catalog_id`, `name`, `perspective_json_string`,`lastUpdate`) VALUES
-- (3, 1003, 'My Perspective 1', '{ tabs : [ { title:"Tab One", sections : [ { title :"Section 1", type : "table", align : "center", fields : [ { term : "nom", size : "small" } ] } ] },{ title:"Tab 2", sections : [ { title :"Section A", type : "mozaic", align : "center", fields : [ { term : "nom", size : "small" } ] }, { title :"section B", type : "table", align : "center", fields : [ { term : "prenom", size : "small" },{ term : "ville", size : "small" },{ term : "file", size : "big" } ] } ] } ] }','2016-03-26 08:49:43'),
-- (4, 1003, 'My Perspective 2', '{ tabs : [ { title:"Tab One", sections : [ { title :"Section 1", type : "mozaic", align : "center", fields : [ { term : "nom", size : "small" } ] } ] },{ title:"Tab 2", sections : [ { title :"Section AAA", type : "mozaic", align : "center", fields : [ { term : "nom", size : "small" } ] }, { title :"section BBB", type : "table", align : "center", fields : [ { term : "prenom", size : "small" },{ term : "ville", size : "small" },{ term : "file", size : "big" } ] } ] } ] }','2016-03-26 08:49:43')
-- ;

-- --------------------------------------------------------


-- --------------------------------------------------------
--
-- Structure de la table `catalog_terms_vocabulary`
--

CREATE TABLE `catalog_terms_vocabulary` (
  `catalog_terms_vocabulary_id` int(32) NOT NULL,
  `catalog_term_id` int(32) NOT NULL,
  `guilanguage_id` int(32) NOT NULL,
  `termTraduction` varchar(45) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL DEFAULT "",
  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;


-- --------------------------------------------------------

--
-- Structure de la table `catalog_vocabulary`
--

CREATE TABLE `catalog_vocabulary` (
`catalog_vocabulary_id` int(32) NOT NULL,
  `catalog_id` int(32) NOT NULL,
  `guilanguage_id` int(32) NOT NULL,
  `catalogName` varchar(45) NOT NULL DEFAULT "",
  `catalogComment` varchar(255) NOT NULL DEFAULT "",
  `itemTraduction` varchar(45) NOT NULL DEFAULT "",
  `itemsTraduction` varchar(45) NOT NULL DEFAULT "",
  `userTraduction` varchar(45) NOT NULL DEFAULT "",
  `usersTraduction` varchar(45) NOT NULL DEFAULT "",
  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `guilanguages`
--

CREATE TABLE `guilanguages` (
`guilanguage_id` int(32) NOT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `shortname` varchar(8) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,  
  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

--
-- Contenu de la table `guilanguages`
--

INSERT INTO `guilanguages` (`guilanguage_id`, `name`, `shortname`, `lastUpdate`) VALUES
(1, 'English', 'EN', '2016-02-21 21:15:19'),
(2, 'Français', 'FR', '2016-02-21 21:15:19'),
(3, 'Español', 'SP', '2016-02-21 21:15:19');

-- --------------------------------------------------------

--
-- Structure de la table `guithemes`
--

CREATE TABLE `guithemes` (
`guitheme_id` int(32) NOT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `shortname` varchar(32) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,  
  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

--
-- Contenu de la table `guithemes`
--

INSERT INTO `guithemes` (`guitheme_id`, `name`, `shortname`, `lastUpdate`) VALUES
(1, 'Silver Stone', 'silver', '2016-02-21 21:15:19'),
(2, 'Green Earth', 'greenearth', '2016-02-21 21:15:19'),
(3, 'Deep Blue', 'deepblue', '2016-02-21 21:15:19');

-- --------------------------------------------------------

--
-- Structure de la table `filters`
--

CREATE TABLE `filters` (
  `filter_id` int(32) NOT NULL,
  `catalog_id` int(32) NOT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `query` varchar(10240) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,    
  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;



-- --------------------------------------------------------

--
-- Structure de la table `users`
--

CREATE TABLE `users` (
`user_id` int(32) NOT NULL,
  `email` varchar(128) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `nickname` varchar(45) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `password` varchar(256) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL DEFAULT 'change_me',
  `enabled` tinyint(4) NOT NULL DEFAULT '0',
  `guilanguage_id` int(32) NOT NULL DEFAULT '1',
  `guitheme_id` int(32) NOT NULL DEFAULT '1',  
  -- max number of catalogs this user is authorized to create
  `maxNbCatalogsCreated` int(32) NOT NULL DEFAULT '1',  
  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;



--
-- Structure de la table `plans`
--

CREATE TABLE `plans` (
`plan_id` int(32) NOT NULL,
  `name` varchar(45) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `catalogsCreatedQuota` int(32) NOT NULL DEFAULT '0',
  `docsQuotaPerCatalog` int(32) NOT NULL DEFAULT '0',
  `discQuotaPerCatalog` int(32) NOT NULL DEFAULT '0',
  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;



--
-- Structure de la table `user_plans`
--

CREATE TABLE `user_plans` (
  `user_plan_id` int(32) NOT NULL,
  `user_id` int(32) NOT NULL,
  `plan_id` int(32) NOT NULL,
  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;


--
-- Structure de la table `user_roles`
--

CREATE TABLE `user_roles` (
`user_role_id` int(32) NOT NULL,
  `user_id` int(32) NOT NULL,
  `role` ENUM('ROLE_USER', 'ROLE_ADMIN', 'ROLE_OBSERVER') NOT NULL,
  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;




--
-- Structure de la table `user_catalogs_rights`
--

CREATE TABLE `user_catalogs_rights` (
`user_catalogs_rights_id` int(32) NOT NULL,
  `user_id` int(32) NOT NULL,
  `catalog_id` int(32) NOT NULL,
  `access_rights` ENUM('NONE', 'CATALOG_ADMIN', 'CATALOG_EDIT','CATALOG_READ') NOT NULL,
  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;




--
-- Structure de la table `user_catalogs_customization`
--

CREATE TABLE `user_catalogs_customization` (
`user_catalogs_customization_id` int(32) NOT NULL,
  `user_id` int(32) NOT NULL,
  `catalog_id` int(32) NOT NULL,
  `kibana_iframe` varchar(2048) CHARACTER SET utf8 COLLATE utf8_unicode_ci,
  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;



--
-- Index pour la table `catalogs`
--

ALTER TABLE `catalogs`
 ADD PRIMARY KEY (`catalog_id`), ADD UNIQUE KEY `name` (`shortname`), ADD KEY `FK_COMMUNITIES_CREATOR_ID` (`creator_id`);


--
-- Index pour la table `catalog_terms`
--
ALTER TABLE `catalog_terms`
 ADD PRIMARY KEY (`catalog_term_id`), ADD UNIQUE KEY `FK_COMMUNITYTERMS_COMMUNITY_ID` (`catalog_id`,`name`);

 
--
-- Index pour la table `catalog_perspectives`
--
ALTER TABLE `catalog_perspectives`
 ADD PRIMARY KEY (`catalog_perspective_id`), ADD UNIQUE KEY `FK_COMMUNITYPERSPECTIVES_COMMUNITY_ID` (`catalog_id`,`name`);

 
--
-- Index pour la table `filters`
--
ALTER TABLE `filters`
 ADD PRIMARY KEY (`filter_id`), 
 ADD KEY `FK_CATALOGS_COMMUNITY_ID` (`catalog_id`),
 ADD UNIQUE KEY `UK_CATALOGS_NAME` (`catalog_id`,`name`);

 
--
-- Index pour la table `catalog_terms_vocabulary`
--
ALTER TABLE `catalog_terms_vocabulary`
 ADD PRIMARY KEY (`catalog_terms_vocabulary_id`), 
 ADD UNIQUE KEY `U_COMMUNITYTERMSVOCABULARY_LANGUAGETERM` (`catalog_term_id`,`guilanguage_id`),
 ADD KEY `catalog_term_id_idx` (`catalog_term_id`),
 ADD KEY `guilanguage_id_idx` (`guilanguage_id`);

--
-- Index pour la table `catalog_vocabulary`
--
ALTER TABLE `catalog_vocabulary`
 ADD PRIMARY KEY (`catalog_vocabulary_id`), 
 ADD UNIQUE KEY `U_COMMUNITYVOCABULARY_COMMUNITYLANGUAGE` (`catalog_id`,`guilanguage_id`), 
 ADD KEY `FK_COMMUNITYVOCABULARY_COMMUNITY_ID` (`catalog_id`), 
 ADD KEY `FK_COMMUNITYVOCABULARY_GUILANGUAGE_ID` (`guilanguage_id`);

--
-- Index pour la table `guilanguages`
--
ALTER TABLE `guilanguages`
 ADD PRIMARY KEY (`guilanguage_id`);

--
-- Index pour la table `guithemes` 
ALTER TABLE `guithemes` 
  ADD PRIMARY KEY (`guitheme_id`);

--
-- Index pour la table `users`
--
ALTER TABLE `users`
 ADD PRIMARY KEY (`user_id`), 
 ADD UNIQUE KEY `email` (`email`), 
 ADD UNIQUE KEY `nickname` (`nickname`), 
 ADD KEY `FK_USERS_GUILANGUAGE_ID` (`guilanguage_id`), 
 ADD KEY `FK_USERS_GUITHEME_ID` (`guitheme_id`);

--
-- Index pour la table `plans`
--
ALTER TABLE `plans`
 ADD PRIMARY KEY (`plan_id`);


--
-- Index pour la table `user_plans`
--
ALTER TABLE `user_plans`
 ADD PRIMARY KEY (`user_plan_id`), 
 ADD UNIQUE KEY `uni_userid_planid` (`plan_id`,`user_id`), 
  ADD KEY `fk_userid_idx` (`user_id`),
  ADD KEY `fk_userplan_idx` (`plan_id`);


--
-- Index pour la table `user_roles`
--
ALTER TABLE `user_roles`
 ADD PRIMARY KEY (`user_role_id`), 
 ADD UNIQUE KEY `uni_userid_role` (`role`,`user_id`), 
  ADD KEY `fk_userid_idx` (`user_id`);

  
--
-- Index pour la table `user_catalogs_rights`
--
ALTER TABLE `user_catalogs_rights`
 ADD PRIMARY KEY (`user_catalogs_rights_id`), 
  ADD UNIQUE KEY `uni_userid_catalog_rights` (`user_id`,`catalog_id`), 
  ADD KEY `fk_rights_userid_idx` (`user_id`),
  ADD KEY `fk_rights_catalogid_idx` (`catalog_id`);

--
-- Index pour la table `user_catalogs_customization`
--
ALTER TABLE `user_catalogs_customization`
 ADD PRIMARY KEY (`user_catalogs_customization_id`), 
  ADD UNIQUE KEY `uni_userid_catalog_customization` (`user_id`,`catalog_id`), 
  ADD KEY `fk_custo_userid_idx` (`user_id`),
  ADD KEY `fk_custo_catalogid_idx` (`catalog_id`);

--
-- AUTO_INCREMENT pour la table `catalogs`
--
ALTER TABLE `catalogs`
MODIFY `catalog_id` int(32) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=1000;

--
-- AUTO_INCREMENT pour la table `catalog_terms`
--
ALTER TABLE `catalog_terms`
MODIFY `catalog_term_id` int(32) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=1000;


--
-- AUTO_INCREMENT pour la table `catalog_perspectives`
--
ALTER TABLE `catalog_perspectives`
MODIFY `catalog_perspective_id` int(32) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=1000;


--
-- AUTO_INCREMENT pour la table `filters`
--
ALTER TABLE `filters`
MODIFY `filter_id` int(32) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=1000;

--
-- AUTO_INCREMENT pour la table `catalog_terms_vocabulary`
--
ALTER TABLE `catalog_terms_vocabulary`
MODIFY `catalog_terms_vocabulary_id` int(32) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=1000;

--
-- AUTO_INCREMENT pour la table `catalog_vocabulary`
--
ALTER TABLE `catalog_vocabulary`
MODIFY `catalog_vocabulary_id` int(32) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=1000;
--
-- AUTO_INCREMENT pour la table `guilanguages`
--
ALTER TABLE `guilanguages`
MODIFY `guilanguage_id` int(32) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=1000;
--
-- AUTO_INCREMENT pour la table `guithemes`
--
ALTER TABLE `guithemes` 
MODIFY `guitheme_id` int(32) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=1000;
--
-- AUTO_INCREMENT pour la table `users`
--
ALTER TABLE `users`
MODIFY `user_id` int(32) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=1000;
--
-- AUTO_INCREMENT pour la table `user_plans`
--
ALTER TABLE `user_plans`
MODIFY `user_plan_id` int(32) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=1000;
--
-- AUTO_INCREMENT pour la table `user_roles`
--
ALTER TABLE `user_roles`
MODIFY `user_role_id` int(32) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=1000;

--
-- AUTO_INCREMENT pour la table `user_catalogs_rights`
--
ALTER TABLE `user_catalogs_rights`
MODIFY `user_catalogs_rights_id` int(32) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=1000;

--
-- AUTO_INCREMENT pour la table `user_catalogs_customization`
--
ALTER TABLE `user_catalogs_customization`
MODIFY `user_catalogs_customization_id` int(32) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=1000;


--
-- Contraintes pour les tables exportées
--

--
-- Contraintes pour la table `catalogs`
--
ALTER TABLE `catalogs`
ADD CONSTRAINT `C_FK_COMMUNITIES_CREATOR_ID` FOREIGN KEY (`creator_id`) REFERENCES `users` (`user_id`),
ADD CONSTRAINT `C_FK_CATALOGS_TIMEFIELD` FOREIGN KEY (`timeField_term_id`) REFERENCES `catalog_terms` (`catalog_term_id`) ON DELETE SET NULL;


--
-- Contraintes pour la table `catalog_terms`
--
ALTER TABLE `catalog_terms`
ADD CONSTRAINT `C_FK_COMMUNITYTERMS_COMMUNITY_ID` FOREIGN KEY (`catalog_id`) REFERENCES `catalogs` (`catalog_id`) ON DELETE CASCADE ;

--
-- Contraintes pour la table `catalog_perspectives`
--
ALTER TABLE `catalog_perspectives`
ADD CONSTRAINT `C_FK_COMMUNITYPERSPECTIVES_COMMUNITY_ID` FOREIGN KEY (`catalog_id`) REFERENCES `catalogs` (`catalog_id`) ON DELETE CASCADE ;

--
-- Contraintes pour la table `filters`
--
ALTER TABLE `filters`
ADD CONSTRAINT `C_FK_CATALOGS_COMMUNITY_ID` FOREIGN KEY (`catalog_id`) REFERENCES `catalogs` (`catalog_id`) ON DELETE CASCADE ;

--
-- Contraintes pour la table `catalog_terms_vocabulary`
--
ALTER TABLE `catalog_terms_vocabulary`
ADD CONSTRAINT `C_FK_COMMUNITYTERMSVOCABULARY_TERM_ID` FOREIGN KEY (`catalog_term_id`) REFERENCES `catalog_terms` (`catalog_term_id`) ON DELETE CASCADE ,
ADD CONSTRAINT `C_FK_COMMUNITYTERMSVOCABULARY_GUILANGUAGE_ID` FOREIGN KEY (`guilanguage_id`) REFERENCES `guilanguages` (`guilanguage_id`) ON DELETE CASCADE ;


--
-- Contraintes pour la table `catalog_vocabulary`
--
ALTER TABLE `catalog_vocabulary`
ADD CONSTRAINT `C_FK_COMMUNITYVOCABULARY_GUILANGUAGE_ID` FOREIGN KEY (`guilanguage_id`) REFERENCES `guilanguages` (`guilanguage_id`) ON DELETE CASCADE,
ADD CONSTRAINT `C_FK_COMMUNITYVOCABULARY_COMMUNITY_ID` FOREIGN KEY (`catalog_id`) REFERENCES `catalogs` (`catalog_id`) ON DELETE CASCADE ;

--
-- Contraintes pour la table `users`
--
ALTER TABLE `users`
ADD CONSTRAINT `C_FK_USERS_GUILANGUAGE_ID` FOREIGN KEY (`guilanguage_id`) REFERENCES `guilanguages` (`guilanguage_id`),
ADD CONSTRAINT `C_FK_USERS_GUITHEME_ID` FOREIGN KEY (`guitheme_id`) REFERENCES `guithemes` (`guitheme_id`);

--
-- Contraintes pour la table `user_plans`
--
ALTER TABLE `user_plans`
ADD CONSTRAINT `c_fk_userplans_userid` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
ADD CONSTRAINT `c_fk_userplans_planid` FOREIGN KEY (`plan_id`) REFERENCES `plans` (`plan_id`);

--
-- Contraintes pour la table `user_roles`
--
ALTER TABLE `user_roles`
ADD CONSTRAINT `c_fk_userid` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;
--
-- Contraintes pour la table `user_catalogs_rights`
--
ALTER TABLE `user_catalogs_rights`
ADD CONSTRAINT `c_fk_rights_userid` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
ADD CONSTRAINT `c_fk_rights_catalogid` FOREIGN KEY (`catalog_id`) REFERENCES `catalogs` (`catalog_id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `user_catalogs_customization`
--
ALTER TABLE `user_catalogs_customization`
ADD CONSTRAINT `c_fk_custo_userid` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
ADD CONSTRAINT `c_fk_custo_catalogid` FOREIGN KEY (`catalog_id`) REFERENCES `catalogs` (`catalog_id`) ON DELETE CASCADE;

