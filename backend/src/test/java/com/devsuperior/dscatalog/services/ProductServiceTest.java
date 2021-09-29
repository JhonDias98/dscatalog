package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.Factory;
import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/*
@ExtendWith: não carrega o contexto, mas permite usar os recursos do Spring com JUnit (teste de unidade: service/component)
@SpringBootTest: carrega o contexto da aplicação (teste de integração)
@SpringBootTest + @AutoConfigureMockMvc: carrega o contexto da aplicação (teste de integração & web) e trata as requisições sem subir o servidor
@WebMvcTest(Classe.class): carrega o contexto, porém somente da camada web (teste de unidade: controlador)

Quando a classe utilizar @ExtendWith, usar @Mock ou Classe classe = Mockito.mock(Classe.class) para instanciar os objetos, pois a classe não vai carregar o contexto da aplicação
Quando a classe utilizar @SpringBootTest ou @WebMvcTest, usar @MockBean para instanciar os objetos para mockar os bean do sistema.
 */

@ExtendWith(SpringExtension.class)
class ProductServiceTest {

    @InjectMocks //Cria uma instância da classe e injeta as informações criadas com as anotações @Mock
    private ProductService service;

    @Mock
    private ProductRepository repository;
    @Mock
    private CategoryRepository categoryRepository;

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    //PageImpl: é um tipo concreto utilizado mais para testes
    private PageImpl<Product> page;
    private Product product;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 1000L;
        dependentId = 5L;
        product = Factory.createProduct();
        page = new PageImpl<>(List.of(product));
        productDTO = new ProductDTO(null, "Smart TV", "Lorem ipsum", 2190.0, "https://img.com", Instant.parse("2021-09-28T10:00:00Z"));

        //FindALl
        //Quando o método findAll do repository for executado, recebendo qualquer argumento, retornar o page que foi instanciado
        Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);

        //FindById
        //Quando o método findById do repository for executado, com um ID existente, vai retornar um Optional contendo o produto
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
        //Quando o método findById for executado, com um ID inexistente, vai retornar um Optional vazio
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        //Update
        //Quando o método getOne do repository for executado, com um ID existente, vai retornar o produto que foi instanciado
        Mockito.when(repository.getOne(existingId)).thenReturn(product);
        //Quando o método getOne do repository for executado, com um ID existente, vai ser lançada a exception EntityNotFoundException
        Mockito.when(repository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);

        //Save
        //Quando o método save do repository for executado, recebendo qualquer argumento, vai retornar o produto que foi instanciado
        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);

        //Delete
        //Quando o método deleteById do repository for executado, com um ID existente, não é para retornar nada
        Mockito.doNothing().when(repository).deleteById(existingId);
        //Quando o método deleteById do repository for executado, com um ID inexistente, vai ser lançada a exception EmptyResultDataAccessException
        Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
        //Quando o método deleteById do repository for executado, com um ID existente que contem relacionamento, vai ser lançada a exception DataIntegrityViolationException
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
    }

    @Test
    void findAllPagedShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductDTO> result = service.findAllPaged(pageable);

        assertNotNull(result);
        Mockito.verify(repository).findAll(pageable);
    }

    @Test
    void deleteShouldDoNothingWhenIdExist() {

        assertDoesNotThrow(() ->  service.delete(existingId));

        //Verifica se o método deleteById do repository foi executado apenas uma vez
        Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
        //Verifica se o método deleteAll do repository nunca foi chamado
        Mockito.verify(repository, Mockito.never()).deleteAll();
    }

    @Test
    void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistingId));

        Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId);
    }

    @Test
    void deleteShouldThrowDatabaseExceptionWhenIdDoesNotExist() {

        assertThrows(DatabaseException.class, () -> service.delete(dependentId));

        Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
    }

    /*
    Implementar os seguintes testes em ProductServiceTest:
    findById deveria
        retornar um ProductDTO quando o id existir
        lançar ResourceNotFoundException quando o id não existir
    update deveria (dica: você vai ter que simular o comportamento do getOne)
        retornar um ProductDTO quando o id existir
        lançar uma ResourceNotFoundException quando o id não existir
     */

    @Test
    void findShouldFindProductWhenIdExist() {
        ProductDTO result = service.findById(existingId);

        assertEquals(existingId, result.getId());
        Mockito.verify(repository, Mockito.times(1)).findById(existingId);
    }

    @Test
    void findShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        ResourceNotFoundException result = assertThrows(ResourceNotFoundException.class, () -> service.findById(nonExistingId));

        Mockito.verify(repository, Mockito.times(1)).findById(nonExistingId);
        assertEquals("Entity not found", result.getMessage());
    }

    @Test
    void updateShouldUpdateProductWhenIdExist() {

        ProductDTO result = service.update(existingId, productDTO);

        assertEquals(existingId, result.getId());
        assertEquals(product.getName(), result.getName());
        Mockito.verify(repository, Mockito.times(1)).getOne(existingId);
        Mockito.verify(repository, Mockito.times(1)).save(ArgumentMatchers.any());
    }

    @Test
    void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        ResourceNotFoundException result = assertThrows(ResourceNotFoundException.class, () -> service.update(nonExistingId, productDTO));

        Mockito.verify(repository, Mockito.times(1)).getOne(nonExistingId);
        assertEquals("Id not found " + nonExistingId, result.getMessage());
    }
}