package com.atlas.atlas.service;

import com.atlas.atlas.dto.ProdutoRequest;
import com.atlas.atlas.dto.ProdutoResponse;
import com.atlas.atlas.model.Produto;
import com.atlas.atlas.repository.ProdutoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
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

    public void excluirProduto(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Produto não encontrado");
        }
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