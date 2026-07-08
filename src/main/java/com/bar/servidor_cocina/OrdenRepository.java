package com.bar.servidor_cocina;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdenRepository extends JpaRepository<Orden, Long> {
    // Ventas del día (fecha en formato YYYY-MM-DD)
    List<Orden> findByFecha(String fecha);

    // Ventas del mes (fecha comienza con "YYYY-MM")
    List<Orden> findByFechaStartingWith(String prefix);
}
