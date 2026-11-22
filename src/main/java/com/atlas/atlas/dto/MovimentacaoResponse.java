package com.atlas.atlas.dto;

import com.atlas.atlas.model.Movimentacao;
import com.atlas.atlas.model.TipoMovimentacao;

import java.time.LocalDateTime;

public record MovimentacaoResponse(
        Long id,
        TipoMovimentacao tipo,
        Integer quantidade,
        LocalDateTime dataHora
) {
    public static MovimentacaoResponse from(Movimentacao movimentacao) {
        return new MovimentacaoResponse(
                movimentacao.getId(),
                movimentacao.getTipo(),
                movimentacao.getQuantidade(),
                movimentacao.getDataHora()
        );
    }
}