CREATE DATABASE  IF NOT EXISTS `barberapp` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `barberapp`;
-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: barberapp
-- ------------------------------------------------------
-- Server version	8.0.36

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
-- Table structure for table `citas`
--

DROP TABLE IF EXISTS `citas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `citas` (
  `cita_id` int NOT NULL AUTO_INCREMENT,
  `cliente_nombre` varchar(100) NOT NULL,
  `servicio_id` int NOT NULL,
  `fecha` date NOT NULL,
  `hora` time NOT NULL,
  `usuario_id` int NOT NULL,
  PRIMARY KEY (`cita_id`),
  KEY `servicio_id` (`servicio_id`),
  KEY `usuario_id` (`usuario_id`),
  CONSTRAINT `citas_ibfk_1` FOREIGN KEY (`servicio_id`) REFERENCES `servicios` (`servicio_id`),
  CONSTRAINT `citas_ibfk_2` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`usuario_id`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `citas`
--

LOCK TABLES `citas` WRITE;
/*!40000 ALTER TABLE `citas` DISABLE KEYS */;
INSERT INTO `citas` VALUES (1,'Prueba',3,'2025-05-31','20:30:00',1),(2,'Samu',1,'2025-05-30','10:30:00',1),(3,'Prueba',3,'2025-06-02','12:30:00',1),(7,'ssssss',1,'2025-06-04','10:00:00',1),(8,'Sergio',1,'2025-06-05','10:00:00',1),(9,'SSSSSSS',1,'2025-06-04','10:30:00',1),(10,'Jose',1,'2025-06-05','10:30:00',1),(11,'Yoseba',1,'2025-06-04','11:00:00',1),(12,'Pablo',1,'2025-06-04','11:30:00',1),(14,'aaaaa',1,'2025-06-04','12:00:00',2),(15,'AAA',1,'2025-06-04','09:00:00',1),(16,'zzzz',1,'2025-06-04','09:30:00',1),(17,'aaaaaaa',1,'2025-06-05','12:00:00',1),(18,'AAA',1,'2025-06-01','09:00:00',1),(26,'Andres',1,'2025-06-11','09:30:00',1),(27,'Jaime',2,'2025-06-11','11:30:00',2),(31,'Jose',1,'2025-06-16','09:00:00',1),(32,'Alberto',3,'2025-06-16','09:30:00',1),(33,'Angel',1,'2025-06-16','10:00:00',1),(34,'Ruben',1,'2025-06-16','10:30:00',1),(35,'Juan Antonio',2,'2025-06-16','11:00:00',1),(36,'Daniel',3,'2025-06-16','11:30:00',1),(37,'Miguel Angel',4,'2025-06-16','12:00:00',1),(38,'Joaquín',2,'2025-06-16','13:30:00',1),(39,'Cliente',2,'2025-06-12','09:00:00',2),(40,'Andresito',2,'2025-06-12','12:00:00',1),(41,'Prueba',1,'2025-06-13','11:30:00',1),(42,'Aloa',1,'2025-06-13','12:00:00',2);
/*!40000 ALTER TABLE `citas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inventario`
--

DROP TABLE IF EXISTS `inventario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventario` (
  `articulo_id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `precio` decimal(6,2) NOT NULL,
  `stock` int NOT NULL,
  PRIMARY KEY (`articulo_id`)
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventario`
--

LOCK TABLES `inventario` WRITE;
/*!40000 ALTER TABLE `inventario` DISABLE KEYS */;
INSERT INTO `inventario` VALUES (9,'Shampoo para hombres',6.99,30),(10,'Acondicionador revitalizante',7.49,25),(11,'Gel fijador fuerte',5.99,40),(12,'Cera para cabello',8.49,35),(13,'Pomada mate',9.99,20),(14,'Aceite para barba',12.99,18),(15,'Bálsamo para barba',11.49,22),(16,'Crema de afeitar',4.99,45),(17,'Aftershave clásico',6.49,38),(18,'Espuma de afeitar',4.75,42),(19,'Loción facial',10.99,15),(20,'Toallas desechables',0.50,50),(21,'Navajas de afeitar',0.80,48),(22,'Peine profesional',2.99,30),(23,'Cepillo para barba',6.49,25),(24,'Máquina de corte básica',49.99,10),(25,'Máquina de corte premium',89.99,5),(26,'Tijeras de corte',14.99,18),(27,'Tijeras de entresacar',17.49,12),(28,'Capa de corte',9.99,20),(29,'Espejo de mano',7.99,15),(30,'Brocha de afeitar',5.49,22),(31,'Desinfectante para herramientas',3.99,30),(32,'Spray fijador',6.99,28),(33,'Polvo matificante',4.49,35),(34,'Crema hidratante',9.49,16),(35,'Cera negra para barba',7.89,24),(36,'Gel exfoliante',8.99,12),(37,'Serum facial',11.99,14),(38,'Shampoo para barba',6.99,27),(39,'Toallas calientes',1.00,50),(40,'Batas desechables',2.00,40),(41,'Aceite esencial para masaje',10.49,19),(42,'Crema para masajes',8.75,21),(43,'Limpiador de máquinas',5.99,23),(44,'Guantes de látex',0.25,50),(45,'Spray limpiador facial',6.49,26),(46,'Cera en spray',7.25,32),(47,'Bálsamo labial',3.49,30),(48,'Mascarilla facial',4.99,20);
/*!40000 ALTER TABLE `inventario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `servicios`
--

DROP TABLE IF EXISTS `servicios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `servicios` (
  `servicio_id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `descripcion` text,
  `precio` decimal(6,2) NOT NULL,
  PRIMARY KEY (`servicio_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `servicios`
--

LOCK TABLES `servicios` WRITE;
/*!40000 ALTER TABLE `servicios` DISABLE KEYS */;
INSERT INTO `servicios` VALUES (1,'Corte','Corte de pelo',7.50),(2,'Barbas','Afeitado',4.50),(3,'Degradado','Corte degradado',9.00),(4,'Tinte','Cabello teñido',50.00);
/*!40000 ALTER TABLE `servicios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `usuario_id` int NOT NULL AUTO_INCREMENT,
  `nombre_usuario` varchar(50) NOT NULL,
  `correo` varchar(100) NOT NULL,
  `contrasena` varchar(100) NOT NULL,
  `tipo_usuario` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`usuario_id`),
  UNIQUE KEY `nombre_usuario` (`nombre_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (1,'Admin','AdminBarber@gmail.com','1234',1),(2,'Prueba','PruebaBarber@gmail.com','1234',0),(3,'Prueba2','a@gmail.com','03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4',0);
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-13 19:20:38
