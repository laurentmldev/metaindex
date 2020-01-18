

select '######## CREATING DEV ENV ########' as '';


SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

USE metaindex;

INSERT INTO `catalogs` (`catalog_id`, `shortname`, `creator_id`, `lastUpdate`) VALUES
(1003, 'test_catalog', 1, '2016-03-26 08:49:43');



INSERT INTO `catalog_terms` (`catalog_term_id`, `catalog_id`, `datatype`, `name`, `enumsList`,`lastUpdate`) VALUES
(3, 1003, 'TINY_TEXT', 'Title', '','2016-03-26 08:49:43'),
(4, 1003, 'TINY_TEXT', 'Author', 'Wes,Jaõe Pass,Billie Holiday Inn]', '2016-03-26 08:49:43'),
(5, 1003, 'IMAGE_URL', 'Illustration', '','2016-03-26 08:49:43');


-- perspective_json_string= {
-- tabs: [
--		{
--			title:"my tab",
--			sections: [
--				   {
--					   title : "my section",
--					   type: "mozaic", (mozaic|table)
-- 					   align : "left", (left|center|right)
--					   fields: [ 
--								nom : {
--									size:"small", (small,medium,big)
--									color:"normal",  (normal, black, red, yellow, green, orange, blue, purple)
-- 									showTitle:"true"
--									weight:"normal", (normal|bold|italic)
--								}
--							]
--				   }
--				]
--      }
--  ]
-- }   
 

-- no default perspective defined, otherwise would need to create associated terms
-- INSERT INTO `catalog_perspectives` (`catalog_perspective_id`, `catalog_id`, `name`, `perspective_json_string`,`lastUpdate`) VALUES
-- (3, 1003, 'My Perspective 1', '{ tabs : [ { title:"Tab One", sections : [ { title :"Section 1", type : "table", align : "center", fields : [ { term : "nom", size : "small" } ] } ] },{ title:"Tab 2", sections : [ { title :"Section A", type : "mozaic", align : "center", fields : [ { term : "nom", size : "small" } ] }, { title :"section B", type : "table", align : "center", fields : [ { term : "prenom", size : "small" },{ term : "ville", size : "small" },{ term : "file", size : "big" } ] } ] } ] }','2016-03-26 08:49:43'),
-- (4, 1003, 'My Perspective 2', '{ tabs : [ { title:"Tab One", sections : [ { title :"Section 1", type : "mozaic", align : "center", fields : [ { term : "nom", size : "small" } ] } ] },{ title:"Tab 2", sections : [ { title :"Section AAA", type : "mozaic", align : "center", fields : [ { term : "nom", size : "small" } ] }, { title :"section BBB", type : "table", align : "center", fields : [ { term : "prenom", size : "small" },{ term : "ville", size : "small" },{ term : "file", size : "big" } ] } ] } ] }','2016-03-26 08:49:43')
-- ;



INSERT INTO `catalog_terms_vocabulary` (`catalog_terms_vocabulary_id`, `catalog_term_id`, `guilanguage_id`, `termTraduction`, `lastUpdate`) VALUES
(7, 3, 1, 'Title', '2016-03-26 08:49:43'),
(8, 4, 1, 'Author', '2016-03-26 08:49:43'),
(9, 3, 2, 'Titre',  '2016-03-26 08:49:43'),
(10, 4, 2, 'Auteur', '2016-03-26 08:49:43'),
(11, 3, 3, 'Titulo', '2016-03-26 08:49:43'),
(12, 4, 3, 'Autor', '2016-03-26 08:49:43');


INSERT INTO `catalog_vocabulary` (`catalog_vocabulary_id`, `catalog_id`, `guilanguage_id`, `catalogName`, `catalogComment`, `itemTraduction`, `itemsTraduction`,`userTraduction`, `usersTraduction`, `lastUpdate`) VALUES
(4, 1003, 1, 'Catalog Name', 'some comments', 'study element', 'study elements','member', 'members', '2016-03-26 08:49:43'),
(5, 1003, 2, 'Votre Catalogue', 'une petite description','objet', 'objets', 'membre', 'membres', '2016-03-26 08:49:43'),
(6, 1003, 3, 'Nombre del Catalogo', 'una explicacion', 'objecto', 'objectos', 'user', 'users', '2016-03-26 08:49:43');


