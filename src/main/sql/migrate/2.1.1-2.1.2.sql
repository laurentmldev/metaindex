


--
-- METAINDEX Database structure migration
--

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>
*/


select '######## MIGRATING METAINDEX SQL DB from v2.1.1 to v2.1.2 ########' as '';

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

CREATE DATABASE IF NOT EXISTS metaindex;

USE metaindex;

ALTER TABLE `users` ADD `lastLogin` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP; 


