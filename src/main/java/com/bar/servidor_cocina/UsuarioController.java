package com.bar.servidor_cocina;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public List<Usuario> getAll() {
        return usuarioRepository.findAll();
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario nuevoUsuario) {
        if (usuarioRepository.findByUsername(nuevoUsuario.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("El usuario ya existe");
        }
        usuarioRepository.save(nuevoUsuario);
        return ResponseEntity.ok("Usuario registrado con éxito");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(request.getUsername());

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).body("❌ Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        if (usuario.getPassword().equals(request.getPassword())) {
            usuario.setPassword("");
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.status(401).body("❌ Contraseña incorrecta");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        usuarioRepository.deleteById(id);
        return ResponseEntity.ok("Eliminado");
    }
}