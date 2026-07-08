package com.bar.servidor_cocina;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Solo buscamos por el nombre. ¡Java se encargará de la contraseña después!
    Optional<Usuario> findByUsername(String username);
}
