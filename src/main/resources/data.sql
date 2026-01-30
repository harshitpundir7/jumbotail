-- =====================================================
-- Sample Data for Shipping Charge Estimator
-- =====================================================
-- Note: Tables are created by Hibernate (ddl-auto: create-drop)
-- This file only contains INSERT statements

-- =====================================================
-- WAREHOUSES (5 strategic locations across India)
-- =====================================================
INSERT INTO warehouses (id, warehouse_code, name, location_latitude, location_longitude, address, pincode, city, state, capacity_sq_ft, utilization_percent, manager_name, contact_phone, is_active, created_at, updated_at) VALUES
(1, 'BLR_WH_01', 'Bangalore Central Warehouse', 12.9716, 77.5946, 'Plot 45, KIADB Industrial Area, Peenya', '560058', 'Bangalore', 'Karnataka', 50000, 65, 'Rajesh Kumar', '9845012345', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'MUM_WH_01', 'Mumbai Distribution Center', 19.0760, 72.8777, 'Unit 12, Bhiwandi Logistics Park, Thane', '421302', 'Mumbai', 'Maharashtra', 75000, 70, 'Priya Sharma', '9820123456', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'DEL_WH_01', 'Delhi NCR Warehouse', 28.7041, 77.1025, 'Warehouse 7, Kundli Industrial Area', '131028', 'Delhi', 'Delhi', 60000, 55, 'Amit Singh', '9811234567', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'CHN_WH_01', 'Chennai Logistics Hub', 13.0827, 80.2707, 'Block C, Ambattur Industrial Estate', '600058', 'Chennai', 'Tamil Nadu', 40000, 45, 'Lakshmi Narayanan', '9841234567', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'KOL_WH_01', 'Kolkata Eastern Warehouse', 22.5726, 88.3639, 'Sector 5, Salt Lake Technology Park', '700091', 'Kolkata', 'West Bengal', 35000, 40, 'Debashis Roy', '9830123456', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =====================================================
-- SELLERS (5 sellers across different locations)
-- =====================================================
INSERT INTO sellers (id, seller_id, company_name, contact_name, phone_number, email, location_latitude, location_longitude, address, pincode, city, state, gst_number, pan_number, is_active, created_at, updated_at) VALUES
(1, 'SELLER-001', 'Nestle India Pvt Ltd', 'Vikram Mehta', '9876543210', 'vikram@nestle.in', 12.9165, 77.6101, '123 Food Park Road, Whitefield', '560066', 'Bangalore', 'Karnataka', '29AABCN1234A1Z5', 'AABCN1234A', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'SELLER-002', 'Premium Rice Traders', 'Suresh Patel', '9898123456', 'suresh@premiumrice.com', 17.3850, 78.4867, '45 Grain Market, Secunderabad', '500003', 'Hyderabad', 'Telangana', '36AABCP5678B2Z6', 'AABCP5678B', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'SELLER-003', 'Sweet Sugar Mills', 'Ramesh Agarwal', '9871234567', 'ramesh@sweetsugar.in', 26.9124, 75.7873, '78 Industrial Area, Jaipur', '302013', 'Jaipur', 'Rajasthan', '08AABCS9012C3Z7', 'AABCS9012C', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'SELLER-004', 'Coastal Spices Co', 'Maria DSouza', '9845678901', 'maria@coastalspices.in', 15.2993, 74.1240, 'Spice Trade Center, Margao', '403601', 'Goa', 'Goa', '30AABCC3456D4Z8', 'AABCC3456D', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'SELLER-005', 'Punjab Flour Mills', 'Harpreet Singh', '9815678901', 'harpreet@punjabflour.com', 30.7333, 76.7794, 'Mill Road, Ludhiana', '141001', 'Ludhiana', 'Punjab', '03AABCP7890E5Z9', 'AABCP7890E', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =====================================================
-- CUSTOMERS (Kirana Stores across India)
-- =====================================================
INSERT INTO customers (id, customer_id, store_name, owner_name, phone_number, email, location_latitude, location_longitude, address, pincode, city, state, gst_number, is_active, created_at, updated_at) VALUES
(1, 'CUST-001', 'Shree Kirana Store', 'Mohan Lal', '9847123456', 'mohan@shreekirana.in', 12.9352, 77.6245, '45 Gandhi Nagar, Indiranagar', '560038', 'Bangalore', 'Karnataka', '29AABCS1111A1Z1', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'CUST-002', 'Andheri Mini Mart', 'Sanjay Deshmukh', '9820456789', 'sanjay@andherimart.com', 19.1136, 72.8697, '23 Link Road, Andheri West', '400053', 'Mumbai', 'Maharashtra', '27AABCA2222B2Z2', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'CUST-003', 'Rajouri Provision Store', 'Ravi Gupta', '9811567890', 'ravi@rajouristore.in', 28.6419, 77.1219, '56 Market Road, Rajouri Garden', '110027', 'Delhi', 'Delhi', '07AABCR3333C3Z3', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'CUST-004', 'T Nagar Groceries', 'Venkat Raman', '9841678901', 'venkat@tnagargroceries.com', 13.0418, 80.2341, '12 Usman Road, T Nagar', '600017', 'Chennai', 'Tamil Nadu', '33AABCT4444D4Z4', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'CUST-005', 'Park Street Supermarket', 'Anindya Ghosh', '9830789012', 'anindya@parkstreetsuper.in', 22.5519, 88.3511, '89 Park Street', '700016', 'Kolkata', 'West Bengal', '19AABCP5555E5Z5', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =====================================================
-- PRODUCTS (Various products with different weights)
-- =====================================================
INSERT INTO products (id, product_id, name, description, category, selling_price, mrp, weight_in_kg, dimension_length_cm, dimension_width_cm, dimension_height_cm, seller_id, is_active, stock_quantity, created_at, updated_at) VALUES
(1, 'PROD-MAGGIE-500', 'Maggi Noodles 500g Pack', 'Instant noodles family pack - 8 pieces', 'Instant Food', 80.00, 90.00, 0.5, 20.0, 15.0, 10.0, 1, true, 1000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'PROD-MAGGIE-BULK', 'Maggi Noodles Bulk Box', 'Carton of 48 packets', 'Instant Food', 450.00, 500.00, 5.0, 40.0, 30.0, 25.0, 1, true, 500, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'PROD-RICE-10KG', 'Premium Basmati Rice 10Kg', 'Long grain aged basmati rice', 'Rice & Grains', 650.00, 750.00, 10.0, 45.0, 30.0, 15.0, 2, true, 300, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'PROD-RICE-25KG', 'Basmati Rice Bulk 25Kg', 'Restaurant quality basmati', 'Rice & Grains', 1500.00, 1800.00, 25.0, 60.0, 40.0, 20.0, 2, true, 200, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'PROD-SUGAR-5KG', 'Refined Sugar 5Kg Pack', 'Premium refined white sugar', 'Sugar & Sweeteners', 250.00, 280.00, 5.0, 25.0, 20.0, 15.0, 3, true, 400, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'PROD-SUGAR-25KG', 'Sugar Bulk 25Kg Sack', 'Industrial grade refined sugar', 'Sugar & Sweeteners', 1100.00, 1250.00, 25.0, 70.0, 50.0, 20.0, 3, true, 150, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 'PROD-TURMERIC-1KG', 'Organic Turmeric Powder 1Kg', 'Pure Lakadong turmeric, high curcumin', 'Spices', 350.00, 400.00, 1.0, 15.0, 10.0, 20.0, 4, true, 600, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 'PROD-SPICE-MIX', 'Garam Masala Mix 500g', 'Traditional blend of 13 spices', 'Spices', 180.00, 200.00, 0.5, 12.0, 8.0, 15.0, 4, true, 800, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, 'PROD-ATTA-10KG', 'Whole Wheat Atta 10Kg', 'Stone ground chakki atta', 'Flour', 400.00, 450.00, 10.0, 40.0, 30.0, 12.0, 5, true, 350, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 'PROD-MAIDA-25KG', 'Refined Flour Bulk 25Kg', 'All-purpose flour for bakeries', 'Flour', 800.00, 900.00, 25.0, 55.0, 40.0, 18.0, 5, true, 180, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
