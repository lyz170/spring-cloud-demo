package com.mycloud.demo.call;

import com.mycloud.demo.entity.Employee;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CallAppTaxCalcService {

    private static final Logger logger = LoggerFactory.getLogger(CallAppTaxCalcService.class);

    /**
     * 客户端负载均衡<br>
     * Using the Ribbon. Ribbon is a client-side load balancer that gives you a lot of control over the behavior of HTTP
     * and TCP clients.
     */
    @Autowired
    private LoadBalancerClient loadBalancer;

    private static final RestTemplate restTemplate = new RestTemplate();

    public void fillTax(List<Employee> params) {
        fillTaxInner(params);
    }

    public void fillTax(Employee param) {
        List<Employee> params = new ArrayList<>();
        params.add(param);
        fillTaxInner(params);
    }

    private void fillTaxInner(List<Employee> params) {

        if (CollectionUtils.isNotEmpty(params)) {

            List<String> paramList = params.stream().map(p -> p.getSalary().toString()).collect(Collectors.toList());

            ServiceInstance instance = loadBalancer.choose("app-tax-calc");
            String uri = String.format("http://%s:%s", instance.getHost(), instance.getPort());
            logger.info("URI is: " + uri);

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(uri + "/app-tax-calc/tax-calc/all",
                    paramList, String.class);
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
