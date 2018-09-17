package com.ainon.check_account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


//@Configuration
//@EnableAutoConfiguration
//@ComponentScan
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class) 
public class CheckAccountApplication {

	public static void main(String[] args) {
		SpringApplication.run(CheckAccountApplication.class, args);
	}
}
