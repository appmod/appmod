-- MySQL dump 10.13  Distrib 5.7.22, for Linux (x86_64)
--
-- Host: localhost    Database: gcm
-- ------------------------------------------------------
-- Server version	5.7.22-0ubuntu0.16.04.1

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
-- Table structure for table `anomalyid`
--

DROP TABLE IF EXISTS `anomalyid`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `anomalyid` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `value` varchar(255) DEFAULT NULL,
  `notification` varchar(255) DEFAULT NULL,
  `malicious` char(5) NOT NULL DEFAULT 'No',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=903 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `anomalyid`
--

LOCK TABLES `anomalyid` WRITE;
/*!40000 ALTER TABLE `anomalyid` DISABLE KEYS */;
INSERT INTO `anomalyid` VALUES (1,'1098','Whatsapp is accessing the contacts of your device.','No'),(2,'2014','Facebook is accessing the location of your device.','No'),(3,'3012','Gmail is modifying the calendar events of your device.','No'),(4,'4011','Instagram is accessing the camera of your device.','No'),(5,'5010','YouTube is accessing the microphone of your device.','No'),(6,'6011','Sudoku is reading your sim card info and sending it to www.abnormal.com.','Yes'),(7,'7009','Candy Crush is reading your contacts and sending it to www.hackme.com.','Yes'),(8,'8012','Whatsapp is making phone calls to numbers not found in your contact list .','Yes'),(9,'9013','Clock is accessing your geolocation and sending it out.','Yes'),(10,'10014','Gmail is sending emails to everyone in your address book.','Yes'),(900,'99900','[msg] Thank you for your participation. Please click on the button below to complete the user survey.','No'),(901,'99901','[msg] You have completed the user survey form. Please do a screenshot and use this code (xxxxx) to get your reward.','No'),(902,'99902','[msg] Thank you for your participation. The user study has been completed. ','No');
/*!40000 ALTER TABLE `anomalyid` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-06-28 14:04:26
