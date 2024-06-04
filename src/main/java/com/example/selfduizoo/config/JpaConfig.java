package com.example.selfduizoo.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaConfig {
    @Bean
    public JPAQueryFactory jpaQueryFactory(
            EntityManager entityManager
    ){
        return new JPAQueryFactory(entityManager);
    }
}
