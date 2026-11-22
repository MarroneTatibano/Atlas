package com.atlas.atlas.repository;

import com.atlas.atlas.model.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {

    List<Movimentacao> findByProdutoIdOrderByDataHoraDesc(Long produtoId);

    void deleteByProdutoId(Long produtoId);
}