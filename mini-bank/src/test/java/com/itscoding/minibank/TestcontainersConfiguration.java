package com.itscoding.minibank;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.mysql.MySQLContainer;

// 10.6절 — 테스트마다 실제 MySQL 컨테이너를 띄워 spring.datasource.*에 자동 연결한다.
// @Import(TestcontainersConfiguration.class)를 붙인 테스트는 로컬 MySQL(docker compose) 없이도,
// GitHub Actions(11.5절)에서도 그대로 실행된다. (Docker가 실행 중이어야 한다)
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    MySQLContainer mysqlContainer() {
        return new MySQLContainer("mysql:8.4.0");
    }
}
