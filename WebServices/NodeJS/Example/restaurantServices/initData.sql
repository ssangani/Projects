-- Adding menu families
INSERT INTO `menuFamily` (`menuFamilyName`) VALUES ('Breakfast'), ('Lunch'), ('Dinner'), ('Beverages');

-- Adding menu categories
INSERT INTO `menuCategory` (`menuCategoryName`) VALUES ('Appetizer'), ('Salad'), ('Pizza'), ('Sandwich'), ('Soup'), ('Dessert'), ('Milkshakes'), ('Meal'), ('Beer');

-- Adding addons
INSERT INTO `menuAddon` (`menuAddonName`) VALUES ('Jalapeno'), ('Roasted Red Peppers'), ('Pepperoni'), ('Chicken'), ('Feta Cheese'), ('Olives'), ('Onions'), ('Fountain Soda'), ('Potato Crisps'), ('Fries');

-- Adding menu items
INSERT INTO `menuItem` (`menuItemName`, `menuItemDesc`, `menuItemPrice`, `refCategoryId`, 
	`refFamilyId`, `menuItemStartTime`, `menuItemEndTime`, `hasAddonOptions`, `maxLimitedAddons`) VALUES
    ('Pancakes', '', 4.99, (SELECT menuCategoryId FROM menuCategory WHERE menuCategoryName = 'Meal' LIMIT 1), (SELECT menuFamilyId FROM menuFamily WHERE menuFamilyName = 'Breakfast' LIMIT 1), '1970-01-01-06:00:00', '1970-01-01-11:00:00', 0, 0),
    ('Mediterranean Salad', '', 6.99, (SELECT menuCategoryId FROM menuCategory WHERE menuCategoryName = 'Salad' LIMIT 1), (SELECT menuFamilyId FROM menuFamily WHERE menuFamilyName = 'Lunch' LIMIT 1), '1970-01-01-11:00:00', '1970-01-01-16:00:00', 0, 0),
    ('Pizza', 'Build Your Own Pizza', 6.99, (SELECT menuCategoryId FROM menuCategory WHERE menuCategoryName = 'Pizza' LIMIT 1), (SELECT menuFamilyId FROM menuFamily WHERE menuFamilyName = 'Lunch' LIMIT 1), '1970-01-01-11:00:00', '1970-01-01-16:00:00', 1, 4),
    ('Mac & Cheese', 'e', 6.99, (SELECT menuCategoryId FROM menuCategory WHERE menuCategoryName = 'Meal' LIMIT 1), (SELECT menuFamilyId FROM menuFamily WHERE menuFamilyName = 'Lunch' LIMIT 1), '1970-01-01-11:00:00', '1970-01-01-16:00:00', 0, 0),
    ('Egg Sandwich', '', 6.99, (SELECT menuCategoryId FROM menuCategory WHERE menuCategoryName = 'Sandwich' LIMIT 1), (SELECT menuFamilyId FROM menuFamily WHERE menuFamilyName = 'Breakfast' LIMIT 1), '1970-01-01-11:00:00', '1970-01-01-16:00:00', 0, 0),
    ('Sandwich of the Day', 'Chef\'s Pick', 8.99, (SELECT menuCategoryId FROM menuCategory WHERE menuCategoryName = 'Sandwich' LIMIT 1), (SELECT menuFamilyId FROM menuFamily WHERE menuFamilyName = 'Lunch' LIMIT 1), '1970-01-01-11:00:00', '1970-01-01-16:00:00', 1, 0),
    ('Vanilla Shake', '', 8.99, (SELECT menuCategoryId FROM menuCategory WHERE menuCategoryName = 'Milkshakes' LIMIT 1), (SELECT menuFamilyId FROM menuFamily WHERE menuFamilyName = 'Beverages' LIMIT 1), '1970-01-01-11:00:00', '1970-01-01-23:00:00', 0, 0),
	('Sam Adams', '', 4.99, (SELECT menuCategoryId FROM menuCategory WHERE menuCategoryName = 'Beer' LIMIT 1), (SELECT menuFamilyId FROM menuFamily WHERE menuFamilyName = 'Beverages' LIMIT 1), '1970-01-01-11:00:00', '1970-01-01-23:00:00', 0, 0),
    ('Draft Beer', 'On the tap', 4.99, (SELECT menuCategoryId FROM menuCategory WHERE menuCategoryName = 'Beer' LIMIT 1), (SELECT menuFamilyId FROM menuFamily WHERE menuFamilyName = 'Beverages' LIMIT 1), '1970-01-01-11:00:00', '1970-01-01-23:00:00', 0, 0);


-- Linking addons to items
INSERT INTO `menuAddonOptions` (`menuItemId`, `menuAddonId`, `menuAddonPrice`, `isLimited`) VALUES 
	((SELECT menuItemId FROM menuItem WHERE menuItemName = 'Pizza' LIMIT 1), (SELECT menuAddonId FROM menuAddon WHERE menuAddonName = 'Jalapeno' LIMIT 1), 1.0, 1),
    ((SELECT menuItemId FROM menuItem WHERE menuItemName = 'Pizza' LIMIT 1), (SELECT menuAddonId FROM menuAddon WHERE menuAddonName = 'Roasted Red Peppers' LIMIT 1), 1.0, 1),
    ((SELECT menuItemId FROM menuItem WHERE menuItemName = 'Pizza' LIMIT 1), (SELECT menuAddonId FROM menuAddon WHERE menuAddonName = 'Pepperoni' LIMIT 1), 1.0, 1),
    ((SELECT menuItemId FROM menuItem WHERE menuItemName = 'Pizza' LIMIT 1), (SELECT menuAddonId FROM menuAddon WHERE menuAddonName = 'Chicken' LIMIT 1), 1.0, 1),
    ((SELECT menuItemId FROM menuItem WHERE menuItemName = 'Pizza' LIMIT 1), (SELECT menuAddonId FROM menuAddon WHERE menuAddonName = 'Feta Cheese' LIMIT 1), 1.0, 1),
    ((SELECT menuItemId FROM menuItem WHERE menuItemName = 'Pizza' LIMIT 1), (SELECT menuAddonId FROM menuAddon WHERE menuAddonName = 'Olives' LIMIT 1), 1.0, 1),
    ((SELECT menuItemId FROM menuItem WHERE menuItemName = 'Pizza' LIMIT 1), (SELECT menuAddonId FROM menuAddon WHERE menuAddonName = 'Onions' LIMIT 1), 1.0, 1),
    ((SELECT menuItemId FROM menuItem WHERE menuItemName = 'Sandwich of the Day' LIMIT 1), (SELECT menuAddonId FROM menuAddon WHERE menuAddonName = 'Fountain Soda' LIMIT 1), 3.0, 0),
    ((SELECT menuItemId FROM menuItem WHERE menuItemName = 'Sandwich of the Day' LIMIT 1), (SELECT menuAddonId FROM menuAddon WHERE menuAddonName = 'Potato Crisps' LIMIT 1), 3.0, 0),
    ((SELECT menuItemId FROM menuItem WHERE menuItemName = 'Sandwich of the Day' LIMIT 1), (SELECT menuAddonId FROM menuAddon WHERE menuAddonName = 'Fries' LIMIT 1), 3.0, 0);