DROP TABLE IF EXISTS employee;

CREATE TABLE employee (
  employee_id    VARCHAR(16) PRIMARY KEY NOT NULL,
  employee_name  VARCHAR(16) NOT NULL,
  employee_email VARCHAR(32) NOT NULL,
  salary         VARCHAR(16) NOT NULL
);