-- phpMyAdmin SQL Dump
-- version 3.2.4
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Jan 23, 2011 at 01:55 PM
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

DROP TABLE IF EXISTS `file`;
CREATE TABLE IF NOT EXISTS `file` (
  `file_id` int(11) NOT NULL AUTO_INCREMENT,
  `file_path` varchar(500) NOT NULL,
  `repository_id` int(11) NOT NULL,
  `binary_index` longblob,
  `usages` longblob,
  `imports` blob NOT NULL,
  PRIMARY KEY (`file_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

--
-- Dumping data for table `file`
--


-- --------------------------------------------------------

--
-- Table structure for table `import`
--

DROP TABLE IF EXISTS `import`;
CREATE TABLE IF NOT EXISTS `import` (
  `source_file_id` int(11) NOT NULL,
  `fully_qualified_class_name` varchar(500) NOT NULL,
  PRIMARY KEY (`source_file_id`,`fully_qualified_class_name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `import`
--


-- --------------------------------------------------------

--
-- Table structure for table `repository`
--

DROP TABLE IF EXISTS `repository`;
CREATE TABLE IF NOT EXISTS `repository` (
  `repository_id` int(11) NOT NULL AUTO_INCREMENT,
  `repository_name` varchar(255) NOT NULL,
  `last_analyzed_revision` varchar(255) NOT NULL DEFAULT '''0''',
  PRIMARY KEY (`repository_id`),
  UNIQUE KEY `repository_name` (`repository_name`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=4 ;

--
-- Dumping data for table `repository`
--

INSERT INTO `repository` (`repository_id`, `repository_name`, `last_analyzed_revision`) VALUES
(1, 'svn_local', '1294680881000');

-- --------------------------------------------------------

--
-- Table structure for table `type`
--

DROP TABLE IF EXISTS `type`;
CREATE TABLE IF NOT EXISTS `type` (
  `type_id` int(11) NOT NULL AUTO_INCREMENT,
  `full_name` varchar(1000) NOT NULL,
  `file_id` int(11) NOT NULL,
  `repo_id` int(11) NOT NULL,
  PRIMARY KEY (`type_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

--
-- Dumping data for table `type`
--

