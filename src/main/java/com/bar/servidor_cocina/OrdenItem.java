package com.bar.servidor_cocina;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "orden_items")
public class OrdenItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    // Relación añadida para que el backend entienda el vínculo
    @ManyToOne
    @JoinColumn(name = "orden_id")
    @JsonIgnore
    private Orden orden;

    @Column(name = "producto_id")
    private Long productoId;

    private String nombre;
    private Double precio;
    private Integer cantidad;
    private String emoji;

    public OrdenItem() {}

    // Getters y Setters
    @JsonIgnore
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public String getEmoji() { return emoji; }
    public void setEmoji(String emoji) { this.emoji = emoji; }

    // Método corregido: Ahora sí asigna el valor al campo privado 'orden'
    public void setOrden(Orden orden) {
        this.orden = orden;
    }
    
    @JsonIgnore
    public Orden getOrden() {
        return orden;
    }
}