package com.atlas.atlas.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import com.atlas.atlas.model.Produto;
import com.atlas.atlas.service.ProdutoService;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping("/produtos")
    public List<Produto> listarProdutos() {
        return produtoService.listarProdutos();
    }
    @PostMapping("/produtos")
    @ResponseStatus(HttpStatus.CREATED)
    public Produto cadastrarProduto(@Valid @RequestBody Produto produto) {
        return produtoService.cadastrarProduto(produto);
    }
    @GetMapping("/produtos/{id}")
    public Produto buscarProduto(@PathVariable Long id) {
        return produtoService.buscarProduto(id);
    }
    @PutMapping("/produtos/{id}")
    public Produto atualizarProduto(@PathVariable Long id, @Valid @RequestBody Produto produto) {
        return produtoService.atualizarProduto(id, produto);
    }
    @DeleteMapping("/produtos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluirProduto(@PathVariable Long id) {
        produtoService.excluirProduto(id);
    }
}