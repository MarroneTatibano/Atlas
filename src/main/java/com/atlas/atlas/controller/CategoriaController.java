package com.atlas.atlas.controller;

import com.atlas.atlas.dto.CategoriaRequest;
import com.atlas.atlas.dto.CategoriaResponse;
import com.atlas.atlas.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public List<CategoriaResponse> listarCategorias() {
        return categoriaService.listarCategorias();
    }

    @GetMapping("/{id}")
    public CategoriaResponse buscarCategoria(@PathVariable Long id) {
        return categoriaService.buscarCategoria(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoriaResponse cadastrarCategoria(@Valid @RequestBody CategoriaRequest request) {
        return categoriaService.cadastrarCategoria(request);
    }

    @PutMapping("/{id}")
    public CategoriaResponse atualizarCategoria(@PathVariable Long id,
                                                @Valid @RequestBody CategoriaRequest request) {
        return categoriaService.atualizarCategoria(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluirCategoria(@PathVariable Long id) {
        categoriaService.excluirCategoria(id);
    }
}