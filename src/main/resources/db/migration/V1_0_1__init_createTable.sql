ALTER table employee_db Add manager_id bigint ;
ALTER TABLE employee_db
    ADD CONSTRAINT fk_manager_id FOREIGN KEY (manager_id) REFERENCES employee_db (id);