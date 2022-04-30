package com.microservicios.springboot.webflux.apirest.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.microservicios.springboot.webflux.apirest.documentos.Categoria;

import reactor.core.publisher.Mono;





public interface CategoriaRepository extends ReactiveMongoRepository<Categoria, String> {
	
	
	/**
	 * Buscar categoria por nombre
	 * @param nombre
	 * @return
	 */
	Mono<Categoria> findByNombre(String nombre);
	
	/**
	 * Obtener categoria por nombre
	 * @param nombre
	 * @return
	 */
	@Query("{'nombre':?0 }")
	Mono<Categoria> obtenerPorNombre(String nombre);

}
