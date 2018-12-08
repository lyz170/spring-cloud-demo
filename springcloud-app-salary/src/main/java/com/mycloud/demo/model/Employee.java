package com.mycloud.demo.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Employee implements Serializable {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("serial")
	private final static List<Employee> employees = new ArrayList<Employee>() {
		{
			add(new Employee(1L, "Zhang San", new BigDecimal(5000)));
			add(new Employee(2L, "Li Si", new BigDecimal(12000)));
			add(new Employee(3L, "Wang Wu", new BigDecimal(35000)));
		}
	};

	private Long id;

	private String name;

	private BigDecimal salary;

	private BigDecimal tax;

	private BigDecimal salaryAfterTax;

	public Employee(Long id, String name, BigDecimal salary) {
		this.id = id;
		this.name = name;
		this.salary = salary;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public BigDecimal getSalary() {
		return salary;
	}

	public BigDecimal getTax() {
		return tax;
	}

	public void setTax(BigDecimal tax) {
		this.tax = tax;
	}

	public BigDecimal getSalaryAfterTax() {
		return salaryAfterTax;
	}

	public void setSalaryAfterTax(BigDecimal salaryAfterTax) {
		this.salaryAfterTax = salaryAfterTax;
	}

	public static List<Employee> getEmployees() {
		return employees;
	}

	public static Employee getEmployee(Long id) {

		if (id == null) {
			return null;
		}

		for (Employee e : employees) {
			if (id.equals(e.getId())) {
				return e;
			}
		}

		return null;
	}

	@Override
	public String toString() {
		return "Employee [id=" + id + ", name=" + name + ", salary=" + salary + ", tax=" + tax + ", salaryAfterTax="
				+ salaryAfterTax + "]";
	}
}
