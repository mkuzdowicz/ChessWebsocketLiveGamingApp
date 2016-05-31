package com.kuzdowicz.livegaming.chess.app.config;

import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kuzdowicz.livegaming.chess.app.filters.AppSiteMeshFilter;

@Configuration
public class ServletInit extends SpringBootServletInitializer {

	@Bean
	public FilterRegistrationBean siteMeshFilter() {
		FilterRegistrationBean filter = new FilterRegistrationBean();
		filter.setFilter(new AppSiteMeshFilter());
		return filter;
	}

}
