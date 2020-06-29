


--
-- METAINDEX Database structure migration
--

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>
*/


select '######## MIGRATING METAINDEX SQL DB from v2.0.4 to v2.0.5 ########' as '';

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

CREATE DATABASE IF NOT EXISTS metaindex;

USE metaindex;

ALTER TABLE `catalogs` ADD `timeField_term_id` int(32) NULL AFTER `ftpPort`; 

ALTER TABLE `catalogs`
ADD CONSTRAINT `C_FK_CATALOGS_TIMEFIELD` FOREIGN KEY (`timeField_term_id`) REFERENCES `catalog_terms` (`catalog_term_id`) ON DELETE SET NULL;
