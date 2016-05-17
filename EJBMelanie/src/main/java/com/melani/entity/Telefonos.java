package com.melani.entity;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "TELEFONOS")
@NamedQueries({
    @NamedQuery(name = "Telefonos.findAll", query = "SELECT t FROM Telefonos t"),
    @NamedQuery(name = "Telefonos.findByIdPrefijo", query = "SELECT t FROM Telefonos t WHERE t.telefonosPK.idPrefijo = :idPrefijo"),
    @NamedQuery(name = "Telefonos.findByNumero", query = "SELECT t FROM Telefonos t WHERE t.telefonosPK.numero = :numero"),
    @NamedQuery(name = "Telefonos.addByCodeAndNumber",query = "SELECT t FROM Telefonos t WHERE t.telefonosPK.idPrefijo = :idPrefijo and " +
                                            "t.telefonosPK.numero = :numero")})
    public class Telefonos implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TelefonosPK telefonosPK;
    @JoinColumn(name = "ID_EMPRESATELEFONIA", referencedColumnName = "ID_EMP_TELEFONIA", nullable = false)
    @ManyToOne(optional = false,fetch=FetchType.LAZY)
    private EmpresaTelefonia idEmpresatelefonia;
    @JoinColumn(name = "ID_TIPOTELEFONO", referencedColumnName = "ID_TIPOTEL")
    @ManyToOne(fetch=FetchType.LAZY)
    private Tipostelefono idTipotelefono;
     @OneToMany(mappedBy = "telefonos",fetch = FetchType.LAZY)
    private List<Personastelefonos> personastelefonosCollection;

    public Telefonos() {
    }

    public Telefonos(TelefonosPK telefonosPK) {
        this.telefonosPK = telefonosPK;
    }

    public Telefonos(Integer numero, long idPrefijo) {
        this.telefonosPK = new TelefonosPK(numero, idPrefijo);
    }

    public TelefonosPK getTelefonosPK() {
        return telefonosPK;
    }

    
    public void setTelefonosPK(TelefonosPK telefonosPK) {
        this.telefonosPK = telefonosPK;
    }

    public EmpresaTelefonia getIdEmpresatelefonia() {
        return idEmpresatelefonia;
    }

    public void setIdEmpresatelefonia(EmpresaTelefonia idEmpresatelefonia) {
        this.idEmpresatelefonia = idEmpresatelefonia;
    }

    public Tipostelefono getIdTipotelefono() {
        return idTipotelefono;
    }

    
    public void setIdTipotelefono(Tipostelefono idTipotelefono) {
        this.idTipotelefono = idTipotelefono;
    }

    public List<Personastelefonos> getPersonastelefonosCollection() {
        return Collections.unmodifiableList(personastelefonosCollection);
    }

    public void setPersonastelefonosCollection(List<Personastelefonos> personastelefonosCollection) {
        this.personastelefonosCollection = personastelefonosCollection;
    }
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (telefonosPK != null ? telefonosPK.hashCode() : 0);
        return hash;
    }
    @Override
    public boolean equals(Object object) {        
        if (!(object instanceof Telefonos)) {
            return false;
        }
        Telefonos other = (Telefonos) object;
        return (this.telefonosPK != null || other.telefonosPK == null) && (this.telefonosPK == null || this.telefonosPK.equals(other.telefonosPK));
    }
    @Override
    public String toString() {
        return "com.tarjetadata.dao.entidades.Telefonos[telefonosPK=" + telefonosPK + "]";
    }

    public String toXML() {
        String xml = "<telefono>\n<idempresatelefonia>" + this.getIdEmpresatelefonia().getidEmpTelefonia() + "</idempresatelefonia>\n" + "<descripcion>" + this.getIdEmpresatelefonia().getNombre() + "</descripcion>\n" + "<prefijo>" + this.getTelefonosPK().getIdPrefijo() + "</prefijo>\n" + "<nrotelefono>" + this.getTelefonosPK().getNumero() + "</nrotelefono>\n" + "<tipotelefono>" + this.getIdTipotelefono().getDescripcion() + "</tipotelefono>\n" + "<idtipotel>" + this.getIdTipotelefono().getIdTipotel() + "</idtipotel>\n" + "</telefono>\n";
        return xml;
    }
}
