-- phpMyAdmin SQL Dump
-- version 4.8.4
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 21, 2018 at 02:58 PM
-- Server version: 10.1.37-MariaDB
-- PHP Version: 7.3.0

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `bank`
--

-- --------------------------------------------------------

--
-- Table structure for table `customers`
--

CREATE TABLE `customers` (
  `name` varchar(50) NOT NULL,
  `account_number` text NOT NULL,
  `password` varchar(10) NOT NULL,
  `balance` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `customers`
--

INSERT INTO `customers` (`name`, `account_number`, `password`, `balance`) VALUES
('Ramanan', 'AC1544197763473', 'sbn234', 3000),
('Kumar', 'AC1544197763474', 'lvn234', 2000),
('Rajesh', 'AC1544197857102', 'sbk234', 1000),
('Vignesh', 'AC1544197886206', 'wjh234', 5500),
('Ram', 'AC1544197907515', 'sbn234', 5100),
('Vishnu', 'AC1544197907516', 'wjt432', 1000),
('Muthu', 'AC1545378052337', 'nvu234', 2000),
('Hari', 'AC1545400160498', 'ibs234', 4400);

-- --------------------------------------------------------

--
-- Table structure for table `history`
--

CREATE TABLE `history` (
  `ID` int(11) NOT NULL,
  `account_number` text NOT NULL,
  `transaction_type` varchar(50) NOT NULL,
  `amount` bigint(20) NOT NULL,
  `balance` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `history`
--

INSERT INTO `history` (`ID`, `account_number`, `transaction_type`, `amount`, `balance`) VALUES
(1, 'AC1544197763473', 'Initial Creation', 1000, 1000),
(2, 'AC1544197763474', 'Initial Creation', 1000, 1000),
(3, 'AC1544197857102', 'Initial Creation', 1000, 1000),
(4, 'AC1544197886206', 'Initial Creation', 1000, 1000),
(5, 'AC1544197907515', 'Initial Creation', 1000, 1000),
(6, 'AC1544197907515', 'Deposit', 5000, 6000),
(7, 'AC1544197907515', 'Withdraw', 500, 5500),
(8, 'AC1544197907515', 'Transfer to AC1544197763473', 2000, 3500),
(9, 'AC1544197763473', 'Credit from AC1544197907515', 2000, 3000),
(10, 'AC1544197763474', 'Deposit', 6000, 7000),
(11, 'AC1544197763474', 'Transfer to AC1544197886206', 5000, 2000),
(12, 'AC1544197886206', 'Credit from AC1544197763474', 5000, 6000),
(13, 'AC1544197886206', 'Withdraw', 500, 5500),
(14, 'AC1544197907516', 'Initial Creation', 1000, 1000),
(15, 'AC1545378052337', 'Initial Creation', 1000, 1000),
(16, 'AC1545378052337', 'Deposit', 5000, 6000),
(17, 'AC1545378052337', 'Withdraw', 3000, 3000),
(18, 'AC1545378052337', 'Transfer to AC1544197907515', 1000, 2000),
(19, 'AC1544197907515', 'Credit from AC1545378052337', 1000, 4500),
(20, 'AC1545400160498', 'Initial Creation', 1000, 1000),
(21, 'AC1545400160498', 'Deposit', 5000, 6000),
(22, 'AC1545400160498', 'Withdraw', 1000, 5000),
(23, 'AC1545400160498', 'Transfer to AC1544197907515', 600, 4400),
(24, 'AC1544197907515', 'Credit from AC1545400160498', 600, 5100);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `customers`
--
ALTER TABLE `customers`
  ADD PRIMARY KEY (`account_number`(20));

--
-- Indexes for table `history`
--
ALTER TABLE `history`
  ADD PRIMARY KEY (`ID`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `history`
--
ALTER TABLE `history`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
