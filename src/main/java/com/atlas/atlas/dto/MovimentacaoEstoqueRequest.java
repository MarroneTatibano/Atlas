package com.atlas.atlas.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MovimentacaoEstoqueRequest(
        @NotNull @Positive Integer quantidade
) {
}