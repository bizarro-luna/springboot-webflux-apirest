package com.microservicios.springboot.webflux.apirest.handler;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

import java.net.URI;
import java.time.LocalDate;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.microservicios.springboot.webflux.apirest.documentos.Categoria;
import com.microservicios.springboot.webflux.apirest.documentos.Producto;
import com.microservicios.springboot.webflux.apirest.service.ProductoServicio;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * Clase que seria como el controlador, pero en este caso es un bean
 * @author Hector
 *
 */
@Component
public class ProductoHandler {
	
	
	/**
	 * Servicio de producto
	 */
	@Autowired
	 private ProductoServicio servicio;
	
	/**
	 * Validador
	 */
	@Autowired
	private Validator validator;
	
	
	/**
	 * Subir Foto version 1
	 * @param request
	 * @return
	 */
	public Mono<ServerResponse> subirFoto(ServerRequest request){		
		
		String id= request.pathVariable("id");
		
		return request.multipartData()
				.map(multipart-> multipart.toSingleValueMap().get("archivo"))
				//Para hacer un casteo
				.cast(FilePart.class)
				.flatMap(archivo-> servicio.findById(id)
						.flatMap(p-> {
								Mono<byte[]> bytes= mergeDataBuffers(archivo.content());
								p.setFoto(bytes.block());
								return servicio.save(p);
				}))
				.flatMap(p-> ServerResponse
						     .created(URI.create("/api/v2/productos".concat(p.getId())))
						     .contentType(MediaType.APPLICATION_JSON)
						     .body(fromValue(p)))
				.switchIfEmpty(ServerResponse.noContent().build());
		
	}
	
	
	/**
	 * Subir foto otra manera
	 * @param request
	 * @return
	 */
	public Mono<ServerResponse> guardarConFoto(ServerRequest request) {

		//Se obtiene el producto del form-Data
		Mono<Producto> producto= request.multipartData().map(multipart-> {
		
			FormFieldPart nombre = (FormFieldPart) multipart.toSingleValueMap().get("nombre");
			FormFieldPart precio = (FormFieldPart) multipart.toSingleValueMap().get("precio");
			FormFieldPart categoriaId = (FormFieldPart) multipart.toSingleValueMap().get("categoria.id");
			FormFieldPart categoriaNombre = (FormFieldPart) multipart.toSingleValueMap().get("categoria.nombre");
			
			Categoria c= new Categoria(categoriaNombre.value());
			c.setId(categoriaId.value());
			
			return new Producto(nombre.value(),Double.parseDouble(precio.value()), c);
		});
 
		return request.multipartData().map(multipart -> multipart.toSingleValueMap().get("archivo"))
				// Para hacer un casteo
				.cast(FilePart.class)
				.flatMap(archivo -> producto.flatMap(p -> {
					Mono<byte[]> bytes = mergeDataBuffers(archivo.content());
					p.setFoto(bytes.block());
					p.setFecha(LocalDate.now());
					return servicio.save(p);
				}))
				.flatMap(p -> ServerResponse.created(URI.create("/api/v2/productos".concat(p.getId())))
						.contentType(MediaType.APPLICATION_JSON)
						.body(fromValue(p)))
				.switchIfEmpty(ServerResponse.noContent().build());

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
	 * Metodo para listar todos los productos
	 * @param request
	 * @return
	 */
	public Mono<ServerResponse> listar(ServerRequest request){		
		return ServerResponse.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(servicio.findAll(),Producto.class);
	}
	
	
	/**
	 * Metodo para ver un producto por medio del id
	 * @param request
	 * @return
	 */
	public Mono<ServerResponse> ver(ServerRequest request){
		
		
		String id= request.pathVariable("id");
		
		return servicio.findById(id).flatMap(p-> ServerResponse.ok().body(fromValue(p))     )
				.switchIfEmpty(ServerResponse.notFound().build());
	}
	
	
	/**
	 * Metodo para guardar un producto
	 * @param request
	 * @return
	 */
	public Mono<ServerResponse> crear(ServerRequest request){
		
		Mono<Producto> producto= request.bodyToMono(Producto.class);
		
		return producto.flatMap(p -> {
			Errors errors= new BeanPropertyBindingResult(p, Producto.class.getName());
			validator.validate(p, errors);
			
			if(errors.hasErrors()) {
				return Flux.fromIterable(errors.getFieldErrors())
						//El map itera la Lis<FieldError>
						.map(fieldError ->"El campo "+ fieldError.getField()+" "+fieldError.getDefaultMessage())
						// y al pasa a una Lis<String>
						.collectList()
						.flatMap(list -> ServerResponse.badRequest().body(fromValue(list)));
				
			}else {
				if(p.getFecha()==null) {
					p.setFecha(LocalDate.now());
				}
				return servicio.save(p).flatMap(pdb -> ServerResponse
						.created(URI.create("/api/v2/productos".concat(pdb.getId())))
						//Opcional
						.contentType(MediaType.APPLICATION_JSON)
						.body(fromValue(pdb)) );
			}
			
		});
		
	}
	
	/**
	 * Metodo para actualizar el producto
	 * @param request
	 * @return
	 */
	public Mono<ServerResponse> actualizar(ServerRequest request) {

		Mono<Producto> producto = request.bodyToMono(Producto.class);
		String id= request.pathVariable("id");

		Mono<Producto> productoDb= servicio.findById(id);
		
		return productoDb.zipWith(producto,( db, req )-> {
			
			db.setNombre(req.getNombre());
			db.setPrecio(req.getPrecio());
			db.setCategoria(req.getCategoria());
			
			return db;
			
		}).flatMap(p-> ServerResponse
				.created(URI.create("/api/v2/productos".concat(p.getId())))
				.contentType(MediaType.APPLICATION_JSON)
				.body(servicio.save(p),Producto.class))
				.switchIfEmpty(ServerResponse.notFound().build());

	}
	
	
	/**
	 * Metodo para eliminar 
	 * @param request
	 * @return
	 */
	public Mono<ServerResponse> eliminar(ServerRequest request) {
		
		String id= request.pathVariable("id");

		Mono<Producto> productoDb= servicio.findById(id);
		
		                  //delete retorna un Mono<Void> para convertirlo a ServerResponse se utiliza el then  
		return productoDb.flatMap(p->servicio.delete(p).then(ServerResponse.noContent().build()))
				//Por si no existe el producto
				.switchIfEmpty(ServerResponse.notFound().build());
		
		
	}
	
	

}
