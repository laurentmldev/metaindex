
select '######## CREATING ADMIN USER ########' as '';

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

USE metaindex;


INSERT INTO `users` (`user_id`, `email`, `nickname`, `password`, `enabled`, `guilanguage_id`, `guitheme_id`, `lastUpdate`) VALUES
(1, 'laurentmlcontact-metaindex@yahoo.fr', 'Laurent Admin', '$2a$10$gwVIdLdHwm8wW5z0Yl1Kxunh5e5TIUdPRHD5hJRJa2iCihqV1mkTS', 1, 1, 1, '1981-06-11 18:00:00');




