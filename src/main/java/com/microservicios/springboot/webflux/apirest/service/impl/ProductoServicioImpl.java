package com.microservicios.springboot.webflux.apirest.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microservicios.springboot.webflux.apirest.documentos.Categoria;
import com.microservicios.springboot.webflux.apirest.documentos.Producto;
import com.microservicios.springboot.webflux.apirest.repository.CategoriaRepository;
import com.microservicios.springboot.webflux.apirest.repository.ProductoRepository;
import com.microservicios.springboot.webflux.apirest.service.ProductoServicio;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 
 * @author Hector
 *
 */
@Service
public class ProductoServicioImpl implements ProductoServicio {
	
	/*
	 * Repositorio de producto
	 */
	@Autowired
	private ProductoRepository productoRepository;
	
	/**
	 * Repositorio de categoria
	 */
	@Autowired
	private CategoriaRepository categoriaRepository;


	/*
	 * (non-Javadoc)
	 * @see com.microservicios.springboot.webflux.service.ProductoService#findAll()
	 */
	@Override
	public Flux<Producto> findAll() {
		// TODO Auto-generated method stub
		return productoRepository.findAll();
	}

	/*
	 * (non-Javadoc)
	 * @see com.microservicios.springboot.webflux.service.ProductoService#findById(java.lang.String)
	 */
	@Override
	public Mono<Producto> findById(String id) {
		return productoRepository.findById(id);
	}

	/*
	 * (non-Javadoc)
	 * @see com.microservicios.springboot.webflux.service.ProductoService#save(com.microservicios.springboot.webflux.documentos.Producto)
	 */
	@Override
	public Mono<Producto> save(Producto producto) {
		return productoRepository.save(producto);
	}

	/*
	 * (non-Javadoc)
	 * @see com.microservicios.springboot.webflux.service.ProductoService#delete(com.microservicios.springboot.webflux.documentos.Producto)
	 */
	@Override
	public Mono<Void> delete(Producto producto) {
		return productoRepository.delete(producto);
	}

	/*
	 * (non-Javadoc)
	 * @see com.microservicios.springboot.webflux.service.ProductoService#findAllConNombreUpperCase()
	 */
	@Override
	public Flux<Producto> findAllConNombreUpperCase() {
		return productoRepository.findAll().map(producto->{
			producto.setNombre(producto.getNombre().toUpperCase());
			return producto;
		});
	}

	/*
	 * (non-Javadoc)
	 * @see com.microservicios.springboot.webflux.service.ProductoService#findAllConNombreUpperCaseRepeat(java.lang.Long)
	 */
	@Override
	public Flux<Producto> findAllConNombreUpperCaseRepeat(Long repeticiones) {
		return findAllConNombreUpperCase().repeat(repeticiones);
	}

	/*
	 * (non-Javadoc)
	 * @see com.microservicios.springboot.webflux.service.ProductoService#findAllCategoria()
	 */
	@Override
	public Flux<Categoria> findAllCategoria() {
		return categoriaRepository.findAll();
	}

	/*
	 * (non-Javadoc)
	 * @see com.microservicios.springboot.webflux.service.ProductoService#findByIdCategoria(java.lang.String)
	 */
	@Override
	public Mono<Categoria> findByIdCategoria(String id) {
		return categoriaRepository.findById(id);
	}

	/*
	 * (non-Javadoc)
	 * @see com.microservicios.springboot.webflux.service.ProductoService#saveCategoria(com.microservicios.springboot.webflux.documentos.Categoria)
	 */
	@Override
	public Mono<Categoria> saveCategoria(Categoria categoria) {
		return categoriaRepository.save(categoria);
	}

	/*
	 * (non-Javadoc)
	 * @see com.microservicios.springboot.webflux.apirest.service.ProductoServicio#findByNombre(java.lang.String)
	 */
	@Override
	public Mono<Producto> findByNombre(String nombre) {
		return productoRepository.buscarPorNombre(nombre);
	}

	/*
	 * (non-Javadoc)
	 * @see com.microservicios.springboot.webflux.apirest.service.ProductoServicio#findByCategoriaNombre(java.lang.String)
	 */
	@Override
	public Mono<Categoria> findByCategoriaNombre(String nombre) {
		return categoriaRepository.obtenerPorNombre(nombre);
	}
	
	

}
