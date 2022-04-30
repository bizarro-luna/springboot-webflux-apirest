package com.microservicios.springboot.webflux.apirest;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import com.microservicios.springboot.webflux.apirest.documentos.Categoria;
import com.microservicios.springboot.webflux.apirest.documentos.Producto;
import com.microservicios.springboot.webflux.apirest.service.ProductoServicio;

import reactor.core.publisher.Flux;



@EnableEurekaClient
@SpringBootApplication
public class SpringBootWebfluxApirestApplication implements CommandLineRunner {
	
	
	@Autowired
	private ReactiveMongoTemplate mongoTemplate;
	
	@Autowired
	private ProductoServicio productoService;
	
	
	private static final Logger log= LoggerFactory.getLogger(SpringBootWebfluxApirestApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebfluxApirestApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		mongoTemplate.dropCollection("productos").subscribe();
		mongoTemplate.dropCollection("categorias").subscribe();
		
		
		Categoria electronico=new Categoria("Electronico");
		Categoria deporte=new Categoria("Deporte");
		Categoria computacion=new Categoria("Computación");
		Categoria muebles=new Categoria("Muebles");
		
		//Primero se ejecuta el insertar las categorias
		Flux.just(electronico,deporte,computacion,muebles)
		.flatMap(productoService::saveCategoria)
		.doOnNext(c ->{
			log.info("Se guardo la categoria :"+c.getNombre()+" con el ID: "+c.getId());
		})
		//Para ejecutar el siguiente insert otro Publisher(then Many)
		.thenMany(
				Flux.just(
						new Producto("Apple",45.78,electronico),
						new Producto("Samsumg",13216.78,electronico),
						new Producto("Tenis Nike",7897.78,deporte),
						new Producto("Tenus Adidas",456.78,deporte),
						new Producto("Lap",44534.78,computacion),
						new Producto("Mouse",4565.78,computacion),
						new Producto("Sillon",456.78,muebles),
						new Producto("Cama",456.78,muebles)
						)
				.flatMap(producto-> {
					producto.setFecha(LocalDate.now());
					return productoService.save(producto); 
					})
				
				)
		.subscribe(producto-> log.info("Insert "+producto.toString()));
		
	}

}
