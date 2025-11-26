package org.example.CodeCash.model;

import lombok.Getter;
import lombok.Setter;
import org.openxava.annotations.Required;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
public class CategoriaIngreso extends BaseEntity {

    @Column(length = 50)
    @Required
    private String nombre;
}