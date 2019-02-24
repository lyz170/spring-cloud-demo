package com.mycloud.demo.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity(name = "employee")
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(length = 16, nullable = false)
    private String employeeId;

    @Column(length = 16, nullable = false)
    private String employeeName;

    @Column(length = 32, nullable = false)
    private String employeeEmail;

    @Column(length = 16, nullable = false)
    private String salary;

    @Transient
    private String tax;

    @Transient
    private String salaryAfterTax;

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeEmail() {
        return employeeEmail;
    }

    public void setEmployeeEmail(String employeeEmail) {
        this.employeeEmail = employeeEmail;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getSalaryAfterTax() {
        return salaryAfterTax;
    }

    public void setSalaryAfterTax(String salaryAfterTax) {
        this.salaryAfterTax = salaryAfterTax;
    }

    @Override
    public String toString() {
        return "Employee [employeeId=" + employeeId + ", employeeName=" + employeeName + ", employeeEmail="
                + employeeEmail + ", salary=" + salary + ", tax=" + tax + ", salaryAfterTax=" + salaryAfterTax + "]";
    }
}
