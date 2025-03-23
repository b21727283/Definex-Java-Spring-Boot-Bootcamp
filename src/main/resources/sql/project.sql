INSERT INTO projects (department_id, title, description, status, created_by, created_date, update_by, update_date, deleted)
VALUES
    ((SELECT id FROM departments WHERE department_name = 'Engineering'), 'Project Alpha', 'Description for Project Alpha', 'IN_PROGRESS', 'system', NOW(), 'system', NOW(), false),
    ((SELECT id FROM departments WHERE department_name = 'Marketing'), 'Project Beta', 'Description for Project Beta', 'COMPLETED', 'system', NOW(), 'system', NOW(), false),
    ((SELECT id FROM departments WHERE department_name = 'Sales'), 'Project Gamma', 'Description for Project Gamma', 'CANCELLED', 'system', NOW(), 'system', NOW(), false),
    ((SELECT id FROM departments WHERE department_name = 'HR'), 'Project Delta', 'Description for Project Delta', 'IN_PROGRESS', 'system', NOW(), 'system', NOW(), false),
    ((SELECT id FROM departments WHERE department_name = 'Finance'), 'Project Epsilon', 'Description for Project Epsilon', 'CANCELLED', 'system', NOW(), 'system', NOW(), false);