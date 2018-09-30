-- phpMyAdmin SQL Dump
-- version 4.2.5
-- http://www.phpmyadmin.net
--
-- Client :  localhost:8889
-- Généré le :  Sam 26 Mars 2016 à 09:50
-- Version du serveur :  5.5.38
-- Version de PHP :  5.5.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

--
-- Base de données :  `metaindextest`
--

-- --------------------------------------------------------

--
-- Structure de la table `communities`
--

CREATE TABLE `communities` (
`community_id` int(11) NOT NULL,
  `idName` varchar(45) NOT NULL,
  `creator_id` int(11) NOT NULL,
  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1004 ;

--
-- Contenu de la table `communities`
--

INSERT INTO `communities` (`community_id`, `idName`, `creator_id`, `lastUpdate`) VALUES
(1003, 'Test Community', 19, '2016-03-26 08:49:43');

-- --------------------------------------------------------

--
-- Structure de la table `community_groups`
--

CREATE TABLE `community_groups` (
`community_group_id` int(11) NOT NULL,
  `community_id` int(11) NOT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `comment` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL DEFAULT "",
  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=7 ;

--
-- Contenu de la table `community_groups`
--

INSERT INTO `community_groups` (`community_group_id`, `community_id`, `name`, `comment`, `lastUpdate`) VALUES
(4, 1003, 'Admin', 'Can create,modify,delete any data of the community, and manage groups and users', '2016-03-26 08:49:43'),
(5, 1003, 'Observers', 'Can only have a visualize data, but cannot do any modification', '2016-03-26 08:49:43'),
(6, 1003, 'Workers', 'Can create data, and modify,delete data he owns', '2016-03-26 08:49:43');

-- --------------------------------------------------------

--
-- Structure de la table `community_access_elements`
--

CREATE TABLE `community_access_elements` (
`community_access_element_id` int(11) NOT NULL,
  `community_group_id` int(11) NOT NULL,
  `community_element_id` int(11) NOT NULL,
  `accessFlag` int(11) NOT NULL DEFAULT "0",
  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `community_catalogs`
--

CREATE TABLE `community_catalogs` (
`community_catalog_id` int(11) NOT NULL,
  `community_id` int(11) NOT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `comment` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL DEFAULT "",
  `search_query` varchar(1024) NOT NULL DEFAULT "",
  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1000 ;

--
-- Contenu de la table `community_catalogs`
-- The search_query 'second' reference the title of a metadata for element id=1000
-- Element 1001 is reference statically
-- As a result the test catalog contains 2 elements.
--

INSERT INTO `community_catalogs` (`community_catalog_id`,`community_id`, `name`, `comment`, `search_query`) VALUES
(1000, 1003, 'Test Catalog', 'A test catalog mixng static and dynamic elements', 'second'),
(1001, 1003, 'Another Catalog', 'This is a good alternative', 'data');

-- --------------------------------------------------------

--
-- Structure de la table `community_elements`
--

CREATE TABLE `community_elements` (
`community_element_id` int(11) NOT NULL,
  `community_id` int(11) NOT NULL,
  `name` varchar(1024) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL DEFAULT "Element",
  `comment` varchar(1024) CHARACTER SET utf8 COLLATE utf8_unicode_ci  NOT NULL DEFAULT "",
  `templateRefElementId` int(11) NULL DEFAULT NULL,
  `thumbnailUrl` varchar(1024) CHARACTER SET utf8 COLLATE utf8_unicode_ci  NOT NULL DEFAULT "",
  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FULLTEXT (name, comment)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1001 ;

--
-- Contenu de la table `community_elements`
--

INSERT INTO `community_elements` (`community_element_id`,`community_id`, `name`,`comment`,`templateRefElementId` ) VALUES
(1000, 1003, 'A test element', 'This element shall be dynamically referenced by the test catalog',NULL),
(1001, 1003, 'Another test element', 'This element shall be deleted by the unit test',NULL),
(1010, 1003, 'Template Ref', 'Test template ref element',NULL),
(1020, 1003, 'Template Instance', 'Test template instance element','1010'),
(1666, 1003, 'Element to be deleted', 'This element shall be deleted by the unit test','1010');

-- --------------------------------------------------------

--
-- Structure de la table `community_templates`
--

CREATE TABLE `community_templates` (
  `community_element_id` int(11) NOT NULL,
  `isTemplate` int(11) NOT NULL DEFAULT '1',
  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1001 ;

--
-- Contenu de la table `community_templates`
--

INSERT INTO `community_templates` (`community_element_id`) VALUES
(1010);

-- --------------------------------------------------------
--
-- Structure de la table `community_datasets`
--

CREATE TABLE `community_datasets` (
`community_dataset_id` int(11) NOT NULL,
  `community_element_id` int(11) NOT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `comment` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL DEFAULT "",
  `layoutNbColumns` int(11) NOT NULL DEFAULT '1',
  `layoutPosition` int(11) NOT NULL DEFAULT '1',
  `layoutAlwaysExpand` int(11) NOT NULL DEFAULT '0',
  `layoutDoDisplayName` int(11) NOT NULL DEFAULT '1',
  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FULLTEXT (name, comment)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1001 ;

--
-- Contenu de la table `community_datasets`
--

INSERT INTO `community_datasets` (`community_dataset_id`,`community_element_id`, `name`,`comment`,`layoutNbColumns`) VALUES
(1000, 1000, 'A test dataset','','3'),
(1001, 1000, 'Another test dataset','','3'),
(1010, 1010, 'A template dataset','template ref version','2'),
(1011, 1010, 'Another template dataset','template ref version','4'),
(1020, 1020, 'A template dataset','overriding original template','3'),
(1021, 1020, 'Another templated dataset','not overriding original template because name is different','4'),
(1022, 1020, 'an extra templated dataset','should not appear','1'),
(1667, 1666, 'A test dataset which will be indirectly deleted','','3'),
(1668, 1666, 'A test dataset which will be indirectly deleted','','3'),
(1666, 1000, 'A test dataset to be deleted','','3');

-- --------------------------------------------------------

--
-- Structure de la table `community_metadata`
--

CREATE TABLE `community_metadata` (
`community_metadata_id` int(11) NOT NULL,
  `community_dataset_id` int(11) NOT NULL,
  `community_term_id` int(11) NOT NULL DEFAULT '4',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL DEFAULT 'Field Name',
  `comment` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL DEFAULT "",
  `layoutColumn` int(11) NOT NULL DEFAULT '1',
  `layoutPosition` int(11) NOT NULL DEFAULT '1',
  `layoutDoDisplayName` int(11) NOT NULL DEFAULT '1',
  `layoutAlign` varchar(45) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL DEFAULT 'center',
  `layoutSize` varchar(45) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL DEFAULT 'normal',
  `valueString1` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL DEFAULT "",
  `valueString2` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL DEFAULT "",
  `valueString3` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL DEFAULT "",
  `valueString4` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL DEFAULT "",
  `valueLongString` varchar(4080) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL DEFAULT "",
  `valueNumber1` float NOT NULL DEFAULT '0',
  `valueNumber2` float NOT NULL DEFAULT '0',
  `valueNumber3` float NOT NULL DEFAULT '0',
  `valueNumber4` float NOT NULL DEFAULT '0',
  `valueBoolean1` boolean NOT NULL DEFAULT false,
  `valueBoolean2` boolean NOT NULL DEFAULT false,
  `valueBoolean3` boolean NOT NULL DEFAULT false,
  `valueBoolean4` boolean NOT NULL DEFAULT false,
  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FULLTEXT (name, comment,valueString1,valueString2,valueString3,valueString4,valueLongString)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1000 ;
 
--
-- Contenu de la table `community_metadata`
--

INSERT INTO `community_metadata` (`community_metadata_id`,`community_dataset_id`, `name`) VALUES
(1000,1000, 'First test metadata'),
(1001,1000, 'Second test metadata'),
(1666,1000, 'Metadata to be deleted'),
(1667,1667, 'Another Metadata to be deleted'),
(1668,1667, 'Again a Metadata to be deleted');
INSERT INTO `community_metadata` (`community_metadata_id`,`community_dataset_id`, `name`,`layoutColumn`,`layoutPosition`) VALUES
(1002,1000, 'second column','2','4'),
(1003,1000, 'in the 3rd col','3','2'),
(1004,1000, 'in the 3rd col too','3','3');

-- for Templates testing
INSERT INTO `community_metadata` (`community_metadata_id`,`community_dataset_id`, `name`,`layoutColumn`,`valueNumber1`,`valueString1`) VALUES
(1010,1010, 'template metadata 1','1','2','text 1'),
(1011,1010, 'template metadata 2','2','4','text 2'),
(1012,1011, 'template metadata 3','3','6','text 3'),
(1020,1020, 'template metadata 1','3','8','text 4'),
(1021,1020, 'templated metadata 2','3','3','text 5'),
(1022,1021, 'templated metadata 3','2','5','text 6'),
(1023,1021, 'templated metadata 4','1','0','text 7');
-- --------------------------------------------------------

--
-- Structure de la table `community_static_catalogs_elements`
--

CREATE TABLE `community_static_catalogs_elements` (
  `community_static_catalogs_element_id` int(11) NOT NULL,
  `community_catalog_id` int(11) NOT NULL,
  `community_element_id` int(11) NOT NULL,
  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Contenu de la table `community_static_catalogs_elements`
--

INSERT INTO `community_static_catalogs_elements` (`community_catalog_id`,`community_element_id`) VALUES
(1000,1000);

-- --------------------------------------------------------

--
-- Structure de la table `community_terms`
--

CREATE TABLE `community_terms` (
`community_term_id` int(11) NOT NULL,
  `community_id` int(11) NOT NULL,
  `datatype_id` int(11) NOT NULL,
  `idName` varchar(45) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `isEnum` tinyint(4) NOT NULL DEFAULT '0',
  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

--
-- Contenu de la table `community_terms`
--

INSERT INTO `community_terms` (`community_term_id`, `community_id`, `datatype_id`, `idName`, `isEnum`, `lastUpdate`) VALUES
(3, 1003, 5, 'Title', 10, '2016-03-26 08:49:43'),
(4, 1003, 5, 'Author', 10, '2016-03-26 08:49:43'),
(5, 1003, 5, 'Illustration', 4, '2016-03-26 08:49:43'),
(666, 1003, 5, 'term to be deleted', 10, '2016-03-26 08:49:43');

-- --------------------------------------------------------

--
-- Structure de la table `community_terms_vocabulary`
--

CREATE TABLE `community_terms_vocabulary` (
`community_terms_vocabulary_id` int(11) NOT NULL,
  `community_term_id` int(11) NOT NULL,
  `guilanguage_id` int(11) NOT NULL,
  `termNameTraduction` varchar(45) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL DEFAULT "",
  `termCommentTraduction` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL DEFAULT "",
  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=13 ;

--
-- Contenu de la table `community_terms_vocabulary`
--

INSERT INTO `community_terms_vocabulary` (`community_terms_vocabulary_id`, `community_term_id`, `guilanguage_id`, `termNameTraduction`, `termCommentTraduction`, `lastUpdate`) VALUES
(7, 3, 1, 'Title', 'The title of the element', '2016-03-26 08:49:43'),
(8, 4, 1, 'Author', 'The author of the element', '2016-03-26 08:49:43'),
(9, 3, 2, 'Titre', 'Le titre de l''élément', '2016-03-26 08:49:43'),
(10, 4, 2, 'Auteur', 'L''auteur de l''élément', '2016-03-26 08:49:43'),
(11, 3, 3, 'Titulo', 'El titulo del elemento', '2016-03-26 08:49:43'),
(12, 4, 3, 'Autor', 'El autor del elemento', '2016-03-26 08:49:43');

-- --------------------------------------------------------

--
-- Structure de la table `community_usergroups`
--

CREATE TABLE `community_usergroups` (
`community_usergroup_id` int(11) NOT NULL,
  `community_group_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

--
-- Contenu de la table `community_usergroups`
--

INSERT INTO `community_usergroups` (`community_usergroup_id`, `community_group_id`, `user_id`, `lastUpdate`) VALUES
(2, 4, 19, '2016-03-26 08:49:43');

-- --------------------------------------------------------

--
-- Structure de la table `community_vocabulary`
--

CREATE TABLE `community_vocabulary` (
`community_vocabulary_id` int(11) NOT NULL,
  `community_id` int(11) NOT NULL,
  `guilanguage_id` int(11) NOT NULL,
  `communityName` varchar(45) NOT NULL DEFAULT "",
  `communityComment` varchar(255) NOT NULL DEFAULT "",
  `communityVocabularyDescription` varchar(255) NOT NULL DEFAULT "",  
  `elementTraduction` varchar(45) NOT NULL DEFAULT "",
  `elementsTraduction` varchar(45) NOT NULL DEFAULT "",
  `datasetTraduction` varchar(45) NOT NULL DEFAULT "",
  `datasetsTraduction` varchar(45) NOT NULL DEFAULT "",
  `metadataTraduction` varchar(45) NOT NULL DEFAULT "",
  `metadatasTraduction` varchar(45) NOT NULL DEFAULT "",
  `catalogTraduction` varchar(45) NOT NULL DEFAULT "",
  `catalogsTraduction` varchar(45) NOT NULL DEFAULT "",
  `userTraduction` varchar(45) NOT NULL DEFAULT "",
  `usersTraduction` varchar(45) NOT NULL DEFAULT "",
  `userGroupTraduction` varchar(45) NOT NULL DEFAULT "",
  `userGroupsTraduction` varchar(45) NOT NULL DEFAULT "",
  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=7 ;

--
-- Contenu de la table `community_vocabulary`
--

INSERT INTO `community_vocabulary` (`community_vocabulary_id`, `community_id`, `guilanguage_id`, `communityName`, `communityComment`, `communityVocabularyDescription`, `elementTraduction`, `elementsTraduction`, `datasetTraduction`, `datasetsTraduction`, `metadataTraduction`, `metadatasTraduction`, `catalogTraduction`, `catalogsTraduction`, `userTraduction`, `usersTraduction`, `userGroupTraduction`, `userGroupsTraduction`, `lastUpdate`) VALUES
(4, 1003, 1, 'Community Name', 'some comments', 'This community lets its %users% to organize and consult %elements%. Each %element% is made of several %metadatas%, and %elements% can be grouped within %catalogs%.', 'study element', 'study elements', 'dataset', 'datasets', 'metadata', 'metadatas', 'catalog', 'catalogs', 'member', 'members', 'group', 'groups', '2016-03-26 08:49:43'),
(5, 1003, 2, 'Votre Communauté', 'une petite description', 'Cette communauté permet à ses %users% d organiser et consulter des %elements%. Chaque %element% est fait de %metadatas%, et ces %elements% peuvent être groupés par %catalogs%.','objet', 'objets', 'dataset', 'datasets', 'métadonnée', 'métadonnées', 'catalogue', 'catalogues', 'membre', 'membres', 'groupe', 'groupes', '2016-03-26 08:49:43'),
(6, 1003, 3, 'Nombre de la Comunidad', 'una explicacion','Este comunidad piermete a su %users% de organisar y consultar %elements%. Cada %element% tiene %metadatas%, y estos %elements% pse pueden regroupar en %catalogs%.', 'objecto', 'objectos', 'dataset', 'datasets', 'metadata', 'metadatas', 'catalog', 'catalogs', 'user', 'users', 'groupo', 'groupos', '2016-03-26 08:49:43');

-- --------------------------------------------------------

--
-- Structure de la table `datatypes`
--

CREATE TABLE `datatypes` (
`datatype_id` int(11) NOT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `comment` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL DEFAULT "",
  `lastUpdated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=9 ;

--
-- Contenu de la table `datatypes`
--

INSERT INTO `datatypes` (`datatype_id`, `name`, `comment`, `lastUpdated`) VALUES
(1, 'Web Page', 'HTTP link toward a web page', '2016-03-08 07:58:20'),
(2, 'Audio', 'HTTP link toward an audio', '2016-03-09 07:29:49'),
(3, 'Video', 'Http link toward a video', '2016-03-09 07:30:12'),
(4, 'Image', 'Http link toward an image', '2016-03-09 07:30:12'),
(5, 'LongText', 'Plain text for litterature', '2016-03-09 07:30:40'),
(6, 'Number', 'Float or Integer number', '2016-03-09 07:31:07'),
(7, 'Date', 'A date in the past, present or future', '2016-03-09 07:31:45'),
(8, 'Period', 'A period of time occuring between two dates', '2016-03-09 07:32:15'),
(9, 'Keywords', 'A coma-separated (",") list of keywords', '2016-03-09 07:32:50'),
(10, 'TinyText', 'Small text (one line)', '2016-03-09 07:30:40');

-- --------------------------------------------------------

--
-- Structure de la table `guilanguages`
--

CREATE TABLE `guilanguages` (
`guilanguage_id` int(11) NOT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `shortname` varchar(8) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,  
  `lastUpdated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=4 ;

--
-- Contenu de la table `guilanguages`
--

INSERT INTO `guilanguages` (`guilanguage_id`, `name`, `shortname`, `lastUpdated`) VALUES
(1, 'English', 'EN', '2016-02-21 21:15:19'),
(2, 'Français', 'FR', '0000-00-00 00:00:00'),
(3, 'Español', 'SP', '0000-00-00 00:00:00');

-- --------------------------------------------------------

--
-- Structure de la table `guithemes`
--

CREATE TABLE `guithemes` (
`guitheme_id` int(11) NOT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `shortname` varchar(32) NOT NULL,
  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

--
-- Contenu de la table `guithemes`
--

INSERT INTO `guithemes` (`guitheme_id`, `name`, `shortname`, `lastUpdate`) VALUES
(1, 'Silver', 'silver', '0000-00-00 00:00:00'),
(2, 'Green Earth', 'greenearth', '0000-00-00 00:00:00');

-- --------------------------------------------------------

--
-- Structure de la table `users`
--

CREATE TABLE `users` (
`user_id` int(11) NOT NULL,
  `email` varchar(45) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `username` varchar(45) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `password` varchar(256) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `enabled` tinyint(4) NOT NULL DEFAULT '1',
  `guilanguage_id` int(11) NOT NULL DEFAULT '1',
  `guitheme_id` int(11) NOT NULL DEFAULT '1',
  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=26 ;

--
-- Contenu de la table `users`
--

INSERT INTO `users` (`user_id`, `email`, `username`, `password`, `enabled`, `guilanguage_id`, `guitheme_id`, `lastUpdate`) VALUES
(16, 'lolo@email.fr', 'lolo', '$2a$10$1x7lT41qHzXyD4MubAoNAOOSOhCU1bv0vqSszHbAkXPEqBQbAtzfa', 1, 1, 1, '2016-03-04 21:46:49'),
(18, 'lolo_ml@yahoo.fr', 'root', '$2a$10$S1EzBLqPv93zWfUk3YXGLexguben6oe5bdRNDrnBjhj1BVlc8eoKC', 1, 1, 1, '0000-00-00 00:00:00'),
(19, 'testuser@metaindex.org', 'testuser', '$2a$10$KdL.HkenDm24Zruv5VM3T.U/r.ES9YyzDTccZwhF2b.fXurLybdsq', 1, 2, 2, '2016-03-16 20:18:57'),
(20, 'testuser2@metaindex.org', 'testuser2', '$2a$10$KdL.HkenDm24Zruv5VM3T.U/r.ES9YyzDTccZwhF2b.fXurLybdsq', 1, 2, 2, '2016-03-16 20:18:57');

-- --------------------------------------------------------

--
-- Structure de la table `user_roles`
--

CREATE TABLE `user_roles` (
`user_role_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `role` varchar(45) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=16 ;

--
-- Contenu de la table `user_roles`
--

INSERT INTO `user_roles` (`user_role_id`, `user_id`, `role`, `lastUpdate`) VALUES
(7, 16, 'ROLE_USER', '0000-00-00 00:00:00'),
(9, 18, 'ROLE_ADMIN', '0000-00-00 00:00:00'),
(11, 18, 'ROLE_USER', '0000-00-00 00:00:00'),
(12, 19, 'ROLE_USER', '2016-03-11 17:46:16'),
(13, 20, 'ROLE_USER', '2016-03-11 17:46:16');
--
-- Index pour les tables exportées
--

--
-- Index pour la table `communities`
--
ALTER TABLE `communities`
 ADD PRIMARY KEY (`community_id`), ADD UNIQUE KEY `name` (`idName`), ADD KEY `FK_COMMUNITIES_CREATOR_ID` (`creator_id`);

--
-- Index pour la table `community_access_elements`
--
ALTER TABLE `community_access_elements`
 ADD PRIMARY KEY (`community_access_element_id`), ADD UNIQUE KEY `U_COMMUNITYACCESSELEMENTS_ELEMENTGROUP` (`community_element_id`, `community_group_id`),ADD KEY `community_element_id_idx` (`community_element_id`),ADD KEY `community_group_id_idx` (`community_group_id`);

--
-- Index pour la table `community_catalogs`
--
ALTER TABLE `community_catalogs`
 ADD PRIMARY KEY (`community_catalog_id`), ADD KEY `FK_COMMUNITYCATALOGS_COMMUNITY_ID` (`community_id`);

--
-- Index pour la table `community_datasets`
--
ALTER TABLE `community_datasets`
 ADD PRIMARY KEY (`community_dataset_id`), ADD KEY `FK_COMMUNITYDATASETS_ELEMENT_ID` (`community_element_id`);

--
-- Index pour la table `community_elements`
--
ALTER TABLE `community_elements`
 ADD PRIMARY KEY (`community_element_id`), ADD KEY `FK_COMMUNITYELEMENTS_COMMUNITY_ID` (`community_id`);

 
--
-- Index pour la table `community_templates`
--
ALTER TABLE `community_templates`
 ADD PRIMARY KEY (`community_element_id`);

 
--
-- Index pour la table `community_groups`
--
ALTER TABLE `community_groups`
 ADD PRIMARY KEY (`community_group_id`), ADD KEY `FK_COMMUNITYGROUPS_COMMUNITY_ID` (`community_id`);

--
-- Index pour la table `community_metadata`
--
ALTER TABLE `community_metadata`
 ADD PRIMARY KEY (`community_metadata_id`), ADD KEY `FK_COMMUNITYMETADATA_DATASET_ID` (`community_dataset_id`), ADD KEY `FK_COMMUNITYMETADATA_TERM_ID` (`community_term_id`);

--
-- Index pour la table `community_static_catalogs_elements`
--
ALTER TABLE `community_static_catalogs_elements`
 ADD PRIMARY KEY (`community_static_catalogs_element_id`), ADD KEY `FK_COMMUNITYSTATICCATALOGSELEMENTS_CATALOG_ID` (`community_catalog_id`), ADD KEY `FK_COMMUNITYSTATICCATALOGSELEMENTS_ELEMENT_ID` (`community_element_id`);

--
-- Index pour la table `community_terms`
--
ALTER TABLE `community_terms`
 ADD PRIMARY KEY (`community_term_id`), ADD UNIQUE KEY `FK_COMMUNITYTERMS_COMMUNITY_ID` (`community_id`,`idName`);

--
-- Index pour la table `community_terms_vocabulary`
--
ALTER TABLE `community_terms_vocabulary`
 ADD PRIMARY KEY (`community_terms_vocabulary_id`), ADD UNIQUE KEY `U_COMMUNITYTERMSVOCABULARY_LANGUAGETERM` (`community_term_id`,`guilanguage_id`),ADD KEY `community_term_id_idx` (`community_term_id`),ADD KEY `guilanguage_id_idx` (`guilanguage_id`);

--
-- Index pour la table `community_usergroups`
--
ALTER TABLE `community_usergroups`
 ADD PRIMARY KEY (`community_usergroup_id`), ADD UNIQUE KEY `U_COMMUNITYUSERGROUPS_USERGROUP` (`community_group_id`,`user_id`), ADD KEY `user_id_idx` (`user_id`),ADD KEY `community_group_id_idx` (`community_group_id`);

--
-- Index pour la table `community_vocabulary`
--
ALTER TABLE `community_vocabulary`
 ADD PRIMARY KEY (`community_vocabulary_id`), ADD UNIQUE KEY `U_COMMUNITYVOCABULARY_COMMUNITYLANGUAGE` (`community_id`,`guilanguage_id`), ADD KEY `FK_COMMUNITYVOCABULARY_COMMUNITY_ID` (`community_id`), ADD KEY `FK_COMMUNITYVOCABULARY_GUILANGUAGE_ID` (`guilanguage_id`);

--
-- Index pour la table `datatypes`
--
ALTER TABLE `datatypes`
 ADD PRIMARY KEY (`datatype_id`);

--
-- Index pour la table `guilanguages`
--
ALTER TABLE `guilanguages`
 ADD PRIMARY KEY (`guilanguage_id`);

--
-- Index pour la table `guithemes`
--
ALTER TABLE `guithemes`
 ADD PRIMARY KEY (`guitheme_id`);

--
-- Index pour la table `users`
--
ALTER TABLE `users`
 ADD PRIMARY KEY (`user_id`), ADD UNIQUE KEY `email` (`email`), ADD UNIQUE KEY `username` (`username`), ADD KEY `FK_USERS_GUILANGUAGE_ID` (`guilanguage_id`), ADD KEY `FK_USERS_GUITHEME_ID` (`guitheme_id`);

--
-- Index pour la table `user_roles`
--
ALTER TABLE `user_roles`
 ADD PRIMARY KEY (`user_role_id`), ADD UNIQUE KEY `uni_userid_role` (`role`,`user_id`), ADD KEY `fk_userid_idx` (`user_id`);

--
-- AUTO_INCREMENT pour les tables exportées
--

--
-- AUTO_INCREMENT pour la table `communities`
--
ALTER TABLE `communities`
MODIFY `community_id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=1004;
--
-- AUTO_INCREMENT pour la table `community_access_elements`
--
ALTER TABLE `community_access_elements`
MODIFY `community_access_element_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT pour la table `community_catalogs`
--
ALTER TABLE `community_catalogs`
MODIFY `community_catalog_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT pour la table `community_datasets`
--
ALTER TABLE `community_datasets`
MODIFY `community_dataset_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT pour la table `community_elements`
--
ALTER TABLE `community_elements`
MODIFY `community_element_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- NO AUTO_INCREMENT pour la table `community_templates` : key is a ref to community_element_id
--

--
-- AUTO_INCREMENT pour la table `community_groups`
--
ALTER TABLE `community_groups`
MODIFY `community_group_id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=7;
--
-- AUTO_INCREMENT pour la table `community_metadata`
--
ALTER TABLE `community_metadata`
MODIFY `community_metadata_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `community_static_catalogs_elements`
--
ALTER TABLE `community_static_catalogs_elements`
MODIFY `community_static_catalogs_element_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT pour la table `community_terms`
--
ALTER TABLE `community_terms`
MODIFY `community_term_id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=5;
--
-- AUTO_INCREMENT pour la table `community_terms_vocabulary`
--
ALTER TABLE `community_terms_vocabulary`
MODIFY `community_terms_vocabulary_id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=13;
--
-- AUTO_INCREMENT pour la table `community_usergroups`
--
ALTER TABLE `community_usergroups`
MODIFY `community_usergroup_id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT pour la table `community_vocabulary`
--
ALTER TABLE `community_vocabulary`
MODIFY `community_vocabulary_id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=7;
--
-- AUTO_INCREMENT pour la table `datatypes`
--
ALTER TABLE `datatypes`
MODIFY `datatype_id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=9;
--
-- AUTO_INCREMENT pour la table `guilanguages`
--
ALTER TABLE `guilanguages`
MODIFY `guilanguage_id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT pour la table `guithemes`
--
ALTER TABLE `guithemes`
MODIFY `guitheme_id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT pour la table `users`
--
ALTER TABLE `users`
MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=26;
--
-- AUTO_INCREMENT pour la table `user_roles`
--
ALTER TABLE `user_roles`
MODIFY `user_role_id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=16;
--
-- Contraintes pour les tables exportées
--

--
-- Contraintes pour la table `communities`
--
ALTER TABLE `communities`
ADD CONSTRAINT `C_FK_COMMUNITIES_CREATOR_ID` FOREIGN KEY (`creator_id`) REFERENCES `users` (`user_id`);

--
-- Contraintes pour la table `community_access_elements`
--
ALTER TABLE `community_access_elements`
ADD CONSTRAINT `C_FK_COMMUNITYACCESSELEMENTS_GROUP_ID` FOREIGN KEY (`community_group_id`) REFERENCES `community_groups` (`community_group_id`),
ADD CONSTRAINT `C_FK_COMMUNITYACCESSELEMENTS_ELEMENT_ID` FOREIGN KEY (`community_element_id`) REFERENCES `community_elements` (`community_element_id`) ON DELETE CASCADE ;

--
-- Contraintes pour la table `community_catalogs`
--
ALTER TABLE `community_catalogs`
ADD CONSTRAINT `C_FK_COMMUNITYCATALOGS_COMMUNITY_ID` FOREIGN KEY (`community_id`) REFERENCES `communities` (`community_id`)  ON DELETE CASCADE ;

--
-- Contraintes pour la table `community_datasets`
--
ALTER TABLE `community_datasets`
ADD CONSTRAINT `C_FK_COMMUNITYDATASETS_ELEMENT_ID` FOREIGN KEY (`community_element_id`) REFERENCES `community_elements` (`community_element_id`) ON DELETE CASCADE ;

--
-- Contraintes pour la table `community_elements`
--
ALTER TABLE `community_elements`
ADD CONSTRAINT `C_FK_COMMUNITYELEMENTS_COMMUNITY_ID` FOREIGN KEY (`community_id`) REFERENCES `communities` (`community_id`)  ON DELETE CASCADE ,
ADD CONSTRAINT `C_FK_COMMUNITYELEMENTS_TEMPLATEREFELEMENT_ID` FOREIGN KEY (`templateRefElementId`) REFERENCES `community_templates` (`community_element_id`);

--
-- Contraintes pour la table `community_templates`
ALTER TABLE `community_templates`
ADD CONSTRAINT `C_FK_COMMUNITYTEMPLATES_ELEMENT_ID` FOREIGN KEY (`community_element_id`) REFERENCES `community_elements` (`community_element_id`)  ON DELETE CASCADE ;


--
-- Contraintes pour la table `community_groups`
--
ALTER TABLE `community_groups`
ADD CONSTRAINT `C_FK_COMMUNITYGROUPS_COMMUNITY_ID` FOREIGN KEY (`community_id`) REFERENCES `communities` (`community_id`)  ON DELETE CASCADE ;

--
-- Contraintes pour la table `community_metadata`
--
ALTER TABLE `community_metadata`
ADD CONSTRAINT `C_FK_COMMUNITYMETADATA_DATASET_ID` FOREIGN KEY (`community_dataset_id`) REFERENCES `community_datasets` (`community_dataset_id`) ON DELETE CASCADE ,
ADD CONSTRAINT `C_FK_COMMUNITYMETADATA_TERM_ID` FOREIGN KEY (`community_term_id`) REFERENCES `community_terms` (`community_term_id`);

--
-- Contraintes pour la table `community_terms`
--
ALTER TABLE `community_terms`
ADD CONSTRAINT `C_FK_COMMUNITYTERMS_DATATYPE_ID` FOREIGN KEY (`datatype_id`) REFERENCES `datatypes` (`datatype_id`),
ADD CONSTRAINT `C_FK_COMMUNITYTERMS_COMMUNITY_ID` FOREIGN KEY (`community_id`) REFERENCES `communities` (`community_id`) ON DELETE CASCADE ;

--
-- Contraintes pour la table `community_terms_vocabulary`
--
ALTER TABLE `community_terms_vocabulary`
ADD CONSTRAINT `C_FK_COMMUNITYTERMSVOCABULARY_TERM_ID` FOREIGN KEY (`community_term_id`) REFERENCES `community_terms` (`community_term_id`) ON DELETE CASCADE ,
ADD CONSTRAINT `C_FK_COMMUNITYTERMSVOCABULARY_GUILANGUAGE_ID` FOREIGN KEY (`guilanguage_id`) REFERENCES `guilanguages` (`guilanguage_id`);

--
-- Contraintes pour la table `community_usergroups`
--
ALTER TABLE `community_usergroups`
ADD CONSTRAINT `C_FK_COMMUNITYUSERGROUPS_COMMUNITY_GROUP_ID` FOREIGN KEY (`community_group_id`) REFERENCES `community_groups` (`community_group_id`),
ADD CONSTRAINT `C_FK_COMMUNITYUSERGROUPS_USER_ID` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ;

--
-- Contraintes pour la table `community_vocabulary`
--
ALTER TABLE `community_vocabulary`
ADD CONSTRAINT `C_FK_COMMUNITYVOCABULARY_GUILANGUAGE_ID` FOREIGN KEY (`guilanguage_id`) REFERENCES `guilanguages` (`guilanguage_id`),
ADD CONSTRAINT `C_FK_COMMUNITYVOCABULARY_COMMUNITY_ID` FOREIGN KEY (`community_id`) REFERENCES `communities` (`community_id`) ON DELETE CASCADE ;

--
-- Contraintes pour la table `users`
--
ALTER TABLE `users`
ADD CONSTRAINT `C_FK_USERS_GUILANGUAGE_ID` FOREIGN KEY (`guilanguage_id`) REFERENCES `guilanguages` (`guilanguage_id`),
ADD CONSTRAINT `C_FK_USERS_GUITHEME_ID` FOREIGN KEY (`guitheme_id`) REFERENCES `guithemes` (`guitheme_id`);

--
-- Contraintes pour la table `user_roles`
--
ALTER TABLE `user_roles`
ADD CONSTRAINT `c_fk_userid` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ;
