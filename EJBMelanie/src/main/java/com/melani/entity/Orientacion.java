
package com.melani.entity;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="ORIENTACION")
public class Orientacion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id   
    @Column(name="ID_ORIENTACION",nullable=false,precision=10)
    private Long id;
    @Column(length=10,name="DESCRIPCION",nullable=false)
    private String descripcion;
    @OneToMany(mappedBy = "idorientacion",fetch = FetchType.LAZY)
    private List<Domicilios> domicilioss;

    public Orientacion(){}

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
        if (!(object instanceof Orientacion)) {
            return false;
        }
        Orientacion other = (Orientacion) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }
    @Override
    public String toString() {
        return "com.melani.entity.Orientaciones[id=" + id + "]";
    }
}