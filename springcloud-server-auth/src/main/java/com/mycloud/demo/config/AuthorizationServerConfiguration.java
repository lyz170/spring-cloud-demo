package com.mycloud.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;

@Configuration
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    /*
     * 用来配置令牌端点(Token Endpoint)的安全约束<br>
     * @see org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter#configure(org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer)
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        // 这个如果配置支持allowFormAuthenticationForClients的，且url中有client_id和client_secret的会走ClientCredentialsTokenEndpointFilter来保护
        // 如果没有支持allowFormAuthenticationForClients或者有支持但是url中没有client_id和client_secret的，走basic认证保护
        // [IMPORTANT] 这里如果不设置，client里的clientAuthenticationScheme应该设置为header，反之设置为form
        security.allowFormAuthenticationForClients();
    }

    /* 用来配置客户端详情服务（ClientDetailsService），客户端详情信息在这里进行初始化<br>
     * @see org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter#configure(org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer)
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

        PasswordEncoder encoder = new BCryptPasswordEncoder();
        clients.inMemory()
                // -------------- Grant Type: authorization_code --------------
                .withClient("client1") // [required] Define a client: 'client1'
                .secret("{bcrypt}" + encoder.encode("client1-secret")) // Password of 'client1'
                .authorizedGrantTypes("authorization_code", "refresh_token") // [required] grant types
                .accessTokenValiditySeconds(300) // token valid seconds
                .scopes("scoap1") // [required] 数据访问范围
                // .redirectUris("http://localhost:9231/app-client/login/myapp")// 认证成功重定向URL
                // -------------- Grant Type: password --------------
                .and().withClient("client2") // [required] Define a client: 'client2'
                .secret("{bcrypt}" + encoder.encode("client2-secret")) // Password of 'client2'
                .authorizedGrantTypes("password", "refresh_token") // [required] grant types
                .accessTokenValiditySeconds(300) // token valid seconds
                .scopes("scoap2"); // [required] 数据访问范围
    }

    /* 定义授权和令牌端点以及令牌服务<br>
     * @see org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter#configure(org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer)
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager) // 使用Spring提供的AuthenticationManager开启密码授权
                .userDetailsService(userDetailsService) // 注入一个 UserDetailsService，那么刷新令牌授权将包含对用户详细信息的检查，以确保该帐户仍然是活动的
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST); // 默认只支持GET
    }
}
