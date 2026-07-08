package com.bar.servidor_cocina;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api")
public class OrdenController {

    private final OrdenRepository ordenRepository;
    private final ProductoRepository productoRepository;

    public OrdenController(OrdenRepository ordenRepository, ProductoRepository productoRepository) {
        this.ordenRepository = ordenRepository;
        this.productoRepository = productoRepository;
    }

    @GetMapping("/ordenes")
    public List<Orden> obtenerOrdenes() {
        return ordenRepository.findAll();
    }

    @PostMapping("/ordenes")
    public ResponseEntity<?> guardarOrden(@RequestBody Orden nuevaOrden) {
        try {
            if (nuevaOrden.getItems() != null) {
                nuevaOrden.getItems().forEach(item -> item.setOrden(nuevaOrden));
            }
            Orden guardada = ordenRepository.save(nuevaOrden);
            return ResponseEntity.ok(guardada);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al guardar la orden: " + e.getMessage());
        }
    } // <--- ¡AQUÍ FALTABA ESTA LLAVE! Esta cierra el método guardarOrden

    // 📅 Ventas del día
    @GetMapping("/ventas/dia")
    public List<Orden> ventasDelDia() {
        String hoy = java.time.LocalDate.now().toString();
        return ordenRepository.findByFechaStartingWith(hoy);
    }

    // 📅 Ventas del mes
    @GetMapping("/ventas/mes")
    public List<Orden> ventasDelMes() {
        java.time.LocalDate ahora = java.time.LocalDate.now();
        String prefijo = ahora.getYear() + "-" + String.format("%02d", ahora.getMonthValue());
        return ordenRepository.findByFechaStartingWith(prefijo);
    }

    // 📥 Exportar Ventas del mes (alias para frontend)
    @GetMapping("/ventas/exportar-mes")
    public List<Orden> exportarVentasDelMes() {
        return ventasDelMes();
    }

    // 🔄 ACTUALIZAR ESTADO DE ORDEN (cocina)
    @PutMapping("/ordenes/{id}/estado")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long id, @RequestBody Map<String, Object> datos) {
        try {
            Optional<Orden> ordenOpt = ordenRepository.findById(id);
            if (ordenOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Orden no encontrada");
            }
            Orden orden = ordenOpt.get();
            if (datos.containsKey("estado")) {
                orden.setEstado((String) datos.get("estado"));
            }
            ordenRepository.save(orden);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "mensaje", "Estado actualizado",
                "ordenId", id,
                "estado", orden.getEstado()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al actualizar estado: " + e.getMessage());
        }
    }
    // 💰 REGISTRAR PAGO - Marcar orden como pagada y descontar inventario
    @PutMapping("/ordenes/{id}/pagar")
    @Transactional
    public ResponseEntity<?> marcarComoPagada(@PathVariable Long id, @RequestBody Map<String, Object> datos) {
        try {
            Optional<Orden> ordenOpt = ordenRepository.findById(id);
            if (ordenOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Orden no encontrada");
            }

            Orden orden = ordenOpt.get();

            orden.setEstado("pagada");

            if (datos.containsKey("metodoPago")) {
                orden.setMetodoPago((String) datos.get("metodoPago"));
            }
            if (datos.containsKey("fechaPago")) {
                orden.setFechaPago((String) datos.get("fechaPago"));
            }

            // Descontar del inventario
            if (orden.getItems() != null) {
                for (OrdenItem item : orden.getItems()) {
                    if (item.getProductoId() != null) {
                        Optional<Producto> prodOpt = productoRepository.findById(item.getProductoId());
                        if (prodOpt.isPresent()) {
                            Producto p = prodOpt.get();
                            // Prevenir stock negativo (opcional, por ahora solo restamos)
                            int nuevoStock = p.getStock() - item.getCantidad();
                            p.setStock(nuevoStock < 0 ? 0 : nuevoStock);
                            productoRepository.save(p);
                        }
                    }
                }
            }

            ordenRepository.save(orden);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "mensaje", "Orden marcada como pagada",
                "ordenId", id,
                "estado", "pagada",
                "metodoPago", datos.getOrDefault("metodoPago", "No especificado")
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al registrar pago: " + e.getMessage());
        }
    }

    // ❌ CANCELAR / ELIMINAR ORDEN
    @DeleteMapping("/ordenes/{id}")
    public ResponseEntity<?> eliminarOrden(@PathVariable Long id) {
        try {
            if (ordenRepository.existsById(id)) {
                ordenRepository.deleteById(id);
            }
            return ResponseEntity.ok(Map.of("success", true, "mensaje", "Orden eliminada (o ya no existía)"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "mensaje", "Error al eliminar orden: " + e.getMessage()));
        }
    }
}