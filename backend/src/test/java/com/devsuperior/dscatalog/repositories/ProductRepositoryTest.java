package com.devsuperior.dscatalog.repositories;

import com.devsuperior.dscatalog.Factory;
import com.devsuperior.dscatalog.entities.Product;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/*
@DataJpaTest: Carrega somente os componentes relacionados ao Spring Data JPA.
Cada teste é transacional e dá rollback ao final.
(teste de unidade: repository)
 */
@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    ProductRepository repository;
    private long existingId;
    private long nonExistingId;
    private long countTotalProducts;

    //Obs: para cada teste é instanciado uma nova classe, para @BeforeAll e @AfterAll usar como métodos estáticos
    @BeforeAll //Roda antes de todos os testes
    static void setUpBeforeClass() { }
    @AfterAll //Roda depois de todos os testes
    static void tearDownAfterClass() { }

    @BeforeEach //Roda antes de cada teste
    void setUp() {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 25L;
    }
    @AfterEach //Roda depois de cada teste
    void tearDown() { }

    @Test
    void saveShouldPersistWithAutoincrementWhenIdIsNull() {

        Product product = Factory.createProduct();
        product.setId(null);

        product = repository.save(product);

        assertNotNull(product.getId());
        assertEquals(countTotalProducts + 1, product.getId());
    }

    @Test
    void FindShouldFindOptionalWhenIdExist() {
        Optional<Product> result = repository.findById(existingId);

        assertTrue(result.isPresent());
        assertEquals(existingId, result.get().getId());
    }

    @Test
    void findShouldFindOptionalEmptyWhenIdDoesNotExist() {

        Optional<Product> result = repository.findById(nonExistingId);

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteShouldDeleteObjectWhenIdExists() {

        repository.deleteById(existingId);
        Optional<Product> result = repository.findById(existingId);

        assertFalse(result.isPresent());
    }

    @Test
    void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExist() {

        assertThrows(EmptyResultDataAccessException.class, () -> {
            repository.deleteById(nonExistingId);
        });
    }

}