


--
-- METAINDEX Database structure migration
--

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>
*/


select '######## MIGRATING METAINDEX SQL DB from v2.1.3 to v2.2.0 ########' as '';

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

CREATE DATABASE IF NOT EXISTS metaindex;

USE metaindex;

-- rename RICH_TEXT into LONG_TEXT
ALTER TABLE `catalog_terms` CHANGE `datatype` `datatype` 
	ENUM('TINY_TEXT','LONG_TEXT','DATE','INTEGER','FLOAT','PAGE_URL','IMAGE_URL','AUDIO_URL','VIDEO_URL','GEO_POINT','LINK') 
	CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL;


