package com.devsuperior.dscatalog.dto;

import com.devsuperior.dscatalog.services.validation.UserInsertValid;

import javax.validation.constraints.NotBlank;

@UserInsertValid
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
