package com.mycloud.demo.service;

import com.mycloud.demo.config.AppException;
import com.mycloud.demo.entity.Employee;
import com.mycloud.demo.repository.EmployeeRepository;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * 使用 [@HystrixCommand] 注解表示该方法由Hystrix断路器进行管理。<br>
     * 当Spring框架检测到该注解时，将生成一个动态代理包装该方法，并用过专门用于处理远程调用的线程池来管理对该方法的所有调用。<br>
     */
    @HystrixCommand
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    @HystrixCommand
    public Employee findById(String id) {
        Optional<Employee> optResult = employeeRepository.findById(id);
        if (!optResult.isPresent()) {
            throw new AppException(String.format("Can not find the employee which id = %s.", id));
        }
        return optResult.get();
    }
}
