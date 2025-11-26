    package org.example.CodeCash.model;

    import lombok.Getter;
    import lombok.Setter;
    import org.openxava.annotations.Required;
    import javax.persistence.Column;
    import javax.persistence.Entity;

    @Getter
    @Setter
    @Entity
    public class CategoriaGasto extends BaseEntity{
        @Column(length = 50)
        @Required
        private String nombre;
    }
