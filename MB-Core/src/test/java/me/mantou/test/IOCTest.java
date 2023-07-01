package me.mantou.test;

import me.mantou.test.config.TestConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class IOCTest {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(
                IOCTest.class.getPackageName()
        );

        System.out.println(applicationContext.getBean(TestConfig.class));
    }
}
