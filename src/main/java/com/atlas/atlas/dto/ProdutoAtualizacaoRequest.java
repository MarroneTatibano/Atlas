package com.atlas.atlas.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProdutoAtualizacaoRequest(
        @NotBlank String nome,
        @NotNull @Positive BigDecimal preco,
        Long categoriaId
) {
}