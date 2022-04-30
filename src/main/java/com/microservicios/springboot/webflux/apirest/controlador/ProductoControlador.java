package com.microservicios.springboot.webflux.apirest.controlador;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebExchangeBindException;

import com.microservicios.springboot.webflux.apirest.documentos.Producto;
import com.microservicios.springboot.webflux.apirest.service.ProductoServicio;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/productos")
public class ProductoControlador {

	@Autowired
	private ProductoServicio productoServicio;
	
	/*
	 * Variable para los logs
	 */
	private static final Logger log= LoggerFactory.getLogger(ProductoControlador.class);
	
	
	@GetMapping
	public Mono<ResponseEntity<Flux<Producto>>> listar(){
		
		return Mono.just(
				ResponseEntity.ok()
				//Es opcional
				.contentType(MediaType.APPLICATION_JSON)
				.body(productoServicio.findAll())); 
		
	}
	
	
	@GetMapping("/{id}")
	public Mono<ResponseEntity<Producto>> ver(@PathVariable String id){
		
		return productoServicio.findById(id)
				//map para modificar el flujo y que regrese un Mono<ResponseEntity<Prodcuto>>
				.map(p->ResponseEntity.ok().body(p))
				//Si llega no encontrar el producto
				.defaultIfEmpty(ResponseEntity.notFound().build());
		
		
	}
	
	/**
	 * Crear producto, validar los datos obligatorios con jax.validation
	 * @param producto
	 * @return
	 */
	@PostMapping
	public Mono<ResponseEntity<Map<String,Object>>> crear(@Valid @RequestBody Mono<Producto> monoProducto){
		
		Map<String,Object> respuesta= new HashMap<>();
		
		return monoProducto.flatMap(producto->{
			
			if(producto.getFecha()==null) {
				producto.setFecha(LocalDate.now());
			}
			 //Con el map convierte del save recibir un Mono<Producto> a un Mono<ResponseEntity<Producto>>
			return productoServicio.save(producto).map(p-> {
			    
				respuesta.put("producto", p);
				respuesta.put("mensaje", "Producto creado con éxito");
				respuesta.put("fecha", LocalDateTime.now());
				return ResponseEntity
					.created(URI.create("/api/productos/".concat(p.getId())))
					//.status(HttpStatus.CREATED)
					.contentType(MediaType.APPLICATION_JSON)
					.body(respuesta);
			       
			});
			
		})
		.onErrorResume(ex->{
			
			return Mono.just(ex).cast(WebExchangeBindException.class)
					.flatMap(e->Mono.just(e.getFieldErrors()))
					.flatMapMany(errores->Flux.fromIterable(errores))
					.map(fieldError-> "El campo "+fieldError.getField()+" "+fieldError.getDefaultMessage())
					.collectList()
					.flatMap(list->{
						respuesta.put("errors", list);
						respuesta.put("fecha", LocalDateTime.now());
						respuesta.put("status", HttpStatus.BAD_REQUEST.value());
						return Mono.just(ResponseEntity.badRequest().body(respuesta));
					});
			
		});
		
	
		
		
		
	}
	
	@PutMapping("/{id}")
	public Mono<ResponseEntity<Producto>> actualizar(@RequestBody Producto producto ,@PathVariable String id){
		                                //El flat es para cambiar los datos del producto y retornarlo con un nuevo valor
		return productoServicio.findById(id).flatMap(p-> {
			
			p.setNombre(producto.getNombre());
			p.setPrecio(producto.getPrecio());
			p.setCategoria(producto.getCategoria());
			
			return productoServicio.save(p);
			
		})
		.map(p->ResponseEntity.created(URI.create("/api/productos/".concat(p.getId())))
					.contentType(MediaType.APPLICATION_JSON)
					.body(p))
		.defaultIfEmpty(ResponseEntity.notFound().build());
		
		
	}
	
	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Void>> eliminar(@PathVariable String id){
		
		return productoServicio.findById(id).map(p->{
					productoServicio.delete(p).subscribe();
					return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
				
		})
		.defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
		
		
	}
	
	
	/**
	 * Cargar imagen
	 * @param id
	 * @param archivo
	 * @return
	 */
	@PostMapping("/upload/{id}")
	public Mono<ResponseEntity<Producto>> subirFoto(@PathVariable String id,@RequestPart FilePart archivo){
		
		return productoServicio.findById(id).flatMap( p->{
			//En el flatMap puedes cambiar los valore y hacer cualquier pericion al servico
			if(archivo!=null) {
				Mono<byte[]> monoBytes= mergeDataBuffers(archivo.content());
				//Mono.block cuando el Mono es de tipo primitivo  Mono<byte[]>
				p.setFoto(monoBytes.block());
			}
			return productoServicio.save(p);
		})
		.map(p->ResponseEntity.created(URI.create("/api/productos/".concat(p.getId())))
						.contentType(MediaType.APPLICATION_JSON)
						.body(p))
	    .defaultIfEmpty(ResponseEntity.notFound().build());
	}
	
	
	
	/**
	 * Metodo para obtener los byte[] del archivo 
	 * @param dataBufferFlux
	 * @return
	 */
	private Mono<byte[]> mergeDataBuffers(Flux<DataBuffer> dataBufferFlux) {
        return DataBufferUtils.join(dataBufferFlux)
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    
                    return bytes;
                });
    }
	
	
	/**
	 * Version dos, enviar los datos en el form data
	 * @param producto
	 * @param archivo
	 * @return
	 */
	@PostMapping("/v2")
	public Mono<ResponseEntity<Producto>> crearFormData(Producto producto,@RequestPart FilePart archivo){
		
		if(producto.getFecha()==null) {
			producto.setFecha(LocalDate.now());
		}
		
		if(archivo!=null) {
			Mono<byte[]> monoBytes= mergeDataBuffers(archivo.content());
			//Mono.block cuando el Mono es de tipo primitivo  Mono<byte[]>
			producto.setFoto(monoBytes.block());
		}
		
		 //Con el map convierte del save recibir un Mono<Producto> a un Mono<ResponseEntity<Producto>>
		return productoServicio.save(producto).map(p-> ResponseEntity
				.created(URI.create("/api/productos/".concat(p.getId())))
				.contentType(MediaType.APPLICATION_JSON)
				.body(p));
		
	}
	
	
	
	

}
