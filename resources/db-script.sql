-- phpMyAdmin SQL Dump
-- version 3.2.4
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Nov 25, 2010 at 04:15 PM
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
  `binary_index` blob,
  PRIMARY KEY (`file_id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `file`
--

INSERT INTO `file` (`file_id`, `file_path`, `repository_id`, `binary_index`) VALUES
(1, 'asdf', 1, 0xaced0005737200136a6176612e7574696c2e41727261794c6973747881d21d99c7619d03000149000473697a6578700000000077040000000a78);

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
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=4 ;

--
-- Dumping data for table `repository`
--

INSERT INTO `repository` (`repository_id`, `repository_name`, `last_analyzed_revision`) VALUES
(1, 'svn_local', '''0''');
