package com.atlas.atlas.service;

import com.atlas.atlas.dto.CategoriaRequest;
import com.atlas.atlas.dto.CategoriaResponse;
import com.atlas.atlas.model.Categoria;
import com.atlas.atlas.repository.CategoriaRepository;
import com.atlas.atlas.repository.ProdutoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProdutoRepository produtoRepository;

    public CategoriaService(CategoriaRepository categoriaRepository,
                            ProdutoRepository produtoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.produtoRepository = produtoRepository;
    }

    public List<CategoriaResponse> listarCategorias() {
        return categoriaRepository.findAll().stream()
                .map(CategoriaResponse::from)
                .toList();
    }

    public CategoriaResponse buscarCategoria(Long id) {
        return CategoriaResponse.from(buscarEntidade(id));
    }

    public CategoriaResponse cadastrarCategoria(CategoriaRequest request) {
        if (categoriaRepository.existsByNomeIgnoreCase(request.nome())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Já existe uma categoria com esse nome");
        }
        Categoria categoria = new Categoria(request.nome());
        return CategoriaResponse.from(categoriaRepository.save(categoria));
    }

    public CategoriaResponse atualizarCategoria(Long id, CategoriaRequest request) {
        Categoria categoria = buscarEntidade(id);

        if (categoriaRepository.existsByNomeIgnoreCaseAndIdNot(request.nome(), id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Já existe uma categoria com esse nome");
        }

        categoria.setNome(request.nome());
        return CategoriaResponse.from(categoriaRepository.save(categoria));
    }

    public void excluirCategoria(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Categoria não encontrada");
        }
        if (produtoRepository.existsByCategoriaId(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Categoria possui produtos vinculados e não pode ser excluída");
        }
        categoriaRepository.deleteById(id);
    }

    private Categoria buscarEntidade(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Categoria não encontrada"));
    }
}