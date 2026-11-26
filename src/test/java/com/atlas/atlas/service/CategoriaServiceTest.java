package com.atlas.atlas.service;

import com.atlas.atlas.dto.CategoriaRequest;
import com.atlas.atlas.dto.CategoriaResponse;
import com.atlas.atlas.model.Categoria;
import com.atlas.atlas.repository.CategoriaRepository;
import com.atlas.atlas.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    @Test
    void cadastrarCategoriaDeveSalvar() {
        when(categoriaRepository.existsByNomeIgnoreCase("Medicamentos")).thenReturn(false);
        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(inv -> inv.getArgument(0));

        CategoriaResponse resposta = categoriaService.cadastrarCategoria(new CategoriaRequest("Medicamentos"));

        assertEquals("Medicamentos", resposta.nome());
    }

    @Test
    void cadastrarCategoriaComNomeDuplicadoDeveLancarConflito() {
        when(categoriaRepository.existsByNomeIgnoreCase("Medicamentos")).thenReturn(true);

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> categoriaService.cadastrarCategoria(new CategoriaRequest("Medicamentos")));

        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void atualizarCategoriaMantendoOMesmoNomeDevePermitir() {
        Categoria categoria = new Categoria("Medicamentos");
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.existsByNomeIgnoreCaseAndIdNot("Medicamentos", 1L)).thenReturn(false);
        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(inv -> inv.getArgument(0));

        CategoriaResponse resposta = categoriaService.atualizarCategoria(1L, new CategoriaRequest("Medicamentos"));

        assertEquals("Medicamentos", resposta.nome());
    }

    @Test
    void atualizarCategoriaComNomeDeOutraCategoriaDeveLancarConflito() {
        Categoria categoria = new Categoria("Higiene");
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.existsByNomeIgnoreCaseAndIdNot("Medicamentos", 1L)).thenReturn(true);

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> categoriaService.atualizarCategoria(1L, new CategoriaRequest("Medicamentos")));

        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
        assertEquals("Higiene", categoria.getNome());
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void excluirCategoriaSemProdutosDeveExcluir() {
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        when(produtoRepository.existsByCategoriaId(1L)).thenReturn(false);

        categoriaService.excluirCategoria(1L);

        verify(categoriaRepository).deleteById(1L);
    }

    @Test
    void excluirCategoriaComProdutosDeveLancarConflito() {
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        when(produtoRepository.existsByCategoriaId(1L)).thenReturn(true);

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> categoriaService.excluirCategoria(1L));

        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
        verify(categoriaRepository, never()).deleteById(any());
    }

    @Test
    void excluirCategoriaInexistenteDeveLancarNaoEncontrado() {
        when(categoriaRepository.existsById(999L)).thenReturn(false);

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> categoriaService.excluirCategoria(999L));

        assertEquals(HttpStatus.NOT_FOUND, erro.getStatusCode());
        verify(categoriaRepository, never()).deleteById(any());
    }

    @Test
    void buscarCategoriaInexistenteDeveLancarNaoEncontrado() {
        when(categoriaRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> categoriaService.buscarCategoria(999L));

        assertEquals(HttpStatus.NOT_FOUND, erro.getStatusCode());
    }
}