
package com.atlas.atlas.controller;

import com.atlas.atlas.model.StatusAtlas;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InicioController {

    @GetMapping("/")
    public StatusAtlas inicio() {
        return new StatusAtlas(
                "Atlas",
                "Sistema funcionando!"
        );
    }
}
