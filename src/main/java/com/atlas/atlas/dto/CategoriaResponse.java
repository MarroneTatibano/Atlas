package com.atlas.atlas.dto;

import com.atlas.atlas.model.Categoria;

public record CategoriaResponse(
        Long id,
        String nome
) {
    public static CategoriaResponse from(Categoria categoria) {
        return new CategoriaResponse(categoria.getId(), categoria.getNome());
    }
}