INSERT INTO `filters` (`filter_id`, `catalog_id`, `name`,`query`, `lastUpdate`) VALUES
(1, 1003, 'Lolo', 'name:Lolo', '2016-02-21 21:15:19');


INSERT INTO `users` (`user_id`, `email`, `nickname`, `password`, `enabled`, `guilanguage_id`, `guitheme_id`, `lastUpdate`) VALUES
(2, 'user@yahoo.fr', 'test-user', '$2a$10$gwVIdLdHwm8wW5z0Yl1Kxunh5e5TIUdPRHD5hJRJa2iCihqV1mkTS', 1, 1, 1, '1970-01-01 00:00:00'),
(3, 'observer@yahoo.fr', 'test-observer', '$2a$10$gwVIdLdHwm8wW5z0Yl1Kxunh5e5TIUdPRHD5hJRJa2iCihqV1mkTS', 1, 1, 1, '1970-01-01 00:00:00'),
(4, 'none@yahoo.fr', 'test-none', '$2a$10$gwVIdLdHwm8wW5z0Yl1Kxunh5e5TIUdPRHD5hJRJa2iCihqV1mkTS', 1, 1, 1, '1970-01-01 00:00:00'),
(5, 'catalog_admin@yahoo.fr', 'test-catalog-admin', '$2a$10$gwVIdLdHwm8wW5z0Yl1Kxunh5e5TIUdPRHD5hJRJa2iCihqV1mkTS', 1, 1, 1, '1970-01-01 00:00:00'),
(6, 'catalog_edit@yahoo.fr', 'test-catalog-edit', '$2a$10$gwVIdLdHwm8wW5z0Yl1Kxunh5e5TIUdPRHD5hJRJa2iCihqV1mkTS', 1, 1, 1, '1970-01-01 00:00:00'),
(7, 'catalog_read@yahoo.fr', 'test-catalog-readonly', '$2a$10$gwVIdLdHwm8wW5z0Yl1Kxunh5e5TIUdPRHD5hJRJa2iCihqV1mkTS', 1, 1, 1, '1970-01-01 00:00:00');


INSERT INTO `user_roles` (`user_role_id`, `user_id`, `role`, `lastUpdate`) VALUES
(7, 1, 'ROLE_ADMIN', '1970-01-01 00:00:00'),
(9, 2, 'ROLE_USER', '1970-01-01 00:00:00'),
(12, 3, 'ROLE_OBSERVER', '2016-03-11 17:46:16'),
(15, 4, 'ROLE_OBSERVER', '2016-03-11 17:46:16'),
(16, 5, 'ROLE_USER', '1970-01-01 00:00:00'),
(17, 6, 'ROLE_USER', '1970-01-01 00:00:00'),
(18, 7, 'ROLE_USER', '1970-01-01 00:00:00');


INSERT INTO `user_catalogs_rights` (`user_catalogs_rights_id`, `user_id`, `catalog_id`, `access_rights`, `lastUpdate`) VALUES
(7, 1, 1003, 'CATALOG_ADMIN', '1970-01-01 00:00:00'),
(9, 2, 1003, 'CATALOG_EDIT', '1970-01-01 00:00:00'),
(12, 3, 1003, 'CATALOG_READ', '2016-03-11 17:46:16'),
(13, 5, 1003, 'CATALOG_ADMIN', '2016-03-11 17:46:16'),
(14, 6, 1003, 'CATALOG_EDIT', '2016-03-11 17:46:16'),
(15, 7, 1003, 'CATALOG_READ', '2016-03-11 17:46:16');




INSERT INTO `user_catalogs_customization` (`user_catalogs_customization_id`, `user_id`, `catalog_id`, `kibana_iframe`, `lastUpdate`) VALUES
(7, 1, 1003, '', '1970-01-01 00:00:00');



