package com.microservicios.springboot.webflux.apirest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.microservicios.springboot.webflux.apirest.handler.ProductoHandler;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;



/**
 * Clase de configuración para endpoins 100% Reactivo
 * @author Hector
 *
 */
@Configuration
public class RouterFunctionConfig {
	
	 
	
	@Bean
	public RouterFunction<ServerResponse> routes(ProductoHandler handler){
		
		
		return RouterFunctions.route(GET("/api/v2/productos").or(GET("/api/v3/productos") ),handler::listar)
				//Para agregar mas endpoints
				.andRoute(GET("/api/v2/productos/{id}") , handler::ver)
				.andRoute(POST("/api/v2/productos"), handler::crear)
				.andRoute(PUT("/api/v2/productos/{id}"), request-> handler.actualizar(request))
				.andRoute(DELETE("/api/v2/productos/{id}"), handler::eliminar)
				.andRoute(POST("/api/v2/productos/upload/{id}"), handler::subirFoto)
				.andRoute(POST("/api/v2/productos/crear"),  request-> handler.guardarConFoto(request));
		
	}

}
