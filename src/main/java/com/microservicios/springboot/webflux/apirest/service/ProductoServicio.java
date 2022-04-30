package com.microservicios.springboot.webflux.apirest.service;



import com.microservicios.springboot.webflux.apirest.documentos.Categoria;
import com.microservicios.springboot.webflux.apirest.documentos.Producto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductoServicio {
	
	/**
	 * Buscar todos los productos
	 * @return
	 */
	Flux<Producto> findAll();
	
	/**
	 * Buscar todos los productos con nombre en mayusculas
	 * @return
	 */
	Flux<Producto> findAllConNombreUpperCase();
	
	/**
	 * Buscar todos,con nombre en mayusculas y repeticion
	 * @param repeticiones
	 * @return
	 */
	Flux<Producto> findAllConNombreUpperCaseRepeat(Long repeticiones);
	
	/**
	 * Buscar producto por ID
	 * @param id
	 * @return
	 */
	Mono<Producto> findById(String id);
	
	
	/**
	 * Guardar Producto
	 * @param producto
	 * @return
	 */
	Mono<Producto> save(Producto producto);
	
	/**
	 * eliminar producto
	 * @param producto
	 * @return
	 */
	Mono<Void> delete(Producto producto);
	
	
	/**
	 * Buscar todas las categorias
	 * @return
	 */
	Flux<Categoria> findAllCategoria();
	
	
	/**
	 * Obtener un Mono de categoria
	 * @param id
	 * @return
	 */
	Mono<Categoria> findByIdCategoria(String id);
	
	
	/**
	 * Guardar la categoria
	 * @param categoria
	 * @return
	 */
	Mono<Categoria> saveCategoria(Categoria categoria);
	
	/**
	 * Buscar producto por nombre
	 * @param nombre
	 * @return
	 */
	Mono<Producto> findByNombre(String nombre);
	
	/**
	 * Buscar categoria por nombre
	 * @param nombre
	 * @return
	 */
	Mono<Categoria> findByCategoriaNombre(String nombre);

}
