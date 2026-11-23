package com.atlas.atlas.dto;

import java.math.BigDecimal;

import com.atlas.atlas.model.Produto;

public record ProdutoResponse(
        Long id,
        String nome,
        BigDecimal preco,
        Integer estoque,
        CategoriaResponse categoria
) {
    public static ProdutoResponse from(Produto produto) {
        return new ProdutoResponse(
                produto.getId(),
                produto.getNome(),
                produto.getPreco(),
                produto.getEstoque(),
                produto.getCategoria() != null
                        ? CategoriaResponse.from(produto.getCategoria())
                        : null
        );
    }
}