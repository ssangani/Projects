DROP TABLE IF EXISTS `menuAddonOptions`;
DROP TABLE IF EXISTS `menuItem`;
DROP TABLE IF EXISTS `menuAddon`;
DROP TABLE IF EXISTS `menuCategory`;
DROP TABLE IF EXISTS `menuFamily`;

CREATE TABLE `menuFamily` (`menuFamilyId` int(11) NOT NULL AUTO_INCREMENT,`menuFamilyName` varchar(100) DEFAULT NULL,PRIMARY KEY (`menuFamilyId`));

CREATE TABLE `menuCategory` (`menuCategoryId` int(11) NOT NULL AUTO_INCREMENT,`menuCategoryName` varchar(100) DEFAULT NULL,PRIMARY KEY (`menuCategoryId`));

CREATE TABLE `menuAddon` (`menuAddonId` int(11) NOT NULL AUTO_INCREMENT,`menuAddonName` varchar(100) DEFAULT NULL,PRIMARY KEY (`menuAddonId`));

CREATE TABLE `menuItem` (`menuItemId` int(11) NOT NULL AUTO_INCREMENT,`menuItemName` varchar(100) DEFAULT NULL,`menuItemDesc` varchar(1000) DEFAULT NULL,`menuItemPrice` float DEFAULT 0,`refCategoryId` int(11) DEFAULT NULL,`refFamilyId` int(11) DEFAULT NULL,`menuItemStartTime` datetime DEFAULT NULL,`menuItemEndTime` datetime DEFAULT NULL,`hasAddonOptions` tinyint DEFAULT 0,`maxLimitedAddons` tinyint DEFAULT 0,`menuItemImageUrl` varchar(1000) DEFAULT NULL,`chefsPick` tinyint DEFAULT NULL,PRIMARY KEY (`menuItemId`),KEY `refFamilyId` (`refFamilyId`),KEY `refCategoryId` (`refCategoryId`),CONSTRAINT `menuFamilyFK` FOREIGN KEY (`refFamilyId`) REFERENCES `menuFamily` (`menuFamilyId`),CONSTRAINT `menuCategoryFK` FOREIGN KEY (`refCategoryId`) REFERENCES `menuCategory` (`menuCategoryId`));

CREATE TABLE `menuAddonOptions` (`menuAddonOptionsId` int(11) NOT NULL AUTO_INCREMENT,`menuItemId` int(11) NOT NULL,`menuAddonId` int(11) NOT NULL,`menuAddonPrice` float DEFAULT 0,`isLimited` tinyint DEFAULT 0,PRIMARY KEY (`menuAddonOptionsId`),KEY `menuItemId` (`menuItemId`),KEY `menuAddonId` (`menuAddonId`),CONSTRAINT `menuItemFK` FOREIGN KEY (`menuItemId`) REFERENCES `menuItem` (`menuItemId`),CONSTRAINT `menuAddonFK` FOREIGN KEY (`menuAddonId`) REFERENCES `menuAddon` (`menuAddonId`));