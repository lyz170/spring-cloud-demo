package com.mycloud.demo.call;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.mycloud.demo.entity.Employee;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

/**
 * 使用服务发现来查找服务<br>
 * ----------------------------------------------<br>
 * (1) 使用Spring DiscoveryClient：使用DiscoveryClient可以查询通过Ribbon注册的所有服务及对应的URL<br>
 * 从Spring Cloud Edgware开始，@EnableDiscoveryClient或@EnableEurekaClient可省略。只需加上相关依赖，并进行相应配置，即可将微服务注册到服务发现组件上。<br>
 * SpringCLoud中的Discovery Service有多种实现，比如：eureka, consul, zookeeper<br>
 * - @EnableDiscoveryClient 注解是基于spring-cloud-commons依赖，并且在classpath中实现；<br>
 * - @EnableEurekaClient 注解是基于spring-cloud-netflix依赖，只能为eureka作用； <br>
 * 如果你的classpath中添加了eureka，则它们的作用是一样的。<br>
 * 
 * <pre>
 * &#64;Autowired
 * private LoadBalancerClient loadBalancer;
 * 
 * public void doStuff() {
 *     ServiceInstance instance = loadBalancer.choose("service-id");
 *     URI storesUri = URI.create(String.format("http://%s:%s", instance.getHost(), instance.getPort()));
 *     RestTemplate restTemplate = new RestTemplate();
 *     restTemplate.getForObject("storesUri" + "/xxx/xxx", String.class);
 * }
 * </pre>
 * 
 * ----------------------------------------------<br>
 * (2) [CURRENT] 使用带有Ribbon功能的Spring RestTemplate调用服务<br>
 * 
 * <pre>
 * &#64;LoadBalanced
 * &#64;Bean
 * public RestTemplate loadbalancedRestTemplate() {
 *     return new RestTemplate();
 * }
 * 
 * public String getFirstProduct() {
 *     return this.restTemplate.getForObject("https://service-id/xxx/xxx", String.class);
 * }
 * </pre>
 * 
 * ----------------------------------------------<br>
 * (3) 使用Netflix Feign客户端调用服务<br>
 * 
 * <pre>
 * &#64;SpringBootApplication
 * &#64;EnableFeignClients
 * public class Application {
 * }
 * 
 * &#64;FeignClient("service-id")
 * public interface StoreClient {
 *     &#64;RequestMapping(method = RequestMethod.GET, value = "/xxx")
 *     List<Store> getStores();
 * 
 *     &#64;RequestMapping(method = RequestMethod.POST, value = "/xxx/{xxx}", consumes = "application/json")
 *     Store update(@PathVariable("storeId") Long storeId, Store store);
 * }
 * </pre>
 * 
 * ----------------------------------------------<br>
 */
@Service
@DefaultProperties(commandProperties = {
        @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000") })
public class CallAppTaxCalcService {

    private static final Logger logger = LoggerFactory.getLogger(CallAppTaxCalcService.class);

    @Autowired
    private RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "callFallbackMethod1")
    public void fillTax(List<Employee> params) {
        fillTaxInner(params);
    }

    @HystrixCommand(fallbackMethod = "callFallbackMethod2")
    public void fillTax(Employee param) {
        List<Employee> params = new ArrayList<>();
        params.add(param);
        fillTaxInner(params);
    }

    private void callFallbackMethod1(List<Employee> params) {
        params.forEach(p -> {
            p.setTax("-");
            p.setSalaryAfterTax("-");
        });
    }

    private void callFallbackMethod2(Employee param) {
        param.setTax("-");
        param.setSalaryAfterTax("-");
    }

    private void fillTaxInner(List<Employee> params) {

        if (CollectionUtils.isNotEmpty(params)) {

            List<String> paramList = params.stream().map(p -> p.getSalary().toString()).collect(Collectors.toList());

            ResponseEntity<String> responseEntity = restTemplate
                    .postForEntity("http://app-tax-calc/app-tax-calc/tax-calc/all", paramList, String.class);
            String body = responseEntity.getBody();
            logger.info("The Response body is: " + body);

            JSONObject jsonObj = (JSONObject) JSONValue.parse(body);
            String strs = jsonObj.get("status").toString();
            JSONArray jsonArr = null;
            if ("200".equals(strs)) {
                String content = jsonObj.get("content").toString();
                if (StringUtils.isNotEmpty(content)) {
                    jsonArr = (JSONArray) JSONValue.parse(content);
                }
            }

            if (jsonArr != null && jsonArr.size() == params.size()) {
                for (int index = 0; index < params.size(); index++) {
                    BigDecimal total = new BigDecimal(params.get(index).getSalary());
                    BigDecimal tax = new BigDecimal(jsonArr.get(index).toString());
                    params.get(index).setTax(tax.toString());
                    params.get(index).setSalaryAfterTax(total.subtract(tax).toString());
                }
            }
        }
    }
}
