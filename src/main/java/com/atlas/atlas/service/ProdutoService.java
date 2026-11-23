package com.atlas.atlas.service;

import com.atlas.atlas.dto.MovimentacaoEstoqueRequest;
import com.atlas.atlas.dto.MovimentacaoResponse;
import com.atlas.atlas.dto.ProdutoRequest;
import com.atlas.atlas.dto.ProdutoResponse;
import com.atlas.atlas.model.Categoria;
import com.atlas.atlas.model.Movimentacao;
import com.atlas.atlas.model.Produto;
import com.atlas.atlas.model.TipoMovimentacao;
import com.atlas.atlas.repository.CategoriaRepository;
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
    private final MovimentacaoRepository movimentacaoRepository;
    private final CategoriaRepository categoriaRepository;                     // NOVO

    public ProdutoService(ProdutoRepository produtoRepository,
                          MovimentacaoRepository movimentacaoRepository,
                          CategoriaRepository categoriaRepository) {           // NOVO parâmetro
        this.produtoRepository = produtoRepository;
        this.movimentacaoRepository = movimentacaoRepository;
        this.categoriaRepository = categoriaRepository;                        // NOVO
    }

    public List<ProdutoResponse> listarProdutos(Long categoriaId) {            // NOVO parâmetro
        List<Produto> produtos = (categoriaId == null)
                ? produtoRepository.findAll()
                : produtoRepository.findByCategoriaId(categoriaId);

        return produtos.stream()
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

    @Transactional
    public ProdutoResponse registrarEntrada(Long id, MovimentacaoEstoqueRequest request) {
        Produto produto = buscarEntidade(id);
        produto.setEstoque(produto.getEstoque() + request.quantidade());
        Produto salvo = produtoRepository.save(produto);

        movimentacaoRepository.save(
                new Movimentacao(salvo, TipoMovimentacao.ENTRADA, request.quantidade()));

        return ProdutoResponse.from(salvo);
    }

    @Transactional
    public ProdutoResponse registrarSaida(Long id, MovimentacaoEstoqueRequest request) {
        Produto produto = buscarEntidade(id);

        if (produto.getEstoque() < request.quantidade()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Estoque insuficiente. Disponível: " + produto.getEstoque());
        }

        produto.setEstoque(produto.getEstoque() - request.quantidade());
        Produto salvo = produtoRepository.save(produto);

        movimentacaoRepository.save(
                new Movimentacao(salvo, TipoMovimentacao.SAIDA, request.quantidade()));

        return ProdutoResponse.from(salvo);
    }

    public List<MovimentacaoResponse> listarMovimentacoes(Long id) {
        buscarEntidade(id);
        return movimentacaoRepository.findByProdutoIdOrderByDataHoraDesc(id).stream()
                .map(MovimentacaoResponse::from)
                .toList();
    }

    @Transactional
    public void excluirProduto(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Produto não encontrado");
        }
        movimentacaoRepository.deleteByProdutoId(id);
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
        produto.setCategoria(buscarCategoria(request.categoriaId()));          // NOVO
    }

    private Categoria buscarCategoria(Long categoriaId) {                      // NOVO método
        if (categoriaId == null) {
            return null;
        }
        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Categoria informada não existe"));
    }
}