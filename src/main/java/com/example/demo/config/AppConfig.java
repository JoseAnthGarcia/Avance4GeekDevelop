package com.example.demo.config;

import com.example.demo.Filters.CustomFilter;
import com.example.demo.repositories.RestauranteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Autowired
    RestauranteRepository restauranteRepository;


    @Bean
    public FilterRegistrationBean<CustomFilter> filterRegistrationBean(){
        FilterRegistrationBean<CustomFilter> registrationBean = new FilterRegistrationBean();
        CustomFilter customFilter = new CustomFilter(restauranteRepository);
        registrationBean.setFilter(customFilter);
        registrationBean.addUrlPatterns("/plato/*","/extra/*","/restaurante/*","/cupon/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
