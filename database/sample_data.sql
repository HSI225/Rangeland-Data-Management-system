-- Optional demonstration data for a clean development database.
USE eiseb_rangeland;

INSERT INTO rangeland_camp
    (camp_name, location, size_hectares, grazing_capacity, camp_type, water_source)
VALUES
    ('North Camp', 'Northern Block', 420.00, 80, 'Grassland', 'Borehole'),
    ('River Block', 'Eastern Boundary', 310.50, 55, 'Savanna', 'River'),
    ('Holding Camp', 'Central Kraal', 85.00, 120, 'Mixed', 'Trough')
ON DUPLICATE KEY UPDATE location = VALUES(location);

INSERT INTO grazing_rotation
    (camp_id, livestock_type, livestock_count, start_date, end_date, notes)
SELECT camp_id, 'Cattle', 45, CURDATE() - INTERVAL 7 DAY, CURDATE() + INTERVAL 14 DAY, 'Demonstration rotation'
FROM rangeland_camp WHERE camp_name = 'North Camp';

INSERT INTO vegetation_monitoring
    (camp_id, monitor_date, vegetation_cover, grass_height_cm, recorded_by, notes)
SELECT camp_id, CURDATE(), 'Good', 18.5, 'Field Staff', 'Demonstration record'
FROM rangeland_camp WHERE camp_name = 'North Camp';

INSERT INTO water_source
    (camp_id, source_type, capacity_liters, last_inspected, condition_status)
SELECT camp_id, 'Borehole', 12000.00, CURDATE(), 'Good'
FROM rangeland_camp WHERE camp_name = 'North Camp';
