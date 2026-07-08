package com.bar.servidor_cocina;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class WebController {

    @GetMapping("/panel")
    public RedirectView redirigirAlInicio() {
        return new RedirectView("/");
    }
    
    // De paso, le agregamos esta joyita: si alguien inventa un enlace que no existe (ej. /pepito)
    // también lo mandamos a la pantalla de inicio en vez de tirarle error.
    @GetMapping("/{path:[^\\.]*}")
    public RedirectView redirigirCualquierCosa() {
        return new RedirectView("/");
    }
}