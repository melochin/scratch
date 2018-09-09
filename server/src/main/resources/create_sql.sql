-- MySQL dump 10.13  Distrib 5.7.22, for Linux (x86_64)
--
-- Host: 127.0.0.1    Database: words
-- ------------------------------------------------------
-- Server version	5.7.22-0ubuntu0.17.10.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `anime`
--

DROP TABLE IF EXISTS `anime`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `anime` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 NOT NULL,
  `alias` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `pic` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `publish_month` date DEFAULT NULL,
  `finished` int(1) DEFAULT '0',
  `episode_no` int(11) DEFAULT NULL,
  `type` varchar(20) DEFAULT NULL,
  `update_time` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UQ_ANIME_NAME` (`name`),
  UNIQUE KEY `UQ_ANIME_ALIAS` (`alias`)
) ENGINE=InnoDB AUTO_INCREMENT=196 DEFAULT CHARSET=gb2312;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `anime_alias`
--

DROP TABLE IF EXISTS `anime_alias`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `anime_alias` (
  `anime_id` int(11) NOT NULL,
  `host_id` int(11) NOT NULL,
  `alias` varchar(255) NOT NULL,
  PRIMARY KEY (`anime_id`,`host_id`,`alias`),
  KEY `anime_id_index` (`anime_id`),
  KEY `host_id_index` (`host_id`)
) ENGINE=InnoDB DEFAULT CHARSET=gb2312;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `anime_episode`
--

DROP TABLE IF EXISTS `anime_episode`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `anime_episode` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `host_id` int(11) NOT NULL,
  `anime_id` bigint(20) NOT NULL,
  `no` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `scratch_time` datetime DEFAULT NULL,
  `save_time` datetime DEFAULT NULL,
  `push_time` datetime DEFAULT NULL,
  `sequence` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UQ_EPISODE_ANIME_NO_HOST` (`anime_id`,`no`,`host_id`),
  CONSTRAINT `FK_ANIME_ID` FOREIGN KEY (`anime_id`) REFERENCES `anime` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=246 DEFAULT CHARSET=gb2312;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary table structure for view `anime_episode_all`
--

