package com.bar.servidor_cocina;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 🛡️ Agrega esta línea justo arriba de la clase
@SpringBootApplication(scanBasePackages = {"com.bar.servidor_cocina"})
public class ServidorCocinaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServidorCocinaApplication.class, args);
    }
}