package com.microservicios.springboot.webflux.apirest;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.microservicios.springboot.webflux.apirest.documentos.Categoria;
import com.microservicios.springboot.webflux.apirest.documentos.Producto;
import com.microservicios.springboot.webflux.apirest.service.ProductoServicio;

import reactor.core.publisher.Mono;

/**
 * Test de prueba de los servicos rest
 * @author Hector
 *
 */
//configuracion siempre debe estar acompañado, cuando se ejecuta el servidor mock WebEnvironment.MOCK
//@AutoConfigureWebTestClient
//Para que levantar en un puerto random netty= webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT
//Para levantar un servidor Mock
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringBootWebfluxApirestApplicationTests {
	
	
	/**
	 * Para realizar pruebas web reales, levanta la aplicacion en un ambine de test real
	 */
	@Autowired
	private WebTestClient client;

	@Autowired
	private ProductoServicio servicio;
	
	
	@Value("${config.base.endpoint}")
	private String url;
	
	@Test
	void listarTest() {
		
		
		
		client.get()
		.uri(url)
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		//Aqui validamos lo que necesitamos, 
		//En este caso validamos que esperamos un 200
		.expectStatus().isOk()
		//Que en la cabecera contenga application/json
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		//que en el cuerpo venga una lista
		.expectBodyList(Producto.class)
		.consumeWith(response->{
			List<Producto> lista= response.getResponseBody();
			lista.forEach(p->{
				System.out.println(p.toString());
			});
			Assertions.assertTrue(lista.size()>0);
		});
		//.hasSize(8)
		
		
	}
	
	
	
	@Test
	void verTest() {
		String nombre="Mouse";
		Producto producto= servicio.findByNombre(nombre).block();
		System.out.println("*************************************"+producto.toString());
		client.get()
		.uri(url+"/{id}",Collections.singletonMap("id", producto.getId()))
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody(Producto.class)
		.consumeWith(response->{
			Producto p= response.getResponseBody();
			
			Assertions.assertNotNull(p.getId());
			Assertions.assertEquals(p.getNombre(), nombre);
			
		})
		;
		
		
		
		/*
		.expectBody()
		//Que no sea vacio el id
		.jsonPath("$.id").isNotEmpty()
		.jsonPath("$.nombre").isEqualTo(nombre);*/
		
		
		
	}
	
	/**
	 * 
	 */
	@Test
	void crearTest() {
		String nombre="Muebles";
		Categoria categoria= servicio.findByCategoriaNombre(nombre).block();
		
		Producto producto= new Producto("Prueba",12.0,categoria);
		client.post().uri(url)
		.contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON)
		.body(Mono.just(producto),Producto.class)
		.exchange()
		.expectStatus().isCreated()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody()
		.jsonPath("$.producto.id").isNotEmpty()
		.jsonPath("$.producto.nombre").isEqualTo("Prueba")
		.jsonPath("$.producto.categoria.nombre").isEqualTo(nombre);
		
		
		
	}
	
	
	@Test
	void crear2Test() {
		String nombre="Muebles";
		Categoria categoria= servicio.findByCategoriaNombre(nombre).block();
		
		Producto producto= new Producto("Prueba",12.0,categoria);
		client.post().uri(url)
		.contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON)
		.body(Mono.just(producto),Producto.class)
		.exchange()
		.expectStatus().isCreated()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody(Producto.class);
//		.expectBody(new ParameterizedTypeReference<LinkedHashMap<String,Object>>() {})
//		.consumeWith(response->{
//			//Producto p= response.getResponseBody();
//			Object o= response.getResponseBody().get("producto");
//			Producto p= new ObjectMapper().convertValue(o, Producto.class);
//			Assertions.assertNotNull(p.getId());
//			Assertions.assertEquals(p.getNombre(), "Prueba");
//			
//		});
	}
	
	@Test
	void editarTest() {
		String nombreP="Samsumg";
		Producto producto= servicio.findByNombre(nombreP).block();
		
		String nombreC="Muebles";
		Categoria categoria= servicio.findByCategoriaNombre(nombreC).block();
		Producto productoEditado= new Producto("Asus Ebook",12.0,categoria);
		
		
		client.put().uri(url+"/{id}", Collections.singletonMap("id", producto.getId()))
		.contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON)
		.body(Mono.just(productoEditado),Producto.class)
		.exchange()
		.expectStatus().isCreated()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody()
		.jsonPath("$.id").isNotEmpty()
		.jsonPath("$.nombre").isEqualTo("Asus Ebook")
		.jsonPath("$.categoria.nombre").isEqualTo(nombreC)
		;
	}
	
	
	@Test
	void eliminarTest() {
		
		String nombreP="Cama";
		Producto producto= servicio.findByNombre(nombreP).block();
		
		client.delete().uri(url+"/{id}", Collections.singletonMap("id", producto.getId()))
		.exchange()
		.expectStatus().isNoContent()
		.expectBody()
		.isEmpty()
		
		;
		
//		
//		client.get().uri(url+"/{id}", Collections.singletonMap("id", producto.getId()))
//		.exchange()
//		.expectStatus().isNotFound()
//		.expectBody()
//		.isEmpty()
//		
//		;
		
	}
	
	
	
}
