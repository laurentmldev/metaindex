
select '######## CREATING STANDALONE DB ENTRIES ########' as '';

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

USE metaindex;

/* login:metaindex pwd:metaindex */
INSERT INTO `users` (`user_id`, `email`, `nickname`, `password`, `enabled`, `guilanguage_id`, `guitheme_id`, `lastUpdate`) VALUES
(0, 'mxuser', 'MetaindeX', '$2a$10$ftX8296NtL5yRyXw20lwDOY4VVI.zPvHYq6XXTlK5hU7QnKpFesfS', 1, 1, 1, '1981-06-11 18:00:00');

INSERT INTO `user_roles` (`user_role_id`, `user_id`, `role`, `lastUpdate`) VALUES
(0, 0, 'ROLE_ADMIN', '2016-02-21 21:15:19');


INSERT INTO `plans` ( `plan_id`, `name`, `availableForPurchase`, `category`,
                    `quotaCreatedCatalogs`, `quotaNbDocsPerCatalog`, `quotaDriveMBytesPerCatalog`, `yearlyCostEuros`) VALUES

(0,'Admin-Standalone',false,'PERSONAL',-1,-1,-1,0);

INSERT INTO `user_plans` ( `user_id`, `plan_id`, `startDate`, `endDate` ) VALUES
(0,0,'1970-01-01','3000-12-31');
