ALTER table Employee Add manager_id bigint ;
ALTER TABLE Employee
    ADD CONSTRAINT fk_manager_id FOREIGN KEY (manager_id) REFERENCES Employee (id);