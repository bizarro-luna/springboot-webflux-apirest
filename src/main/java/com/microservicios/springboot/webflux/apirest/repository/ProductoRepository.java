package com.microservicios.springboot.webflux.apirest.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.microservicios.springboot.webflux.apirest.documentos.Producto;

import reactor.core.publisher.Mono;



/*
 * Repositorio reactivo para mongo
 */
public interface ProductoRepository extends ReactiveMongoRepository<Producto,String> {
	
	/**
	 * Buscar producto por nombre
	 * @param nombre
	 * @return
	 */
	Mono<Producto> findByNombre(String nombre);
	
	
	/**
	 * Consulta tipoca de mongodb
	 * @param nombre
	 * @return
	 */
	@Query("{'nombre':?0 }")
	Mono<Producto> buscarPorNombre(String nombre);

}
