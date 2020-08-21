package com.daniel.demo.config;

import com.daniel.demo.repository.ProductRepository;
import com.daniel.demo.service.ProductService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class ServiceConfig {

    //productService元件，如果有很多元件也可以利用這個方式一次產生
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ProductService productService(ProductRepository repository) {
        return new ProductService(repository);
    }
}

