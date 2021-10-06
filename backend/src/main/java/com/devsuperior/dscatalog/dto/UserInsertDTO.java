package com.devsuperior.dscatalog.dto;

import javax.validation.constraints.NotBlank;

public class UserInsertDTO extends UserDTO{

    @NotBlank(message = "Campo obrigatório")
    private String password;

    UserInsertDTO() {
        super();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
