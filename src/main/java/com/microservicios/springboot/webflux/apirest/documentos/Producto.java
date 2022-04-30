package com.microservicios.springboot.webflux.apirest.documentos;

import java.io.Serializable;
import java.time.LocalDate;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Document(collection="productos")
public class Producto implements Serializable {
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4150309371305779852L;


	//El id en mongo siempre es string
	@Id
	private String id;
	
	
	@NotEmpty
	private String nombre;
	
	@NotNull
	private Double precio;
	
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private LocalDate fecha;
	
	@Valid
	@NotNull
	private Categoria categoria;
	
	
	@JsonIgnore
	private byte[] foto;
	
	/**
	 * Retornal la foto en HashCode
	 * @return
	 */
	public Integer getFotoHashCode() {
		return this.foto!=null?this.foto.hashCode():null;
	}
	
	
	

	public Producto(String nombre, Double precio) {
		this.nombre = nombre;
		this.precio = precio;
	}
	
	public Producto(String nombre, Double precio,Categoria categoria) {
		this(nombre, precio);
		this.categoria=categoria;
	}
	
	
	
	public Producto() {}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the nombre
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * @param nombre the nombre to set
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * @return the precio
	 */
	public Double getPrecio() {
		return precio;
	}

	/**
	 * @param precio the precio to set
	 */
	public void setPrecio(Double precio) {
		this.precio = precio;
	}

	/**
	 * @return the fecha
	 */
	public LocalDate getFecha() {
		return fecha;
	}

	/**
	 * @param fecha the fecha to set
	 */
	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}
	
	

	/**
	 * @return the categoria
	 */
	public Categoria getCategoria() {
		return categoria;
	}



	/**
	 * @param categoria the categoria to set
	 */
	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}
	
	



	/**
	 * @return the foto
	 */
	public byte[] getFoto() {
		return foto;
	}




	/**
	 * @param foto the foto to set
	 */
	public void setFoto(byte[] foto) {
		this.foto = foto;
	}




	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Producto [id=");
		builder.append(id);
		builder.append(", nombre=");
		builder.append(nombre);
		builder.append(", precio=");
		builder.append(precio);
		builder.append(", fecha=");
		builder.append(fecha);
		builder.append("]");
		return builder.toString();
	}
	
	
	

}
