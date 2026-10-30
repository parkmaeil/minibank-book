package com.itscoding.minibank.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;

//@Component                // 3.2절 확인이 끝나면 주석 처리해 둔다
public class BeanPrinter implements CommandLineRunner {

    // 생성자를 통한 DI (Constructor Injection)
    private final ApplicationContext context;
    public BeanPrinter(ApplicationContext context) { this.context = context; }

    @Override
    public void run(String... args) {
        // Spring IoC 컨테이너에 등록된 Bean 중 'account'를 포함하는 것들만 출력
        System.out.println("--- 등록된 Account 관련 Bean 목록 ---");
        Arrays.stream(context.getBeanDefinitionNames())
              .filter(name -> name.toLowerCase().contains("account"))
              .forEach(System.out::println);
    }
}
