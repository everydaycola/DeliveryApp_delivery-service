-- Disable constraints for seeding
SET session_replication_role = 'replica';

-- Seed Couriers
-- Courier 1 - no active delivery (current_delivery_id is NULL)
INSERT INTO couriers (id, current_delivery_id)
VALUES ('550e8400-e29b-41d4-a716-446655440001', NULL);

-- Courier 2 - has active delivery (Delivery4Id)
INSERT INTO couriers (id, current_delivery_id)
VALUES ('550e8400-e29b-41d4-a716-446655440002', '770e8400-e29b-41d4-a716-446655440004');

-- Seed Deliveries
-- Delivery 1 - successful delivery by courier 2
INSERT INTO deliveries (id, order_id, courier_id, is_successful, start_time, end_time, payout)
VALUES ('770e8400-e29b-41d4-a716-446655440001', '660e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440002', true, to_date('2025-10-01 12:00:00', 'YYYY-MM-DD HH:mm:ss'), to_date('2025-10-01 12:34:56', 'YYYY-MM-DD HH:mm:ss'), 10.30);

-- Delivery 2 - successful delivery by courier 2
INSERT INTO deliveries (id, order_id, courier_id, is_successful, start_time, end_time, payout)
VALUES ('770e8400-e29b-41d4-a716-446655440002', '660e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440002', true, to_date('2025-10-03 12:00:00', 'YYYY-MM-DD HH:mm:ss'), to_date('2025-10-03 12:34:56', 'YYYY-MM-DD HH:mm:ss'), 23.43);

-- Delivery 3 - successful delivery by courier 1
INSERT INTO deliveries (id, order_id, courier_id, is_successful, start_time, end_time, payout)
VALUES ('770e8400-e29b-41d4-a716-446655440003', '660e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440001', true, to_date('2025-10-05 12:00:00', 'YYYY-MM-DD HH:mm:ss'), to_date('2025-10-05 12:34:56', 'YYYY-MM-DD HH:mm:ss'), 12.65);

-- Delivery 4 - active delivery by courier 2 (not successful yet)
INSERT INTO deliveries (id, order_id, courier_id, is_successful, start_time)
VALUES ('770e8400-e29b-41d4-a716-446655440004', '660e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440002', false, to_date('2025-10-08 12:00:00', 'YYYY-MM-DD HH:mm:ss'));

-- Delivery 5 - unclaimed delivery (courier_id is NULL)
INSERT INTO deliveries (id, order_id, courier_id, is_successful)
VALUES ('770e8400-e29b-41d4-a716-446655440005', '660e8400-e29b-41d4-a716-446655440005', NULL, false);

-- Delivery 6 - unclaimed delivery (courier_id is NULL)
INSERT INTO deliveries (id, order_id, courier_id, is_successful)
VALUES ('770e8400-e29b-41d4-a716-446655440006', '660e8400-e29b-41d4-a716-446655440006', NULL, false);

-- Enable constraints for seeding
SET session_replication_role = 'origin';