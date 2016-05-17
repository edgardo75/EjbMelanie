package com.melani.entity;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Entity
@Table(name="BARRIOS")
@NamedQueries({
    @NamedQuery(name = "Barrios.findAll", query = "SELECT b FROM Barrios b"),
    @NamedQuery(name = "Barrios.findByIdBarrio", query = "SELECT b FROM Barrios b WHERE b.id = :id"),
    @NamedQuery(name = "Barrios.findByDescripcion", query = "SELECT b FROM Barrios b WHERE b.descripcion = :descripcion"),
    @NamedQuery(name = "Barrios.findByDescripcionByLike",query = "SELECT b FROM Barrios b WHERE LOWER(b.descripcion) LIKE LOWER(?1)")
})
public class Barrios implements Serializable {
    private static final long serialVersionUID = 1L;    
    @Id
    @Column(name="ID_BARRIO")
    @GeneratedValue(strategy=GenerationType.TABLE,generator="BarrioIdGen")
    @TableGenerator(name="BarrioIdGen", table="ID_GEN_BARRIO",
    pkColumnName="FNAME",pkColumnValue="Barrios", valueColumnName="FKEY",
    allocationSize=1)
    private Long id;
    @Column(length = 100,name="DESCRIPCION",nullable = false,unique=true)    
    @NotNull(message = "El nombre del Barrio es requerido")
    @Pattern(message = "El nombre de Barrio no es válido",regexp = "(?=^.{3,100}$)^([\\w\\.\\p{IsLatin}][\\s]?)+$")
    private String descripcion;    
    @OneToMany(mappedBy = "idbarrio",fetch = FetchType.LAZY)
    private List<Domicilios> domicilioss;

    public Barrios(){}

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<Domicilios> getDomicilioss() {
        return Collections.unmodifiableList(domicilioss);
    }

    public void setDomicilioss(List<Domicilios> domicilioss) {
        this.domicilioss = domicilioss;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }
    
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Barrios)) {
            return false;
        }
        Barrios other = (Barrios) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }
    
    @Override
    public String toString() {
        return "com.melani.entity.Barrios[id=" + id + "]";
    }

    public String toXML() throws UnsupportedEncodingException{
        StringBuilder item = new StringBuilder("<item>\n");
                item.append("<id>").append(this.getId()).append("</id>\n");
                item.append("<nombre>").append(this.getDescripcion()).append("</nombre>\n");
                item.append("</item>\n");
        return item.toString();
    }
    
}
