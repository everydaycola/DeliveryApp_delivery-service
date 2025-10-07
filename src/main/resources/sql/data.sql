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
INSERT INTO deliveries (id, order_id, courier_id, status, start_time, end_time, payout)
VALUES ('770e8400-e29b-41d4-a716-446655440001', '660e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440002',  'DELIVERED', '2025-10-01 12:00:00', '2025-10-01 12:12:56', 6.6);

-- Delivery 2 - successful delivery by courier 2
INSERT INTO deliveries (id, order_id, courier_id, status, start_time, end_time, payout)
VALUES ('770e8400-e29b-41d4-a716-446655440002', '660e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440002', 'DELIVERED', '2025-10-03 12:00:00', '2025-10-03 12:20:56', 9.0);

-- Delivery 3 - successful delivery by courier 1
INSERT INTO deliveries (id, order_id, courier_id, status, start_time, end_time, payout)
VALUES ('770e8400-e29b-41d4-a716-446655440003', '660e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440001', 'DELIVERED', '2025-10-05 12:00:00', '2025-10-05 12:01:56', 4.5);

-- Delivery 4 - active delivery by courier 2 (not successful yet)
INSERT INTO deliveries (id, order_id, courier_id, status, start_time, payout)
VALUES ('770e8400-e29b-41d4-a716-446655440004', '660e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440002', 'READY_FOR_PICKUP', '2025-10-08 12:00:00', 0);

-- Delivery 5 - unclaimed delivery (courier_id is NULL)
INSERT INTO deliveries (id, order_id, courier_id, status, payout)
VALUES ('770e8400-e29b-41d4-a716-446655440005', '660e8400-e29b-41d4-a716-446655440005', NULL, 'UNCLAIMED', 0);

-- Delivery 6 - unclaimed delivery (courier_id is NULL)
INSERT INTO deliveries (id, order_id, courier_id, status, payout)
VALUES ('770e8400-e29b-41d4-a716-446655440006', '660e8400-e29b-41d4-a716-446655440006', NULL, 'UNCLAIMED', 0);

-- Enable constraints after seeding
SET session_replication_role = 'origin';