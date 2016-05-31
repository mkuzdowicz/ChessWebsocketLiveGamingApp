package com.kuzdowicz.livegaming.chess.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SpringSecurityConf extends WebSecurityConfigurerAdapter {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	public void configure(WebSecurity webSecurity) throws Exception {

		webSecurity.ignoring().antMatchers("/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http//
				.formLogin()//
				.loginPage("/login")//
				.defaultSuccessUrl("/user/tutorials-to-do")//
				.and()//
				.authorizeRequests()//
				.antMatchers("/user/**")//
				.hasAnyRole("USER", "ADMIN")//
				.antMatchers("/admin/**").hasRole("ADMIN")//
				.anyRequest().permitAll();
	}

}