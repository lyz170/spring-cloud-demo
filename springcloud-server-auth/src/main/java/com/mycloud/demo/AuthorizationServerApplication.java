package com.mycloud.demo;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * -------------------------------------------------<br>
 * It's an OAuth2 Authorization Server<br>
 * -------------------------------------------------<br>
 * OAuth 2.0 提供方实现<br>
 * OAuth 2.0 的提供方实际涵盖两个角色，即认证服务 (Authorization Service) 和资源服务 (Resource Service)，有时候它们会在同一个应用程序中实现。<br>
 * 使用 Spring Security OAuth 的时候你可以选择把把它们分别放在两个应用程序中，也可以选择建立使用同一个认证服务的多个资源服务。<br>
 * 对令牌的请求由Spring MVC控制器终端进行处理，而标准的Spring security请求过滤器会处理对受保护资源的访问。Spring以Security过滤器链需要以下各端来实现OAuth 2.0认证服务<br>
 * AuthorizationEndpoint服务于认证请求。默认 URL： /oauth/authorize<br>
 * TokenEndpoint服务于访问令牌的请求。默认 URL： /oauth/token<br>
 * -------------------------------------------------<br>
 * endpoint的认证<br>
 * /oauth/authorize:这个需要保护用户账号密码认证保护<br>
 * /oauth/token:如果配置有allowFormAuthenticationForClients且url中有client_id&client_secret的用ClientCredentialsTokenEndpointFilter<br>
 * 如果没有支持allowFormAuthenticationForClients或者有支持但是url中没有client_id和client_secret的，走basic认证保护<br>
 * /oauth/check_token:这个走basic认证保护<br>
 * /oauth/confirm_access:这个需要认证保护，否则报500<br>
 * /oauth/error:这个可以不用认证保护<br>
 * -------------------------------------------------<br>
 * [第三方授权模型]<br>
 * [单点登录模型]<br>
 * UserInfoTokenServices对应于[security.oauth2.resource.user-info-uri]，通过/user，Authorization Server传回User&Role。<br>
 * Resource Server可以通过User&Role来控制访问权限。可以看到UserInfoTokenServices的OAuth2Request并没拿到resourceIds,scope,<br>
 * 所以client中resourceIds, scope的设定是失效的。<br>
 * -------------------------------------------------<br>
 * URLs:<br>
 * http://localhost:9141/server-auth/user?access_token=xxx<br>
 * -- [authorization_code] --<br>
 * http://localhost:9141/server-auth/oauth/authorize?response_type=code&client_id=client1&scope=name&state=dummy&redirect_uri=https://www.google.com<br>
 * http://localhost:9141/server-auth/oauth/authorize?response_type=code&client_id=client1&state=dummy&redirect_uri=https://www.google.com<br>
 * (Response: https://www.google.com/?code=Bor88Q&state=dummy)<br>
 * http://localhost:9141/server-auth/oauth/token?client_id=client1&grant_type=authorization_code&code=S704qA&redirect_uri=https://www.google.com<br>
 * (Need to add header ["Authorization", "Basic client1:client1-secret"])<br>
 * -- [password] --<br>
 * http://localhost:9141/server-auth/oauth/token?client_id=client2&client_secret=client2-secret&grant_type=password&username=user1&password=password1<br>
 * (Need to add header ["Authorization", "Basic client1:client1-secret"])<br>
 * -------------------------------------------------<br>
 */
@SpringBootApplication
@RestController
@EnableAuthorizationServer // Enable OAuth2 Authorization Server
@EnableResourceServer // // Enable OAuth2 Resource Server (only for '/user' resource)
// ResourceServerConfigurerAdapter.configure()的order默认为3[ResourceServerConfiguration]，所以不要在其他的WebSecurityConfigurerAdapters中用这个order
// [https://docs.spring.io/spring-security/oauth/apidocs/org/springframework/security/oauth2/config/annotation/web/configuration/EnableResourceServer.html]
public class AuthorizationServerApplication implements CommandLineRunner {

    @Value("${app.property.env}")
    public String ENVIRONMENT;

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationServerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(AuthorizationServerApplication.class, args);
        logger.info("<<<<<<<<<< Application[springcloud-server-auth] Started >>>>>>>>>>");
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info(String.format("<<<<<<<<<< Environment : %s >>>>>>>>>>", ENVIRONMENT.toUpperCase()));
    }

    /**
     * 映射到/user, Resource Server会调用该端点<br>
     * Resource Server中的@EnableResourceServer会强制执行一个过滤器，<br>
     * 该拦截器会用传入的token回调[security.oauth2.resource.userInfoUri]中定义的URI来查看令牌是否有效。<br>
     * 此外，该URI还会从Authorization Server传回一个Map，包含Principal and GrantedAuthority信息。<br>
     * 这个信息是必须的。详细请看：UserInfoTokenServices.loadAuthentication<br>
     * 
     * @param user
     * @return
     */
    @RequestMapping(value = "/user", produces = "application/json")
    public Map<String, Object> user(OAuth2Authentication user, @RequestParam(required = false) String client) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("user", "myapp".equals(client) ? user.getUserAuthentication().getName()
                : user.getUserAuthentication().getPrincipal());
        userInfo.put("authorities", AuthorityUtils.authorityListToSet(user.getUserAuthentication().getAuthorities()));
        return userInfo;
    }
}