DROP TABLE IF EXISTS `anime_episode_all`;
/*!50001 DROP VIEW IF EXISTS `anime_episode_all`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `anime_episode_all` AS SELECT 
 1 AS `anime_id`,
 1 AS `no`,
 1 AS `url`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `anime_episode_scratch`
--

DROP TABLE IF EXISTS `anime_episode_scratch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `anime_episode_scratch` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `host_id` int(11) NOT NULL,
  `anime_id` bigint(20) NOT NULL,
  `no` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `url` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `scratch_time` datetime DEFAULT NULL,
  `status` int(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_anime_episode_scratch_host_id_anime_id_url` (`host_id`,`anime_id`,`url`)
) ENGINE=InnoDB AUTO_INCREMENT=619 DEFAULT CHARSET=gb2312;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `anime_focus`
--

DROP TABLE IF EXISTS `anime_focus`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `anime_focus` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `anime_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `last_push_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_FOCUS_ANIME_USER` (`anime_id`,`user_id`),
  KEY `FK_FOCUS_ANIME_ID` (`anime_id`) USING BTREE,
  KEY `FK_FOCUS_USER_ID` (`user_id`),
  CONSTRAINT `FK_FOCUS_ANIME_ID` FOREIGN KEY (`anime_id`) REFERENCES `anime` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_FOCUS_USER_ID` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=gb2312;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bili_scratch_record`
--

DROP TABLE IF EXISTS `bili_scratch_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bili_scratch_record` (
  `rid` bigint(20) NOT NULL AUTO_INCREMENT,
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `error` varchar(255) DEFAULT NULL,
  `scratch_count` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`rid`)
) ENGINE=InnoDB AUTO_INCREMENT=93 DEFAULT CHARSET=gb2312;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bili_video`
--

DROP TABLE IF EXISTS `bili_video`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bili_video` (
  `avid` bigint(20) NOT NULL,
  `tid` int(10) DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `url` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `pic_url` varchar(255) DEFAULT NULL,
  `uploader` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `uploader_id` bigint(20) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  `play` bigint(20) DEFAULT NULL,
  `duration` int(11) DEFAULT NULL,
  `description` varchar(1000) CHARACTER SET utf8 DEFAULT NULL,
  PRIMARY KEY (`avid`),
  KEY `IDX_play` (`play`) USING HASH,
  KEY `IDX_TYPE` (`tid`) USING HASH,
  KEY `IDX_DATE` (`create_date`) USING BTREE,
  KEY `IDX_TITLE` (`title`) USING BTREE,
  CONSTRAINT `FK_TYPE_ID` FOREIGN KEY (`tid`) REFERENCES `bili_video_type` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=gb2312;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bili_video_type`
--

DROP TABLE IF EXISTS `bili_video_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bili_video_type` (
  `code` int(10) NOT NULL,
  `name` varchar(30) NOT NULL,
  `parent_code` int(10) DEFAULT NULL,
  `level` int(1) DEFAULT NULL,
  `video_count` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`code`),
  KEY `PK_PARENT_CODE` (`parent_code`) USING HASH,
  KEY `IDX_CODE` (`code`) USING BTREE,
  CONSTRAINT `PK_PARENT_CODE` FOREIGN KEY (`parent_code`) REFERENCES `bili_video_type` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=gb2312;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dictionary`
--

DROP TABLE IF EXISTS `dictionary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dictionary` (
  `code` varchar(4) NOT NULL,
  `parent_code` varchar(4) NOT NULL DEFAULT '-1',
  `value` varchar(255) NOT NULL,
  `sequence` int(4) DEFAULT NULL,
  `used` varchar(1) DEFAULT '1' COMMENT '1:使用 0:暂停',
  PRIMARY KEY (`code`,`parent_code`),
  UNIQUE KEY `UN_DICTIONARY_CODE_PARENT_CODE` (`code`,`parent_code`) USING BTREE,
  UNIQUE KEY `UN_DICTIONARY_PARENT_ORDER` (`code`,`parent_code`,`sequence`) USING BTREE,
  KEY `index_DICTIONARY_PARENT_CODE` (`parent_code`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=gb2312;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `manga_manga`
--

DROP TABLE IF EXISTS `manga_manga`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `manga_manga` (
  `mangaId` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `previewUrl` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `status` int(11) DEFAULT NULL COMMENT '0-未完成 1-完成',
  PRIMARY KEY (`mangaId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `manga_page`
--

DROP TABLE IF EXISTS `manga_page`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `manga_page` (
  `seqId` bigint(20) NOT NULL AUTO_INCREMENT,
  `mangaId` int(11) NOT NULL,
  `pageId` int(11) NOT NULL,
  `url` varchar(500) DEFAULT NULL,
  `status` int(11) DEFAULT NULL COMMENT '0-未下载 1-下载 9-失败',
  PRIMARY KEY (`seqId`),
  UNIQUE KEY `UK_MANGE_PAGE` (`mangaId`,`pageId`)
) ENGINE=InnoDB AUTO_INCREMENT=1007 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `memory_word`
--

DROP TABLE IF EXISTS `memory_word`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `memory_word` (
  `w_id` int(11) NOT NULL AUTO_INCREMENT,
  `word` varchar(255) DEFAULT NULL,
  `rember_times` int(11) DEFAULT '0',
  `right_times` int(11) DEFAULT '0',
  `wrong_times` int(11) DEFAULT '0',
  `last_date` datetime DEFAULT NULL,
  `register_date` datetime DEFAULT NULL,
  PRIMARY KEY (`w_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scratch_record`
--

DROP TABLE IF EXISTS `scratch_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scratch_record` (
  `rid` bigint(20) NOT NULL AUTO_INCREMENT,
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `count` bigint(20) DEFAULT NULL,
  `error` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`rid`)
) ENGINE=InnoDB AUTO_INCREMENT=93 DEFAULT CHARSET=gb2312;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `search_keyword`
--

DROP TABLE IF EXISTS `search_keyword`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `search_keyword` (
  `searchId` bigint(20) NOT NULL AUTO_INCREMENT,
  `keyword` varchar(255) DEFAULT NULL,
  `tag` varchar(255) DEFAULT NULL,
  `lastSearchTime` datetime DEFAULT NULL,
  `tagId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`searchId`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `search_tag`
--

DROP TABLE IF EXISTS `search_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `search_tag` (
  `tag_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `type_id` bigint(10) DEFAULT NULL,
  PRIMARY KEY (`tag_id`),
  KEY `FK_USER_ID` (`user_id`),
  CONSTRAINT `FK_USER_ID` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=75 DEFAULT CHARSET=gb2312;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `search_type`
--

DROP TABLE IF EXISTS `search_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `search_type` (
  `code` int(6) NOT NULL,
  `name` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=gb2312;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(30) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `status` varchar(1) DEFAULT '0',
  `email` varchar(100) DEFAULT NULL,
  `role` int(11) DEFAULT '0',
  `salt` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `INDEX_USERNAME` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=91 DEFAULT CHARSET=gb2312;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `userconnection`
--

DROP TABLE IF EXISTS `userconnection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userconnection` (
  `userId` varchar(255) NOT NULL,
  `providerId` varchar(255) NOT NULL,
  `providerUserId` varchar(255) NOT NULL DEFAULT '',
  `rank` int(11) NOT NULL,
  `displayName` varchar(255) DEFAULT NULL,
  `profileUrl` varchar(512) DEFAULT NULL,
  `imageUrl` varchar(512) DEFAULT NULL,
  `accessToken` varchar(512) NOT NULL,
  `secret` varchar(512) DEFAULT NULL,
  `refreshToken` varchar(512) DEFAULT NULL,
  `expireTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`userId`,`providerId`,`providerUserId`),
  UNIQUE KEY `UserConnectionRank` (`userId`,`providerId`,`rank`)
) ENGINE=InnoDB DEFAULT CHARSET=gb2312;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Final view structure for view `anime_episode_all`
--

/*!50001 DROP VIEW IF EXISTS `anime_episode_all`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `anime_episode_all` AS select `anime_episode_scratch`.`anime_id` AS `anime_id`,`anime_episode_scratch`.`no` AS `no`,`anime_episode_scratch`.`url` AS `url` from `anime_episode_scratch` union select `anime_episode`.`anime_id` AS `anime_id`,`anime_episode`.`no` AS `no`,`anime_episode`.`url` AS `url` from `anime_episode` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-09-07 14:08:43
