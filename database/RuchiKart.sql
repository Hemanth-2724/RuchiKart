CREATE DATABASE IF NOT EXISTS `food_delivery_db`;
USE `food_delivery_db`;

-- Drop tables in reverse order of dependency
DROP TABLE IF EXISTS `OrderItem`;
DROP TABLE IF EXISTS `OrderTable`;
DROP TABLE IF EXISTS `Menu`;
DROP TABLE IF EXISTS `Restaurant`;
DROP TABLE IF EXISTS `Customer`;
DROP TABLE IF EXISTS `RestaurantOwner`;
DROP TABLE IF EXISTS `DeliveryPartner`;
DROP TABLE IF EXISTS `Admin`;

-- 1. Customer Table
CREATE TABLE `Customer` (
    `CustomerID` INT AUTO_INCREMENT PRIMARY KEY,
    `Username` VARCHAR(100) NOT NULL UNIQUE,
    `Password` VARCHAR(255) NOT NULL,
    `Email` VARCHAR(100) NOT NULL UNIQUE,
    `Address` TEXT,
    `CreatedDate` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `LastLoginDate` DATETIME DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1000000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. RestaurantOwner Table
CREATE TABLE `RestaurantOwner` (
    `OwnerID` INT AUTO_INCREMENT PRIMARY KEY,
    `Username` VARCHAR(100) NOT NULL UNIQUE,
    `Password` VARCHAR(255) NOT NULL,
    `Email` VARCHAR(100) NOT NULL UNIQUE,
    `Address` TEXT,
    `CreatedDate` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `LastLoginDate` DATETIME DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=2000000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. DeliveryPartner Table
CREATE TABLE `DeliveryPartner` (
    `PartnerID` INT AUTO_INCREMENT PRIMARY KEY,
    `Username` VARCHAR(100) NOT NULL UNIQUE,
    `Password` VARCHAR(255) NOT NULL,
    `Email` VARCHAR(100) NOT NULL UNIQUE,
    `Address` TEXT,
    `CreatedDate` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `LastLoginDate` DATETIME DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=3000000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Admin Table
CREATE TABLE `Admin` (
    `AdminID` INT AUTO_INCREMENT PRIMARY KEY,
    `Username` VARCHAR(100) NOT NULL UNIQUE,
    `Password` VARCHAR(255) NOT NULL,
    `Email` VARCHAR(100) NOT NULL UNIQUE,
    `Address` TEXT,
    `CreatedDate` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `LastLoginDate` DATETIME DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=4000000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Restaurant Table (linked to RestaurantOwner)
CREATE TABLE `Restaurant` (
    `RestaurantID` INT AUTO_INCREMENT PRIMARY KEY,
    `Name` VARCHAR(150) NOT NULL,
    `CuisineType` VARCHAR(100) NOT NULL,
    `DeliveryTime` INT NOT NULL COMMENT 'Estimated delivery time in minutes',
    `Address` TEXT NOT NULL,
    `Rating` DECIMAL(3, 2) DEFAULT 0.00 CHECK (`Rating` >= 0.00 AND `Rating` <= 5.00),
    `IsActive` BOOLEAN NOT NULL DEFAULT TRUE,
    `ImagePath` VARCHAR(255) DEFAULT NULL,
    `IsVeg` BOOLEAN NOT NULL DEFAULT FALSE,
    `OwnerID` INT DEFAULT NULL,
    CONSTRAINT `fk_restaurant_owner` 
        FOREIGN KEY (`OwnerID`) 
        REFERENCES `RestaurantOwner` (`OwnerID`) 
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. Menu Table
CREATE TABLE `Menu` (
    `MenuID` INT AUTO_INCREMENT PRIMARY KEY,
    `RestaurantID` INT NOT NULL,
    `ItemName` VARCHAR(150) NOT NULL,
    `Description` TEXT DEFAULT NULL,
    `Price` DECIMAL(10, 2) NOT NULL,
    `IsAvailable` BOOLEAN NOT NULL DEFAULT TRUE,
    `ImagePath` VARCHAR(255) DEFAULT NULL,
    `IsVeg` BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT `fk_menu_restaurant` 
        FOREIGN KEY (`RestaurantID`) 
        REFERENCES `Restaurant` (`RestaurantID`) 
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. OrderTable Table (linked to Customer)
CREATE TABLE `OrderTable` (
    `OrderID` INT AUTO_INCREMENT PRIMARY KEY,
    `UserID` INT NOT NULL,
    `OrderDate` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `TotalAmount` DECIMAL(10, 2) NOT NULL,
    `Status` ENUM('pending', 'confirmed', 'preparing', 'ready', 'ready_for_pickup', 'out_for_delivery', 'delivered', 'cancelled') NOT NULL DEFAULT 'pending',
    `PaymentMethod` ENUM('cash_on_delivery', 'credit_card', 'debit_card', 'upi', 'net_banking') NOT NULL,
    `RestaurantID` INT NOT NULL,
    `DeliveryPartnerID` INT DEFAULT NULL,
    CONSTRAINT `fk_orders_customer` 
        FOREIGN KEY (`UserID`) 
        REFERENCES `Customer` (`CustomerID`) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_orders_restaurant` 
        FOREIGN KEY (`RestaurantID`) 
        REFERENCES `Restaurant` (`RestaurantID`) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_orders_deliverypartner` 
        FOREIGN KEY (`DeliveryPartnerID`) 
        REFERENCES `DeliveryPartner` (`PartnerID`) 
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. OrderItem Table
CREATE TABLE `OrderItem` (
    `OrderItemID` INT AUTO_INCREMENT PRIMARY KEY,
    `OrderID` INT NOT NULL,
    `Quantity` INT NOT NULL CHECK (`Quantity` > 0),
    `ItemTotal` DECIMAL(10, 2) NOT NULL,
    `MenuID` INT NOT NULL,
    CONSTRAINT `fk_orderitem_order` 
        FOREIGN KEY (`OrderID`) 
        REFERENCES `OrderTable` (`OrderID`) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_orderitem_menu` 
        FOREIGN KEY (`MenuID`) 
        REFERENCES `Menu` (`MenuID`) 
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- SEED USERS (All passwords are 'admin123')
-- ==========================================

-- Seed Admins
INSERT INTO `Admin` (`AdminID`, `Username`, `Password`, `Email`, `Address`) VALUES
(4000001, 'admin_user', '$2a$12$AX.1a4wsfFQt9D2/TTif6uVFDJAeHdWU08czIM6ltR1h.4V6S7vb6', 'admin@ruchikart.com', 'Main Office, RR Nagar, Bengaluru');

-- Seed Customers
INSERT INTO `Customer` (`CustomerID`, `Username`, `Password`, `Email`, `Address`) VALUES
(1000001, 'Hemanth', '$2a$12$AX.1a4wsfFQt9D2/TTif6uVFDJAeHdWU08czIM6ltR1h.4V6S7vb6', 'hemanth@gmail.com', 'No 24, BEML Layout 3rd Stage, RR Nagar, Bengaluru'),
(1000002, 'amit_k', '$2a$12$AX.1a4wsfFQt9D2/TTif6uVFDJAeHdWU08czIM6ltR1h.4V6S7vb6', 'customer1@ruchikart.com', 'Flat 402, Ideal Homes Apartments, RR Nagar, Bengaluru'),
(1000003, 'priya_sharma', '$2a$12$AX.1a4wsfFQt9D2/TTif6uVFDJAeHdWU08czIM6ltR1h.4V6S7vb6', 'customer2@ruchikart.com', 'No 24, BEML Layout 3rd Stage, RR Nagar, Bengaluru');

-- Seed Delivery Partners
INSERT INTO `DeliveryPartner` (`PartnerID`, `Username`, `Password`, `Email`, `Address`) VALUES
(3000001, 'delivery_raju', '$2a$12$AX.1a4wsfFQt9D2/TTif6uVFDJAeHdWU08czIM6ltR1h.4V6S7vb6', 'delivery1@ruchikart.com', 'Ideal Homes Township, RR Nagar, Bengaluru'),
(3000002, 'delivery_shyam', '$2a$12$AX.1a4wsfFQt9D2/TTif6uVFDJAeHdWU08czIM6ltR1h.4V6S7vb6', 'delivery2@ruchikart.com', 'BEML Layout, RR Nagar, Bengaluru'),
(3000003, 'delivery_babu', '$2a$12$AX.1a4wsfFQt9D2/TTif6uVFDJAeHdWU08czIM6ltR1h.4V6S7vb6', 'delivery3@ruchikart.com', 'Kenchanahalli Road, RR Nagar, Bengaluru');

-- Seed Restaurant Owners
INSERT INTO `RestaurantOwner` (`OwnerID`, `Username`, `Password`, `Email`, `Address`) VALUES
(2000001, 'owner_paakashala', '$2a$12$AX.1a4wsfFQt9D2/TTif6uVFDJAeHdWU08czIM6ltR1h.4V6S7vb6', 'owner1@ruchikart.com', '60 Feet Road, Ideal Homes Township, RR Nagar, Bengaluru'),
(2000002, 'owner_nagarjuna', '$2a$12$AX.1a4wsfFQt9D2/TTif6uVFDJAeHdWU08czIM6ltR1h.4V6S7vb6', 'owner2@ruchikart.com', 'BEML Layout, RR Nagar, Bengaluru'),
(2000003, 'owner_empire', '$2a$12$AX.1a4wsfFQt9D2/TTif6uVFDJAeHdWU08czIM6ltR1h.4V6S7vb6', 'owner3@ruchikart.com', '80 Feet Road, Halagevaderahalli, RR Nagar, Bengaluru'),
(2000004, 'owner_a2b', '$2a$12$AX.1a4wsfFQt9D2/TTif6uVFDJAeHdWU08czIM6ltR1h.4V6S7vb6', 'owner4@ruchikart.com', 'Mysore Road, RR Nagar, Bengaluru'),
(2000005, 'owner_truffles', '$2a$12$AX.1a4wsfFQt9D2/TTif6uVFDJAeHdWU08czIM6ltR1h.4V6S7vb6', 'owner5@ruchikart.com', 'Channasandra Main Road, RR Nagar, Bengaluru'),
(2000006, 'owner_manis', '$2a$12$AX.1a4wsfFQt9D2/TTif6uVFDJAeHdWU08czIM6ltR1h.4V6S7vb6', 'owner6@ruchikart.com', 'Ideal Homes Layout, RR Nagar, Bengaluru'),
(2000007, 'owner_leons', '$2a$12$AX.1a4wsfFQt9D2/TTif6uVFDJAeHdWU08czIM6ltR1h.4V6S7vb6', 'owner7@ruchikart.com', 'Gopalan Arcade Mall, RR Nagar, Bengaluru'),
(2000008, 'owner_polarbear', '$2a$12$AX.1a4wsfFQt9D2/TTif6uVFDJAeHdWU08czIM6ltR1h.4V6S7vb6', 'owner8@ruchikart.com', 'Opp. Ideal Homes Library, RR Nagar, Bengaluru'),
(2000009, 'owner_meghana', '$2a$12$AX.1a4wsfFQt9D2/TTif6uVFDJAeHdWU08czIM6ltR1h.4V6S7vb6', 'owner9@ruchikart.com', 'RR Nagar Main Road, RR Nagar, Bengaluru'),
(2000010, 'owner_pizzahut', '$2a$12$AX.1a4wsfFQt9D2/TTif6uVFDJAeHdWU08czIM6ltR1h.4V6S7vb6', 'owner10@ruchikart.com', 'Remco Layout, RR Nagar, Bengaluru');

-- ==========================================
-- SEED 10 RESTAURANTS (Near RR Nagar, linked to Owners)
-- ==========================================
INSERT INTO `Restaurant` (`RestaurantID`, `Name`, `CuisineType`, `DeliveryTime`, `Address`, `Rating`, `IsActive`, `ImagePath`, `OwnerID`) VALUES
(1, 'Paakashala', 'South Indian, North Indian, Chinese (Pure Veg)', 25, '123, 60 Feet Road, Ideal Homes Township, RR Nagar, Bengaluru', 4.30, 1, 'https://paakashala.com/wp-content/uploads/2025/10/1.webp', 2000001),
(2, 'Nagarjuna Restaurant', 'Andhra, Biryani (Veg & Non-Veg)', 30, '45, BEML Layout, 5th Stage, RR Nagar, Bengaluru', 4.50, 1, 'https://dynamic-media-cdn.tripadvisor.com/media/photo-o/30/ce/c9/20/caption.jpg?w=1200&h=1200&s=1', 2000002),
(3, 'Empire Restaurant', 'North Indian, Kebabs, Biryani (Veg & Non-Veg)', 35, '80 Feet Main Road, Halagevaderahalli, RR Nagar, Bengaluru', 4.20, 1, 'https://dynamic-media-cdn.tripadvisor.com/media/photo-o/10/5b/45/0d/empire-restaurant-mysore.jpg?w=900&h=500&s=1', 2000003),
(4, 'Adyar Ananda Bhavan (A2B)', 'South Indian, Sweets (Pure Veg)', 20, 'Near Gopalan Arcade Mall, Mysore Road, RR Nagar, Bengaluru', 4.10, 1, 'https://img.lentlo.com/images/listing-img-108720/w-828.webp', 2000004),
(5, 'Truffles', 'Burgers, Cafe, Desserts (Veg & Non-Veg)', 30, 'Channasandra Main Road, Near Ideal Homes, RR Nagar, Bengaluru', 4.40, 1, 'https://b.zmtcdn.com/data/pictures/0/51040/3614a681863f438e8a1018e313ed070f.jpg', 2000005),
(6, 'Mani\'s Dum Biryani', 'Biryani, Andhra (Veg & Non-Veg)', 25, 'Double Road, Ideal Homes Layout, RR Nagar, Bengaluru', 4.30, 1, 'https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=500&auto=format&fit=crop&q=60', 2000006),
(7, 'Leon\'s - Burgers & Salads', 'American, Fast Food (Veg & Non-Veg)', 25, 'Gopalan Arcade Mall, RR Nagar, Bengaluru', 4.30, 1, 'https://content.jdmagicbox.com/v2/comp/bangalore/a7/080pxx80.xx80.250829110900.b2a7/catalogue/leon-s-burgers-and-wings-whitefield-bangalore-fast-food-k4qk6sxx64.jpg', 2000007),
(8, 'Polar Bear', 'Ice Cream, Desserts (Pure Veg)', 20, 'Opposite Ideal Homes Library, RR Nagar, Bengaluru', 4.50, 1, 'https://b.zmtcdn.com/data/pictures/0/21798770/593623f606661ecef068cc8152a79912.jpeg?fit=around|960:500&crop=960:500;*,*', 2000008),
(9, 'Meghana Foods', 'Andhra Biryani, Kebabs (Veg & Non-Veg)', 35, 'Kenchenahalli, RR Nagar Main Road, RR Nagar, Bengaluru', 4.60, 1, 'https://www.mappls.com/place/F8J1J8_1704969215969_0.jpeg', 2000009),
(10, 'Pizza Hut', 'Italian, Pizza (Veg & Non-Veg)', 35, 'Shree Arcade, Remco Layout, RR Nagar, Bengaluru', 4.00, 1, 'https://dynamic-media-cdn.tripadvisor.com/media/photo-o/0f/29/e2/a8/from-outside.jpg?w=900&h=-1&s=1', 2000010);

-- ==========================================
-- SEED MENUS (5 Items per Restaurant)
-- ==========================================

-- Restaurant 1: Paakashala (Veg)
INSERT INTO `Menu` (`RestaurantID`, `ItemName`, `Description`, `Price`, `IsAvailable`, `ImagePath`, `IsVeg`) VALUES
(1, 'Idly (2 pcs)', 'Fluffy steamed rice cakes served with sambar and chutneys.', 60.00, 1, 'https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=500&auto=format&fit=crop&q=80', 1),
(1, 'Vada (1 pc)', 'Crispy fried lentil donut served with sambar.', 35.00, 1, 'https://images.unsplash.com/photo-1601050690597-df056fb4ce78?w=500&auto=format&fit=crop&q=80', 1),
(1, 'Masala Dosa', 'Golden crispy rice crepe filled with spiced potato mash.', 90.00, 1, 'https://images.unsplash.com/photo-1668236543090-82eba5ee5976?w=500&auto=format&fit=crop&q=80', 1),
(1, 'South Indian Meals', 'A full meal consisting of Rice, Sambar, Rasam, Poriyal, Curd, and Appalam.', 160.00, 1, 'https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=500&auto=format&fit=crop&q=80', 1),
(1, 'Paneer Butter Masala', 'Cottage cheese cubes simmered in a rich tomato and cream gravy.', 220.00, 1, 'https://images.unsplash.com/photo-1631452180519-c014fe946bc7?w=500&auto=format&fit=crop&q=80', 1);

-- Restaurant 2: Nagarjuna (Veg & Non-Veg)
INSERT INTO `Menu` (`RestaurantID`, `ItemName`, `Description`, `Price`, `IsAvailable`, `ImagePath`, `IsVeg`) VALUES
(2, 'Andhra Veg Meals', 'Signature Andhra meals served on banana leaf with rice, dal, and ghee.', 240.00, 1, 'https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=500&auto=format&fit=crop&q=80', 1),
(2, 'Nagarjuna Chicken Biryani', 'Fragrant Basmati rice cooked with chicken and traditional spices.', 320.00, 1, 'https://images.unsplash.com/photo-1633945274405-b6c8069047b0?w=500&auto=format&fit=crop&q=80', 0),
(2, 'Chicken Sholay Kabab', 'Spicy fried chicken pieces tossed with green chilies and curry leaves.', 280.00, 1, 'https://images.unsplash.com/photo-1599487488170-d11ec9c172f0?w=500&auto=format&fit=crop&q=80', 0),
(2, 'Chili Chicken', 'Stir-fried chicken bites cooked with onions, green chilies, and soy sauce.', 290.00, 1, 'https://images.unsplash.com/photo-1525755662778-989d0524087e?w=500&auto=format&fit=crop&q=80', 0),
(2, 'Guntur Chicken Dry', 'Spicy chicken dry dish prepared with roasted Guntur chilies.', 300.00, 1, 'https://images.unsplash.com/photo-1606755962773-d324e0a13086?w=500&auto=format&fit=crop&q=80', 0);

-- Restaurant 3: Empire (Veg & Non-Veg)
INSERT INTO `Menu` (`RestaurantID`, `ItemName`, `Description`, `Price`, `IsAvailable`, `ImagePath`, `IsVeg`) VALUES
(3, 'Empire Special Chicken Kabab', 'Deep-fried chicken pieces marinated in secret spices.', 220.00, 1, 'https://images.unsplash.com/photo-1544025162-d76694265947?w=500&auto=format&fit=crop&q=80', 0),
(3, 'Ghee Rice', 'Basmati rice cooked in pure ghee, flavored with whole spices.', 140.00, 1, 'https://images.unsplash.com/photo-1536304997881-a372c179924b?w=500&auto=format&fit=crop&q=80', 1),
(3, 'Chicken Dum Biryani', 'Empire\'s iconic Dum Biryani served with raita and sherva.', 290.00, 1, 'https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=500&auto=format&fit=crop&q=80', 0),
(3, 'Coin Parotta (1 pc)', 'Flaky, layered multi-layered flatbread.', 25.00, 1, 'https://images.unsplash.com/photo-1565557623262-b51c2513a641?w=500&auto=format&fit=crop&q=80', 1),
(3, 'Butter Chicken', 'Tender chicken pieces cooked in a creamy tomato gravy.', 300.00, 1, 'https://images.unsplash.com/photo-1603894584373-5ac82b2ae398?w=500&auto=format&fit=crop&q=80', 0);

-- Restaurant 4: A2B (Veg)
INSERT INTO `Menu` (`RestaurantID`, `ItemName`, `Description`, `Price`, `IsAvailable`, `ImagePath`, `IsVeg`) VALUES
(4, 'Mini Ghee Giddu Idly', 'Bite-sized button idlies soaked in ghee and gun powder.', 85.00, 1, 'https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=500&auto=format&fit=crop&q=80', 1),
(4, 'Pongal', 'Traditional South Indian dish made of rice and yellow lentil.', 95.00, 1, 'https://images.unsplash.com/photo-1567337710282-00832b415979?w=500&auto=format&fit=crop&q=80', 1),
(4, 'Ghee Roast Dosa', 'Large thin crispy rice crepe roasted with pure ghee.', 110.00, 1, 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=500&auto=format&fit=crop&q=80', 1),
(4, 'Poori Saagu', 'Two puffed deep-fried wheat breads served with potato-onion saagu.', 90.00, 1, 'https://images.unsplash.com/photo-1601050690597-df056fb4ce78?w=500&auto=format&fit=crop&q=80', 1),
(4, 'Filter Coffee', 'Traditional South Indian frothy coffee.', 30.00, 1, 'https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=500&auto=format&fit=crop&q=80', 1);

-- Restaurant 5: Truffles (Veg & Non-Veg)
INSERT INTO `Menu` (`RestaurantID`, `ItemName`, `Description`, `Price`, `IsAvailable`, `ImagePath`, `IsVeg`) VALUES
(5, 'Classic Cheese Burger', 'Juicy grilled burger with cheese slice and fresh veggies.', 170.00, 1, 'https://images.unsplash.com/photo-1550317138-10000687a72b?w=500&auto=format&fit=crop&q=80', 1),
(5, 'Crunchy Chicken Burger', 'Crispy fried chicken breast burger with garlic mayo.', 190.00, 1, 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=500&auto=format&fit=crop&q=80', 0),
(5, 'Truffles Club Sandwich', 'Double decker toasted sandwich packed with chicken and egg.', 180.00, 1, 'https://images.unsplash.com/photo-1528735602780-2552fd46c7af?w=500&auto=format&fit=crop&q=80', 0),
(5, 'Peri Peri French Fries', 'Crispy potato fries tossed in spicy peri peri seasoning.', 120.00, 1, 'https://images.unsplash.com/photo-1573080496219-bb080dd4f877?w=500&auto=format&fit=crop&q=80', 1),
(5, 'Chocolate Truffle Pastry', 'Rich layered dark chocolate cake.', 110.00, 1, 'https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=500&auto=format&fit=crop&q=80', 1);

-- Restaurant 6: Mani's Dum Biryani (Veg & Non-Veg)
INSERT INTO `Menu` (`RestaurantID`, `ItemName`, `Description`, `Price`, `IsAvailable`, `ImagePath`, `IsVeg`) VALUES
(6, 'Mani\'s Egg Biryani', 'Aromatic biryani rice served with boiled eggs and gravy.', 200.00, 1, 'https://images.unsplash.com/photo-1517685352821-92cf88aee5a5?w=500&auto=format&fit=crop&q=80', 0),
(6, 'Chicken Dum Biryani', 'Signature slow-cooked chicken dum biryani.', 280.00, 1, 'https://images.unsplash.com/photo-1633945274405-b6c8069047b0?w=500&auto=format&fit=crop&q=80', 0),
(6, 'Chicken Kabab', 'Deep-fried chicken pieces marinated in local spices.', 180.00, 1, 'https://images.unsplash.com/photo-1599487488170-d11ec9c172f0?w=500&auto=format&fit=crop&q=80', 0),
(6, 'Mirchi Ka Salan', 'Traditional peanut and green chili gravy.', 60.00, 1, 'https://images.unsplash.com/photo-1574653853027-5382a3d23a15?w=500&auto=format&fit=crop&q=80', 1),
(6, 'Raita', 'Cool yogurt dip with onions and cucumber.', 40.00, 1, 'https://images.unsplash.com/photo-1551248429-40975aa4de74?w=500&auto=format&fit=crop&q=80', 1);

-- Restaurant 7: Leon's - Burgers & Salads (Veg & Non-Veg)
INSERT INTO `Menu` (`RestaurantID`, `ItemName`, `Description`, `Price`, `IsAvailable`, `ImagePath`, `IsVeg`) VALUES
(7, 'Leon\'s Classic Lamb Burger', 'Juicy minced lamb burger with caramelized onions.', 250.00, 1, 'https://images.unsplash.com/photo-1553979459-d2229ba7433b?w=500&auto=format&fit=crop&q=80', 0),
(7, 'Crispy Chicken Strips', 'Tender breaded chicken strips fried golden.', 180.00, 1, 'https://images.unsplash.com/photo-1562967914-608f82629a7a?w=500&auto=format&fit=crop&q=80', 0),
(7, 'Caesar Chicken Salad', 'Fresh romaine lettuce with grilled chicken and parmesan.', 170.00, 1, 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=500&auto=format&fit=crop&q=80', 0),
(7, 'Garlic Bread with Cheese', 'Baked baguettes loaded with garlic butter and melted cheese.', 110.00, 1, 'https://images.unsplash.com/photo-1573145959956-e9fae6b8845d?w=500&auto=format&fit=crop&q=80', 1),
(7, 'Leon\'s Special Mojito', 'Mint and lime sparkling summer cooler.', 90.00, 1, 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=500&auto=format&fit=crop&q=80', 1);

-- Restaurant 8: Polar Bear (Veg)
INSERT INTO `Menu` (`RestaurantID`, `ItemName`, `Description`, `Price`, `IsAvailable`, `ImagePath`, `IsVeg`) VALUES
(8, 'Death By Chocolate', 'Decadent chocolate cake, ice cream, fudge, and nuts.', 220.00, 1, 'https://images.unsplash.com/photo-1541599540903-216a46ca1dc0?w=500&auto=format&fit=crop&q=80', 1),
(8, 'Gudbud Special Sundae', 'Three layers of ice cream with jelly, fruits, and dry fruits.', 180.00, 1, 'https://images.unsplash.com/photo-1563805042-7684c019e1cb?w=500&auto=format&fit=crop&q=80', 1),
(8, 'Belgian Chocolate Scoop', 'Rich creamy chocolate scoop.', 80.00, 1, 'https://images.unsplash.com/photo-1580915411954-282cb1b0d780?w=500&auto=format&fit=crop&q=80', 1),
(8, 'Alphonso Mango Ice Cream', 'Made with real seasonal mango pulp.', 90.00, 1, 'https://images.unsplash.com/photo-1497034825429-c343d7c6a68f?w=500&auto=format&fit=crop&q=80', 1),
(8, 'Butterscotch Shake', 'Thick creamy butterscotch milkshake.', 120.00, 1, 'https://images.unsplash.com/photo-1572490122747-3968b75cc699?w=500&auto=format&fit=crop&q=80', 1);

-- Restaurant 9: Meghana Foods (Veg & Non-Veg)
INSERT INTO `Menu` (`RestaurantID`, `ItemName`, `Description`, `Price`, `IsAvailable`, `ImagePath`, `IsVeg`) VALUES
(9, 'Meghana Special Chicken Biryani', 'Signature Andhra style spicy chicken biryani.', 320.00, 1, 'https://images.unsplash.com/photo-1633945274405-b6c8069047b0?w=500&auto=format&fit=crop&q=80', 0),
(9, 'Lolipop Chicken Dry', 'Fried chicken drumsticks tossed in hot sauces.', 280.00, 1, 'https://images.unsplash.com/photo-1606755962773-d324e0a13086?w=500&auto=format&fit=crop&q=80', 0),
(9, 'Guntur Paneer Tikka', 'Spicy grilled cottage cheese cubes.', 250.00, 1, 'https://images.unsplash.com/photo-1565557623262-b51c2513a641?w=500&auto=format&fit=crop&q=80', 1),
(9, 'Meghana Special Biryani Rice', 'Flavored plain biryani rice served with salan.', 150.00, 1, 'https://images.unsplash.com/photo-1536304997881-a372c179924b?w=500&auto=format&fit=crop&q=80', 1),
(9, 'Chilli Paneer', 'Stir-fried cottage cheese cubes in spicy chili garlic sauce.', 230.00, 1, 'https://images.unsplash.com/photo-1603360946369-dc9bb6258143?w=500&auto=format&fit=crop&q=80', 1);

-- Restaurant 10: Pizza Hut (Veg & Non-Veg)
INSERT INTO `Menu` (`RestaurantID`, `ItemName`, `Description`, `Price`, `IsAvailable`, `ImagePath`, `IsVeg`) VALUES
(10, 'Margherita Pizza', 'Classic cheese and tomato pizza.', 190.00, 1, 'https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=500&auto=format&fit=crop&q=80', 1),
(10, 'Chicken Supreme Pizza', 'Loaded with chicken meatballs, tikka, and cheese.', 320.00, 1, 'https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=500&auto=format&fit=crop&q=80', 0),
(10, 'Paneer Veggie Pizza', 'Tandoori paneer cubes, capsicum, onion, and cheese.', 280.00, 1, 'https://images.unsplash.com/photo-1513104890138-7c749659a591?w=500&auto=format&fit=crop&q=80', 1),
(10, 'Spicy Baked Garlic Bread', 'Crispy toasted slices topped with garlic spread.', 120.00, 1, 'https://images.unsplash.com/photo-1573145959956-e9fae6b8845d?w=500&auto=format&fit=crop&q=80', 1),
(10, 'Choco Lava Cake', 'Warm chocolate cake with molten core.', 90.00, 1, 'https://images.unsplash.com/photo-1606313564200-e75d5e30476c?w=500&auto=format&fit=crop&q=80', 1);

-- Update restaurant veg status (1 = Paakashala, 4 = A2B, 8 = Polar Bear)
UPDATE `Restaurant` SET `IsVeg` = TRUE WHERE `RestaurantID` IN (1, 4, 8);
