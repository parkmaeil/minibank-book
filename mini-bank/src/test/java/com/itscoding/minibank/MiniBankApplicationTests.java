package com.itscoding.minibank;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfiguration.class)   // 10.6절부터 실제 MySQL 컨테이너로 실행
class MiniBankApplicationTests {

	@Test
	void contextLoads() {
	}

}
