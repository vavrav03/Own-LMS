-- MySQL dump 10.13  Distrib 8.0.18, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: arabase
-- ------------------------------------------------------
-- Server version	8.0.18

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `classes`
--

DROP TABLE IF EXISTS `classes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `classes` (
  `class_id` int(11) NOT NULL AUTO_INCREMENT,
  `grade` int(11) NOT NULL,
  `suffix` varchar(8) NOT NULL,
  `teacher_id` int(11) NOT NULL,
  PRIMARY KEY (`class_id`),
  KEY `id_idx` (`teacher_id`),
  CONSTRAINT `teacher` FOREIGN KEY (`teacher_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `classes`
--

LOCK TABLES `classes` WRITE;
/*!40000 ALTER TABLE `classes` DISABLE KEYS */;
INSERT INTO `classes` VALUES (1,2,'E',28),(2,2,'B',28);
/*!40000 ALTER TABLE `classes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `engaged_tests`
--

DROP TABLE IF EXISTS `engaged_tests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `engaged_tests` (
  `test_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `begin_date` timestamp NOT NULL,
  `volunteer` tinyint(4) NOT NULL,
  `deadline` timestamp NULL DEFAULT NULL,
  `submit_time` timestamp NULL DEFAULT NULL,
  `alterable` tinyint(4) NOT NULL DEFAULT '1',
  `term` int(8) NOT NULL,
  PRIMARY KEY (`test_id`,`user_id`),
  KEY `tested_user_idx` (`user_id`),
  KEY `tested_test_idx` (`test_id`),
  CONSTRAINT `tested_test` FOREIGN KEY (`test_id`) REFERENCES `tests` (`test_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `tested_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `engaged_tests`
--

LOCK TABLES `engaged_tests` WRITE;
/*!40000 ALTER TABLE `engaged_tests` DISABLE KEYS */;
INSERT INTO `engaged_tests` VALUES (46,34,'2020-04-25 13:55:28',0,'2020-04-30 22:36:43','2020-04-30 22:26:46',0,2),(46,36,'2020-04-29 21:59:25',0,NULL,NULL,1,2),(49,34,'2020-04-30 19:04:27',0,'2020-04-30 22:51:43',NULL,0,1);
/*!40000 ALTER TABLE `engaged_tests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `forgot_password`
--

DROP TABLE IF EXISTS `forgot_password`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `forgot_password` (
  `user_id` int(11) NOT NULL,
  `token` varbinary(128) NOT NULL,
  `token_expire` datetime NOT NULL,
  PRIMARY KEY (`user_id`),
  CONSTRAINT `user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `forgot_password`
--

LOCK TABLES `forgot_password` WRITE;
/*!40000 ALTER TABLE `forgot_password` DISABLE KEYS */;
/*!40000 ALTER TABLE `forgot_password` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `options_0`
--

DROP TABLE IF EXISTS `options_0`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `options_0` (
  `question_id` int(11) NOT NULL,
  `answer` tinyint(4) NOT NULL,
  KEY `question_id_0_idx` (`question_id`),
  CONSTRAINT `question_id_0` FOREIGN KEY (`question_id`) REFERENCES `questions` (`question_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `options_0`
--

LOCK TABLES `options_0` WRITE;
/*!40000 ALTER TABLE `options_0` DISABLE KEYS */;
INSERT INTO `options_0` VALUES (56,1),(57,0),(58,0),(59,1),(60,1),(61,0),(62,0),(72,1),(73,0),(75,0),(76,1),(77,1),(78,0),(79,0),(80,1),(81,1),(82,0);
/*!40000 ALTER TABLE `options_0` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `options_1`
--

DROP TABLE IF EXISTS `options_1`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `options_1` (
  `question_id` int(11) NOT NULL,
  `subquestion_number` int(8) NOT NULL,
  `option_number` int(8) NOT NULL,
  `text` varchar(60) NOT NULL,
  KEY `question_id_idx` (`question_id`),
  CONSTRAINT `question_id1` FOREIGN KEY (`question_id`) REFERENCES `questions` (`question_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `options_1`
--

LOCK TABLES `options_1` WRITE;
/*!40000 ALTER TABLE `options_1` DISABLE KEYS */;
INSERT INTO `options_1` VALUES (55,0,0,'JVM'),(55,0,1,'JDK'),(55,0,2,'JVS'),(55,1,0,'JDK'),(55,1,1,'JVB'),(55,1,2,'JKD'),(55,2,0,'javac'),(55,2,1,'cjava'),(55,2,2,'djava'),(55,3,0,'Hello World'),(55,3,1,'First Programme'),(55,3,2,'First Words'),(55,4,0,'hlavn??'),(55,4,1,'priv??tn??'),(55,4,2,'vedlej????'),(55,5,0,'kon???? v??stupem'),(55,5,1,'pokra??uje i po v??stupu'),(55,5,2,'kon???? zavol??n??m System.exit(0)'),(55,6,0,'interpret'),(55,6,1,'kompiler'),(55,6,2,'JDK'),(55,7,0,'System'),(55,7,1,'Console'),(55,7,2,'Print'),(55,8,0,'println'),(55,8,1,'writeln'),(55,8,2,'console'),(70,0,0,'ve dvojkov??'),(70,0,1,'v jedni??kov??'),(70,0,2,'v des??tkov??'),(70,1,0,'Hornerovo sch??ma'),(70,1,1,'prvo????seln?? rozklad'),(70,1,2,'p??evod d??len??m'),(70,2,0,'1 ????slici'),(70,2,1,'8 ????slic'),(70,2,2,'10 ????slic'),(70,3,0,'8 t??chto ????slic'),(70,3,1,'1 tuto ????slici'),(70,3,2,'10 t??chto ????slic'),(70,4,0,'4'),(70,4,1,'3'),(70,4,2,'2'),(70,5,0,'deklarovat'),(70,5,1,'inicializovat'),(70,5,2,'zalogovat'),(70,6,0,'inicializujeme'),(70,6,1,'deklarujeme'),(70,6,2,'zalogujeme');
/*!40000 ALTER TABLE `options_1` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `options_2`
--

DROP TABLE IF EXISTS `options_2`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `options_2` (
  `question_id` int(11) NOT NULL,
  `option_number` int(11) NOT NULL,
  `text` varchar(40) NOT NULL,
  `definition` varchar(120) NOT NULL,
  KEY `question_id_2_idx` (`question_id`),
  CONSTRAINT `question_id_2` FOREIGN KEY (`question_id`) REFERENCES `questions` (`question_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `options_2`
--

LOCK TABLES `options_2` WRITE;
/*!40000 ALTER TABLE `options_2` DISABLE KEYS */;
INSERT INTO `options_2` VALUES (54,0,'Java SE','Platforma na v??voj desktopov??ch aplikac??'),(54,1,'Java EE','Platforma pro v??voj webov??ch podnikov??ch apliakc??'),(54,2,'Java ME','Platforma na v??voj aplikac?? pro mal?? za????zen??'),(54,3,'Java FX','Platforma na v??voj grafick??ho u??ivatelsk??ho rozshran?? '),(74,0,'Aritmetick??','+, -, /, *'),(74,1,'Un??rn??','++, --'),(74,2,'P??i??azovac??','=, /=, *='),(74,3,'Bin??rn??','~, <<, >>, >>>, ^'),(74,4,'Logick?? (rela??n??)','||, &&, !');
/*!40000 ALTER TABLE `options_2` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `options_3`
--

DROP TABLE IF EXISTS `options_3`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `options_3` (
  `question_id` int(11) NOT NULL,
  `option_number` int(11) NOT NULL,
  `answer` varchar(120) NOT NULL,
  KEY `question_id_3_idx` (`question_id`),
  CONSTRAINT `question_id_3` FOREIGN KEY (`question_id`) REFERENCES `questions` (`question_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `options_3`
--

LOCK TABLES `options_3` WRITE;
/*!40000 ALTER TABLE `options_3` DISABLE KEYS */;
INSERT INTO `options_3` VALUES (51,1,'Projekt'),(51,2,'Src slo??ka'),(51,3,'Bal????ek'),(51,4,'T????da'),(51,5,'Metoda'),(71,1,'boolean'),(71,2,'byte'),(71,3,'short'),(71,4,'int'),(71,5,'double');
/*!40000 ALTER TABLE `options_3` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `questions`
--

DROP TABLE IF EXISTS `questions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `questions` (
  `question_id` int(11) NOT NULL AUTO_INCREMENT,
  `question_text` varchar(1500) NOT NULL,
  `right_option_points` int(8) NOT NULL,
  `wrong_option_points` int(8) NOT NULL,
  `type` int(4) NOT NULL,
  PRIMARY KEY (`question_id`)
) ENGINE=InnoDB AUTO_INCREMENT=83 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `questions`
--

LOCK TABLES `questions` WRITE;
/*!40000 ALTER TABLE `questions` DISABLE KEYS */;
INSERT INTO `questions` VALUES (51,'Se??a??te hierarchii soubor??, slo??ek a ????st?? k??du\r\nv??Jav?? od nejvy?????? po nejni??????',1,0,3),(54,'P??i??a??te n??zev platformy k jej??mu u??it??',1,1,2),(55,'Abychom mohli ps??t k??dy v??Jav??, pot??ebujeme krom?? JRE,\r ve kter?? je obsa??en virtu??ln?? stroj ___,\r st??hnout tak?? ___. Bez n??j bychom toti??\r nem??li p????stup k??d??le??it??m knihovn??m a hlavn?? bychom nem??li p????stup ke\r kompil??toru, kter?? se v??Jav?? jmenuje ___. Prvn?? program, kter?? v??t??ina program??tor?? nap????e jako prvn??\r v????ivot?? se jmenuje ___. M?? za ??kol do konzole vypsat pozdrav sv??tu. Obsahuje jen\r jednu metodu, kter?? je ___,\r tedy nese n??zev main. Ka??d?? jedno-vl??knov?? program napsan?? v??Jav?? za????n??\r vstupem do t??to metody a ___??z??t??to metody. Plat??, ??e\r ka??d?? spustiteln?? aplikace m?? tuto metodu pr??v?? jednu Kdyby jich toti?? bylo v??ce,\r nev??d??l by ___, do kter??\r vstoupit prvn?? kde m?? za????t. K??vypisov??n?? do konzole slou???? p????kaz ___.out.___.',1,1,1),(56,'Soubory\r\nv??bytek??du v??Jav?? maj?? p????ponu .class??',1,1,0),(57,'Soubor\r\n.class m????e obsahovat dv?? t????dy??',1,1,0),(58,'ClassLoader\r\nm?? za ??kol spr??vu pam??ti b??hem b??hu aplikace v JVM??',1,1,0),(59,'Execution\r\nengine interpretuje bytek??d do strojov??ho k??du??',1,1,0),(60,'JIT kompiler\r\nkompiluje ??asto kop??rovan?? (spou??t??n??) k??d.',1,1,0),(61,'Garbage\r\nCollector se vyskytuje jak v??Jav??, tak i v??C++??',1,1,0),(62,'Soubor\r .java m????e obsahovat dv?? ve??ejn?? t????dy (zano??en?? nepo????t??me) ??',1,1,0),(70,'Prom??nn?? jsou na po????ta??i reprezentov??ny pomoc?? jedni??ek a\r\nnul. Jsou tedy ulo??eny ??___??soustav??. Algoritmus na efektivn?? p??evod z??des??tkov?? do dvojkov?? a naopak\r\nse naz??v?? ___. Jeden bit reprezentuje ___??ve dvojkov?? soustav??. Naproti tomu jeden bajt reprezentuje ___.??V??Jav?? se nach??z?? p??esn?? ___??primitivn??\r\ndatov?? typy, kter?? jsou ur??eny pro celo????seln?? reprezentace ????sel (nepo????t??me\r\nchar, ten je ur??en na n??co jin??ho). Prom??nnou m????eme ___, ur????me j?? tak m??sto v??pam??ti. Pot??\r\nprom??nnou ___,\r\nt??m j?? p??i??ad??me n??jakou hodnotu.',1,1,1),(71,'Se??a??te tyto datov?? typy vzestupn?? podle teoretick??ho po??tu bit??\r pot??ebn??ch na jejich reprezentaci:',1,0,3),(72,'150\r\nz??des??tkov?? do dvojkov?? je ve dvojkov?? soustav????10010110.',1,1,0),(73,'10001001 z dvojkov?? soustavy je v des??tkov?? soustav?? 135.',1,1,0),(74,'P??i??a??te oper??tory do spr??vn?? skupiny',1,1,2),(75,'Oper??tor\r\ndisjunkce m?? p??ednost p??ed oper??torem konjunkce.',1,1,0),(76,'Oper??tor\r\n>>> na nejv??znamn??j????m bitu (nejv??ce vlevo) v??dy vlo???? nulu.??',1,1,0),(77,'Prom??nn??\r\ntypu float m?? 23 bit?? na mantisu.',1,1,0),(78,'Prom??nn??\r\ntypu byte m?? rozsah od -127 do 128.',1,1,0),(79,'V??Jav??\r\nje p??i d??len?? nulou s??typem double nebo float vyhozena v??jimka.',1,1,0),(80,'Pokud\r\nmetoda nevrac?? ????dnou hodnotu, je typu void.',1,1,0),(81,'Ve\r\nt????d?? Math se vyskytuj?? pr??v?? 2 konstanty.',1,1,0),(82,'Pokud\r\nchceme reprezentovat velk?? cel?? ????sla a z??rove?? chceme zachovat maxim??ln??\r\np??esnost na ??kor rychlosti, pou??ijeme double??',1,1,0);
/*!40000 ALTER TABLE `questions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `remembered_users`
--

DROP TABLE IF EXISTS `remembered_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `remembered_users` (
  `token_id` int(11) NOT NULL AUTO_INCREMENT,
  `selector` varchar(64) NOT NULL,
  `hash` varbinary(128) NOT NULL,
  `user_id` int(11) NOT NULL,
  `token_expire` datetime NOT NULL,
  PRIMARY KEY (`token_id`),
  KEY `userIID_idx` (`user_id`),
  CONSTRAINT `userIID` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `remembered_users`
--

LOCK TABLES `remembered_users` WRITE;
/*!40000 ALTER TABLE `remembered_users` DISABLE KEYS */;
INSERT INTO `remembered_users` VALUES (31,'PbIqsW5J85vc6Ceu/j+eMMffGsH3t2YXGviVe3gB1go=',_binary '?8V?8q\???Fy|?\??JU>e',28,'2020-02-18 17:02:55'),(82,'p1a1Zh2VwwKQMS96vK1VfhJO3JHzUrTF+eoYsZhXxQU=',_binary '\?su\?$p???I?.<iA+?p;	',34,'2020-05-07 12:30:31'),(84,'8VcrD6JYm4n3gOzVpNd3k3PWSQG5WvZLFQtoNzujmx4=',_binary '?j??\Z2:??????\?%??d',34,'2020-05-07 17:05:14');
/*!40000 ALTER TABLE `remembered_users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_answers`
--

DROP TABLE IF EXISTS `student_answers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_answers` (
  `user_id` int(11) NOT NULL,
  `test_id` int(11) NOT NULL,
  `question_id` int(11) NOT NULL,
  `subquestion_number` int(11) NOT NULL,
  `selected_option` int(11) NOT NULL,
  KEY `userIIDD_idx` (`user_id`),
  KEY `testIIDD_idx` (`test_id`),
  KEY `questionIIDD_idx` (`question_id`),
  CONSTRAINT `questionIIDD` FOREIGN KEY (`question_id`) REFERENCES `questions` (`question_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `testIIDD` FOREIGN KEY (`test_id`) REFERENCES `tests` (`test_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `userIIDD` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_answers`
--

LOCK TABLES `student_answers` WRITE;
/*!40000 ALTER TABLE `student_answers` DISABLE KEYS */;
INSERT INTO `student_answers` VALUES (34,46,51,1,1),(34,46,51,2,2),(34,46,51,3,3),(34,46,51,4,4),(34,46,51,5,5),(34,46,54,0,0),(34,46,54,1,1),(34,46,54,2,2),(34,46,54,3,3),(34,46,55,0,0),(34,46,55,1,0),(34,46,55,2,0),(34,46,55,3,0),(34,46,55,4,0),(34,46,55,5,0),(34,46,55,6,0),(34,46,55,7,0),(34,46,55,8,0),(34,46,56,0,1),(34,46,57,0,0),(34,46,58,0,0),(34,46,59,0,1),(34,46,60,0,1),(34,46,61,0,0),(34,46,62,0,0);
/*!40000 ALTER TABLE `student_answers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `students`
--

DROP TABLE IF EXISTS `students`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `students` (
  `user_id` int(11) NOT NULL,
  `class_id` int(11) NOT NULL,
  UNIQUE KEY `user_id_UNIQUE` (`user_id`),
  KEY `student_idx` (`user_id`),
  KEY `class_id_idx` (`class_id`),
  CONSTRAINT `class_id` FOREIGN KEY (`class_id`) REFERENCES `classes` (`class_id`),
  CONSTRAINT `student_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `students`
--

LOCK TABLES `students` WRITE;
/*!40000 ALTER TABLE `students` DISABLE KEYS */;
INSERT INTO `students` VALUES (34,1),(36,2);
/*!40000 ALTER TABLE `students` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tests`
--

DROP TABLE IF EXISTS `tests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tests` (
  `test_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(60) NOT NULL,
  `grade` int(8) NOT NULL,
  `time_to_complete` int(22) NOT NULL,
  `practice` tinyint(4) NOT NULL,
  PRIMARY KEY (`test_id`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tests`
--

LOCK TABLES `tests` WRITE;
/*!40000 ALTER TABLE `tests` DISABLE KEYS */;
INSERT INTO `tests` VALUES (46,'O Jav?? obecn??',1,1800,0),(49,'Prom??nn??',2,10,0);
/*!40000 ALTER TABLE `tests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `used_questions`
--

DROP TABLE IF EXISTS `used_questions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `used_questions` (
  `test_id` int(11) NOT NULL,
  `question_id` int(11) NOT NULL,
  KEY `used_question_idx` (`question_id`),
  KEY `test_idx` (`test_id`),
  CONSTRAINT `questionId` FOREIGN KEY (`question_id`) REFERENCES `questions` (`question_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `test` FOREIGN KEY (`test_id`) REFERENCES `tests` (`test_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `used_questions`
--

LOCK TABLES `used_questions` WRITE;
/*!40000 ALTER TABLE `used_questions` DISABLE KEYS */;
INSERT INTO `used_questions` VALUES (46,51),(46,54),(46,55),(46,56),(46,57),(46,58),(46,59),(46,60),(46,61),(46,62),(49,70),(49,71),(49,72),(49,73),(49,74),(49,75),(49,76),(49,77),(49,78),(49,79),(49,80),(49,81),(49,82);
/*!40000 ALTER TABLE `used_questions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `first_name` varchar(26) NOT NULL,
  `last_name` varchar(26) NOT NULL,
  `email` char(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `iterationCount` int(11) NOT NULL,
  `salt` varbinary(16) NOT NULL,
  `hash` varbinary(16) NOT NULL,
  `category` int(3) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (28,'U??itel','U??itelovi??','ucitel@ucitel.cz',80278,_binary '\?X?{_@\?Q\?\?\\?\?]',_binary '?\?\?jk;\\UZS\Z7???',1),(29,'Admin','Adminoci??','admin@admin.cz',17166,_binary ',??7qu??v6Bh??',_binary 'c?<?9\rI??cc9M?@',0),(34,'Vladim??r','V??vra','vladimir.vavra@student.gyarab.cz',52574,_binary '?d($\r???K???S',_binary '\?T?>??? \???\?C?',2),(36,'Lubos','Vavra','lubos@lubos.cz',58705,_binary '\?_J/aO}W??O\?\?\??',_binary 'I-??\??t#?)????',2);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-05-01 11:00:55
