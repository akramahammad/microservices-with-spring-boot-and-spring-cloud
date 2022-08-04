package com.microservices.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.factory.TokenRelayGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableEurekaClient
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}
	
	
	@Autowired
	private TokenRelayGatewayFilterFactory filterFactory;
	
	@Bean
	public RouteLocator myRoutes(RouteLocatorBuilder builder) {
	    return builder.routes()
	        .route(p -> p
	            .path("/bank/accounts/**")
	            .filters(f -> f.filter(filterFactory.apply())
					.rewritePath("/bank/accounts/(?<segment>.*)","/${segment}")
					.removeRequestHeader("Cookie"))
	            .uri("lb://ACCOUNTS")).
	        route(p -> p
		            .path("/bank/loans/**")
					.filters(f -> f.filter(filterFactory.apply())
							.rewritePath("/bank/loans/(?<segment>.*)","/${segment}")
							.removeRequestHeader("Cookie"))
		            .uri("lb://LOANS")).
	        route(p -> p
		            .path("/bank/cards/**")
					.filters(f -> f.filter(filterFactory.apply())
							.rewritePath("/bank/cards/(?<segment>.*)","/${segment}")
							.removeRequestHeader("Cookie"))
		            .uri("lb://CARDS")).build();
	}

}
