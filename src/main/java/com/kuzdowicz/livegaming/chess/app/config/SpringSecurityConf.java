package com.kuzdowicz.livegaming.chess.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SpringSecurityConf extends WebSecurityConfigurerAdapter {
	
	
	@Override
	public void configure(WebSecurity webSecurity) throws Exception {

		webSecurity.ignoring().antMatchers("/resources/**");
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
