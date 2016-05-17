package com.melani.entity;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
@Entity
@Table(name = "TIPOSTELEFONO") 
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tipostelefono.findAll", query = "SELECT t FROM Tipostelefono t"),
    @NamedQuery(name = "Tipostelefono.findByIdTipotel", query = "SELECT t FROM Tipostelefono t WHERE t.idTipotel = :idTipotel"),
    @NamedQuery(name = "Tipostelefono.findByDescripcion", query = "SELECT t FROM Tipostelefono t WHERE t.descripcion = :descripcion")})
public class Tipostelefono implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id    
    @Column(name = "ID_TIPOTEL")
    private Short idTipotel;    
    @Column(name = "DESCRIPCION")
    private String descripcion;
    @OneToMany(mappedBy = "idTipotelefono",fetch = FetchType.LAZY)
    private List<Telefonos> telefonosList;

    public Tipostelefono() {
    }

    public Tipostelefono(Short idTipotel) {
        this.idTipotel = idTipotel;
    }

    public Short getIdTipotel() {
        return idTipotel;
    }

    public void setIdTipotel(Short idTipotel) {
        this.idTipotel = idTipotel;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public List<Telefonos> getTelefonosList() {
        return Collections.unmodifiableList(telefonosList);
    }

    public void setTelefonosList(List<Telefonos> telefonosList) {
        this.telefonosList = telefonosList;
    }
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idTipotel != null ? idTipotel.hashCode() : 0);
        return hash;
    }
    @Override
    public boolean equals(Object object) {       
        if (!(object instanceof Tipostelefono)) {
            return false;
        }
        Tipostelefono other = (Tipostelefono) object;
        return (this.idTipotel != null || other.idTipotel == null) && (this.idTipotel == null || this.idTipotel.equals(other.idTipotel));
    }
    @Override
    public String toString() {
        return "com.melani.entity.Tipostelefono[ idTipotel=" + idTipotel + " ]";
    }

    public String toXML(){
        String item = "<item>\n<id>" + this.getIdTipotel() + "</id>\n" + "<nombre>" + this.getDescripcion() + "</nombre>\n" + "</item>\n";
         return item;
    }
}
