package com.atlas.atlas.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
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
import com.atlas.atlas.dto.ProdutoRequest;
import com.atlas.atlas.dto.ProdutoResponse;
import com.atlas.atlas.dto.MovimentacaoEstoqueRequest;
import com.atlas.atlas.dto.MovimentacaoResponse;

@RestController
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping("/produtos")
    public List<ProdutoResponse> listarProdutos() {
        return produtoService.listarProdutos();
    }

    @PostMapping("/produtos")
    @ResponseStatus(HttpStatus.CREATED)
    public ProdutoResponse cadastrarProduto(@Valid @RequestBody ProdutoRequest request) {
        return produtoService.cadastrarProduto(request);
    }

    @GetMapping("/produtos/{id}")
    public ProdutoResponse buscarProduto(@PathVariable Long id) {
        return produtoService.buscarProduto(id);
    }

    @PutMapping("/produtos/{id}")
    public ProdutoResponse atualizarProduto(@PathVariable Long id, @Valid @RequestBody ProdutoRequest request) {
        return produtoService.atualizarProduto(id, request);
    }

    @DeleteMapping("/produtos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluirProduto(@PathVariable Long id) {
        produtoService.excluirProduto(id);
    }
    @PostMapping("/produtos/{id}/entrada")
    public ProdutoResponse registrarEntrada(@PathVariable Long id,
                                            @Valid @RequestBody MovimentacaoEstoqueRequest request) {
        return produtoService.registrarEntrada(id, request);
    }

    @PostMapping("/produtos/{id}/saida")
    public ProdutoResponse registrarSaida(@PathVariable Long id,
                                          @Valid @RequestBody MovimentacaoEstoqueRequest request) {
        return produtoService.registrarSaida(id, request);
    }
    @GetMapping("/produtos/{id}/movimentacoes")
    public List<MovimentacaoResponse> listarMovimentacoes(@PathVariable Long id) {
        return produtoService.listarMovimentacoes(id);
    }
}