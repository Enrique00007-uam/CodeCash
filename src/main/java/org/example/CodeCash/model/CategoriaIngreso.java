package org.example.CodeCash.model;

import lombok.Getter;
import lombok.Setter;
import org.openxava.annotations.Required;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Table(name = "categoria_ingreso", schema = "public")
public class CategoriaIngreso extends BaseEntity {

    @Column(length = 50)
    @Required
    private String nombre;

    public String getNombre() {
        return nombre;
    }
}