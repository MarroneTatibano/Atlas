package com.atlas.atlas.service;

import com.atlas.atlas.dto.MovimentacaoEstoqueRequest;
import com.atlas.atlas.dto.MovimentacaoResponse;
import com.atlas.atlas.dto.ProdutoRequest;
import com.atlas.atlas.dto.ProdutoResponse;
import com.atlas.atlas.model.Movimentacao;
import com.atlas.atlas.model.Produto;
import com.atlas.atlas.model.TipoMovimentacao;
import com.atlas.atlas.repository.MovimentacaoRepository;
import com.atlas.atlas.repository.ProdutoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final MovimentacaoRepository movimentacaoRepository;               // NOVO

    public ProdutoService(ProdutoRepository produtoRepository,
                          MovimentacaoRepository movimentacaoRepository) {     // NOVO parâmetro
        this.produtoRepository = produtoRepository;
        this.movimentacaoRepository = movimentacaoRepository;                  // NOVO
    }

    public List<ProdutoResponse> listarProdutos() {
        return produtoRepository.findAll().stream()
                .map(ProdutoResponse::from)
                .toList();
    }

    public ProdutoResponse buscarProduto(Long id) {
        return ProdutoResponse.from(buscarEntidade(id));
    }

    public ProdutoResponse cadastrarProduto(ProdutoRequest request) {
        Produto produto = new Produto();
        copiarDados(request, produto);
        return ProdutoResponse.from(produtoRepository.save(produto));
    }

    public ProdutoResponse atualizarProduto(Long id, ProdutoRequest request) {
        Produto produto = buscarEntidade(id);
        copiarDados(request, produto);
        return ProdutoResponse.from(produtoRepository.save(produto));
    }

    @Transactional                                                             // NOVO
    public ProdutoResponse registrarEntrada(Long id, MovimentacaoEstoqueRequest request) {
        Produto produto = buscarEntidade(id);
        produto.setEstoque(produto.getEstoque() + request.quantidade());
        Produto salvo = produtoRepository.save(produto);

        movimentacaoRepository.save(                                           // NOVO: grava no histórico
                new Movimentacao(salvo, TipoMovimentacao.ENTRADA, request.quantidade()));

        return ProdutoResponse.from(salvo);
    }

    @Transactional                                                             // NOVO
    public ProdutoResponse registrarSaida(Long id, MovimentacaoEstoqueRequest request) {
        Produto produto = buscarEntidade(id);

        if (produto.getEstoque() < request.quantidade()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Estoque insuficiente. Disponível: " + produto.getEstoque());
        }

        produto.setEstoque(produto.getEstoque() - request.quantidade());
        Produto salvo = produtoRepository.save(produto);

        movimentacaoRepository.save(                                           // NOVO: grava no histórico
                new Movimentacao(salvo, TipoMovimentacao.SAIDA, request.quantidade()));

        return ProdutoResponse.from(salvo);
    }

    public List<MovimentacaoResponse> listarMovimentacoes(Long id) {           // NOVO método
        buscarEntidade(id);
        return movimentacaoRepository.findByProdutoIdOrderByDataHoraDesc(id).stream()
                .map(MovimentacaoResponse::from)
                .toList();
    }

    @Transactional                                                             // NOVO
    public void excluirProduto(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Produto não encontrado");
        }
        movimentacaoRepository.deleteByProdutoId(id);                          // NOVO: apaga o histórico antes
        produtoRepository.deleteById(id);
    }

    private Produto buscarEntidade(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Produto não encontrado"));
    }

    private void copiarDados(ProdutoRequest request, Produto produto) {
        produto.setNome(request.nome());
        produto.setPreco(request.preco());
        produto.setEstoque(request.estoque());
    }
}