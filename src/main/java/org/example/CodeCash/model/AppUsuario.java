package org.example.CodeCash.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.codec.digest.DigestUtils;
import org.openxava.annotations.*;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;

@Entity
@Getter
@Setter
@Table(name = "app_usuario", schema = "public")
public class AppUsuario extends BaseEntity {

    @Required
    @Column(length = 100, nullable = false, unique = true)
    @SearchKey
    private String usuario;

    @Hidden
    @Column(length = 255)
    private String password;

    @Transient
    @Stereotype("PASSWORD")
    private String nuevaPassword;

    @Transient
    @Stereotype("PASSWORD")
    private String confirmarPassword;

    @AssertTrue(message = "Las contraseñas no coinciden")
    private boolean isPasswordsMatch() {
        if (password == null || password.isEmpty()) {
            if (nuevaPassword == null || nuevaPassword.isEmpty()) {
                return false;
            }
        }

        if ((nuevaPassword == null || nuevaPassword.isEmpty()) &&
                (confirmarPassword == null || confirmarPassword.isEmpty())) {
            return true;
        }

        if (nuevaPassword == null || confirmarPassword == null) {
            return false;
        }

        return nuevaPassword.equals(confirmarPassword);
    }

    @PrePersist
    @PreUpdate
    private void savePassword() {
        if (nuevaPassword != null && !nuevaPassword.isEmpty()) {
            this.password = nuevaPassword;
        }
    }

}