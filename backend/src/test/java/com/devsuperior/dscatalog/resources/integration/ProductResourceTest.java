package com.devsuperior.dscatalog.resources.integration;

import com.devsuperior.dscatalog.Factory;
import com.devsuperior.dscatalog.dto.ProductDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductResourceTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private Long existingId;
    private Long nonExistingId;
    private Long countTotalProducts;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 25L;
    }

    @Test
    void findAllShouldReturnSortedPageWhenSortByName() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                    .get("/products?page=0&size=12&sort=name,asc")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(countTotalProducts))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value("Macbook Pro"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].name").value("PC Gamer"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[2].name").value("PC Gamer Alfa"));
    }

    @Test
    void updateShouldReturnProductDTOWhenIdExists() throws Exception {

        ProductDTO productDTO = Factory.createProductDto();
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        String expectedName = productDTO.getName();
        String expectedDescription = productDTO.getDescription();

        mockMvc.perform(MockMvcRequestBuilders
                .put("/products/{id}", existingId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(existingId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(expectedName))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(expectedDescription));
    }

    @Test
    void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {

        ProductDTO productDTO = Factory.createProductDto();
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        mockMvc.perform(MockMvcRequestBuilders
                .put("/products/{id}", nonExistingId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

}