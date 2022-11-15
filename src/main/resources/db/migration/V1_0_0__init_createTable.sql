
Create Table employee_db(
    id bigserial not null ,
    department varchar(255) ,
    email varchar(255) not null ,
    first_name varchar(255) not null ,
    mobile_number varchar(255),
    last_name varchar(255),
    telephone_number varchar(255),
    title varchar(255),
    username varchar(255) not null ,
    dn varchar(255) not null   ,
    primary key (id)
);