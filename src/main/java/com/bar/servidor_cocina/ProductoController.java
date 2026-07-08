package com.bar.servidor_cocina;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    public ProductoController(ProductoRepository repository, SimpMessagingTemplate messagingTemplate) {
        this.repository = repository;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping
    public List<Producto> obtenerTodos() {
        return repository.findAll();
    }

    @PostMapping
    public Producto guardarProducto(@RequestBody Producto producto) {
        Producto guardado = repository.save(producto);
        messagingTemplate.convertAndSend("/tema/cocina", (Object) Map.of("tipoAccion", "INVENTARIO_ACTUALIZADO"));
        return guardado;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        try {
            repository.deleteById(id);
            return ResponseEntity.ok("Producto eliminado");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al eliminar producto: " + e.getMessage());
        }
    }

    // 📉 DESCUENTAR STOCK CUANDO SE ENVÍA UN PEDIDO
    @PostMapping("/actualizar-stock")
    public ResponseEntity<?> actualizarStock(@RequestBody Map<String, Object> data) {
        try {
            Long productoId = ((Number) data.get("productoId")).longValue();
            int cantidadRestada = ((Number) data.get("cantidadRestada")).intValue();

            Optional<Producto> productoOpt = repository.findById(productoId);
            if (productoOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Producto no encontrado");
            }

            Producto producto = productoOpt.get();

            // Verificar que hay stock suficiente
            if (producto.getStock() < cantidadRestada) {
                return ResponseEntity.badRequest().body("Stock insuficiente: disponible=" + producto.getStock() + ", solicitado=" + cantidadRestada);
            }

            // Restar stock
            producto.setStock(producto.getStock() - cantidadRestada);
            repository.save(producto);

            messagingTemplate.convertAndSend("/tema/cocina", (Object) Map.of("tipoAccion", "INVENTARIO_ACTUALIZADO"));

            return ResponseEntity.ok(Map.of(
                "success", true,
                "mensaje", "Stock actualizado correctamente",
                "productoId", productoId,
                "stockRestante", producto.getStock()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al actualizar stock: " + e.getMessage());
        }
    }
}