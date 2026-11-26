package com.atlas.atlas.controller;

import com.atlas.atlas.dto.MovimentacaoEstoqueRequest;
import com.atlas.atlas.dto.ProdutoRequest;
import com.atlas.atlas.dto.ProdutoResponse;
import com.atlas.atlas.service.ProdutoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProdutoController.class)
class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProdutoService produtoService;

    private final ProdutoResponse dipirona =
            new ProdutoResponse(4L, "Dipirona", new BigDecimal("3.49"), 20, null);

    @Test
    void buscarProdutoDeveRetornar200ComOProduto() throws Exception {
        when(produtoService.buscarProduto(4L)).thenReturn(dipirona);

        mockMvc.perform(get("/produtos/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.nome").value("Dipirona"))
                .andExpect(jsonPath("$.estoque").value(20));
    }

    @Test
    void buscarProdutoInexistenteDeveRetornar404ComMensagem() throws Exception {
        when(produtoService.buscarProduto(999L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));

        mockMvc.perform(get("/produtos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Produto não encontrado"));
    }

    @Test
    void listarProdutosPorCategoriaDeveRepassarOFiltro() throws Exception {
        when(produtoService.listarProdutos(2L)).thenReturn(List.of(dipirona));

        mockMvc.perform(get("/produtos").param("categoriaId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nome").value("Dipirona"));

        verify(produtoService).listarProdutos(2L);
    }

    @Test
    void cadastrarProdutoValidoDeveRetornar201() throws Exception {
        when(produtoService.cadastrarProduto(any(ProdutoRequest.class))).thenReturn(dipirona);

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "nome": "Dipirona", "preco": 3.49, "estoque": 20 }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4));
    }

    @Test
    void cadastrarProdutoSemPrecoDeveRetornar400ComOCampo() throws Exception {
        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "nome": "Dipirona", "estoque": 20 }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.preco").exists());

        verify(produtoService, never()).cadastrarProduto(any());
    }

    @Test
    void registrarSaidaComQuantidadeZeroDeveRetornar400() throws Exception {
        mockMvc.perform(post("/produtos/4/saida")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "quantidade": 0 }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.quantidade").exists());

        verify(produtoService, never()).registrarSaida(any(), any());
    }

    @Test
    void registrarSaidaComEstoqueInsuficienteDeveRetornar409() throws Exception {
        when(produtoService.registrarSaida(eq(4L), any(MovimentacaoEstoqueRequest.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Estoque insuficiente. Disponível: 20"));

        mockMvc.perform(post("/produtos/4/saida")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "quantidade": 100 }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro").value("Estoque insuficiente. Disponível: 20"));
    }

    @Test
    void excluirProdutoDeveRetornar204() throws Exception {
        mockMvc.perform(delete("/produtos/4"))
                .andExpect(status().isNoContent());

        verify(produtoService).excluirProduto(4L);
    }
}
