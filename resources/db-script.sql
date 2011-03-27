-- phpMyAdmin SQL Dump
-- version 3.2.4
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Mar 27, 2011 at 12:46 PM
-- Server version: 5.1.41
-- PHP Version: 5.3.1

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `codesearch`
--
CREATE DATABASE `codesearch` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `codesearch`;

-- --------------------------------------------------------

--
-- Table structure for table `file`
--

CREATE TABLE IF NOT EXISTS `file` (
  `file_id` int(11) NOT NULL AUTO_INCREMENT,
  `file_path` varchar(500) NOT NULL,
  `repository_id` int(11) NOT NULL,
  `binary_index` longblob,
  `usages` longblob,
  PRIMARY KEY (`file_id`),
  KEY `repository_id` (`repository_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=4582 ;

--
-- Dumping data for table `file`
--


-- --------------------------------------------------------

--
-- Table structure for table `import`
--

CREATE TABLE IF NOT EXISTS `import` (
  `source_file_id` int(11) NOT NULL,
  `target_file_path` varchar(500) NOT NULL,
  PRIMARY KEY (`source_file_id`,`target_file_path`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `import`
--


-- --------------------------------------------------------

--
-- Table structure for table `repository`
--

CREATE TABLE IF NOT EXISTS `repository` (
  `repository_id` int(11) NOT NULL AUTO_INCREMENT,
  `repository_name` varchar(255) NOT NULL,
  `last_analyzed_revision` varchar(255) NOT NULL DEFAULT '''0''',
  PRIMARY KEY (`repository_id`),
  UNIQUE KEY `repository_name` (`repository_name`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `repository`
--


-- --------------------------------------------------------

--
-- Table structure for table `type`
--

CREATE TABLE IF NOT EXISTS `type` (
  `type_id` int(11) NOT NULL AUTO_INCREMENT,
  `full_name` varchar(1000) NOT NULL,
  `file_id` int(11) NOT NULL,
  `repo_id` int(11) NOT NULL,
  PRIMARY KEY (`type_id`),
  KEY `file_id` (`file_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1782 ;

--
-- Dumping data for table `type`
--


--
-- Constraints for dumped tables
--

--
-- Constraints for table `file`
--
ALTER TABLE `file`
  ADD CONSTRAINT `file_ibfk_1` FOREIGN KEY (`repository_id`) REFERENCES `repository` (`repository_id`) ON DELETE CASCADE;

--
-- Constraints for table `import`
--
ALTER TABLE `import`
  ADD CONSTRAINT `import_ibfk_1` FOREIGN KEY (`source_file_id`) REFERENCES `file` (`file_id`) ON DELETE CASCADE;

--
-- Constraints for table `type`
--
ALTER TABLE `type`
  ADD CONSTRAINT `type_ibfk_1` FOREIGN KEY (`file_id`) REFERENCES `file` (`file_id`) ON DELETE CASCADE;
