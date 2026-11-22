package com.atlas.atlas.service;

import com.atlas.atlas.dto.MovimentacaoEstoqueRequest;
import com.atlas.atlas.dto.ProdutoResponse;
import com.atlas.atlas.model.Produto;
import com.atlas.atlas.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.atlas.atlas.repository.MovimentacaoRepository;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private MovimentacaoRepository movimentacaoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    private Produto produto;

    @BeforeEach
    void setUp() {
        produto = new Produto(1L, "Dipirona", new BigDecimal("3.49"), 20);
    }

    @Test
    void registrarEntradaDeveSomarAoEstoque() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenAnswer(inv -> inv.getArgument(0));

        ProdutoResponse resposta = produtoService.registrarEntrada(1L, new MovimentacaoEstoqueRequest(10));

        assertEquals(30, resposta.estoque());
    }

    @Test
    void registrarSaidaDeveSubtrairDoEstoque() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenAnswer(inv -> inv.getArgument(0));

        ProdutoResponse resposta = produtoService.registrarSaida(1L, new MovimentacaoEstoqueRequest(5));

        assertEquals(15, resposta.estoque());
    }

    @Test
    void registrarSaidaComEstoqueInsuficienteDeveLancarConflito() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> produtoService.registrarSaida(1L, new MovimentacaoEstoqueRequest(100)));

        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
        verify(produtoRepository, never()).save(any());
    }

    @Test
    void buscarProdutoInexistenteDeveLancarNaoEncontrado() {
        when(produtoRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> produtoService.buscarProduto(999L));

        assertEquals(HttpStatus.NOT_FOUND, erro.getStatusCode());
    }
}