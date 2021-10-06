package com.devsuperior.dscatalog.dto;

import com.devsuperior.dscatalog.entities.Category;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

public class CategoryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    @NotBlank(message = "Campo obrigatório")
    private String name;

    public CategoryDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public CategoryDTO(Category entity) {
        this.id = entity.getId();
        this.name = entity.getName();
    }

    public CategoryDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category toModel() {
        return new Category(this.getId(), this.getName());
    }

}
