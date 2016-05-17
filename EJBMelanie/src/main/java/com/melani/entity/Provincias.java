package com.melani.entity;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
@Entity
@Table(name = "PROVINCIAS")
@NamedQueries({
    @NamedQuery(name = "Provincias.findAll", query = "SELECT p FROM Provincias p"),
    @NamedQuery(name = "Provincias.findByIdProvincia", query = "SELECT p FROM Provincias p WHERE p.idProvincia = :idProvincia"),
    @NamedQuery(name = "Provincias.findByProvincia", query = "SELECT p FROM Provincias p WHERE p.provincia = :provincia"),
    @NamedQuery(name = "Provincias.findByCodigo", query = "SELECT p FROM Provincias p WHERE p.codigo = :codigo")})
public class Provincias implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false,fetch = FetchType.LAZY)
    @Column(name = "ID_PROVINCIA")
    private Short idProvincia;
    @Column(name = "PROVINCIA",length=30)
    private String provincia;
    @Column(name = "CODIGO")
    private Character codigo;
    @OneToMany(orphanRemoval = true, mappedBy = "provincias",fetch = FetchType.LAZY)
    private List<Localidades> localidadesList;

    public Provincias() {
    }

    public Provincias(Short idProvincia) {
        this.idProvincia = idProvincia;
    }

    
    public Short getIdProvincia() {
        return idProvincia;
    }

    public void setIdProvincia(Short idProvincia) {
        this.idProvincia = idProvincia;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    
    public Character getCodigo() {
        return codigo;
    }

    public void setCodigo(Character codigo) {
        this.codigo = codigo;
    }

    public List<Localidades> getLocalidadesList() {
        return Collections.unmodifiableList(localidadesList);
    }

    
    public void setLocalidadesList(List<Localidades> localidadesList) {
        this.localidadesList = localidadesList;
    }
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idProvincia != null ? idProvincia.hashCode() : 0);
        return hash;
    }
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Provincias)) {
            return false;
        }
        Provincias other = (Provincias) object;
        return !((this.idProvincia == null && other.idProvincia != null) || (this.idProvincia != null && !this.idProvincia.equals(other.idProvincia)));
    }
    @Override
    public String toString() {
        return "entity.Provincias[idProvincia=" + idProvincia + "]";
    }

    public String toXML(){
    StringBuilder xml = new StringBuilder("<provincia>\n").append("<id>").append(this.getIdProvincia()).append("</id>\n");
            xml.append("<descripcion>").append(this.getProvincia()).append("</descripcion>\n");
            xml.append("<codigo>").append(this.getCodigo()).append("</codigo>\n");
            xml.append("</provincia>\n");
    return xml.toString();
    }
}
