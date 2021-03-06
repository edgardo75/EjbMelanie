/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
/**
 * A Entity Telefonos
 *@version 1.0
 * @author Edgardo Alvarez
 */
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

    /**
     *
     */
    @EmbeddedId
    protected TelefonosPK telefonosPK;
    @JoinColumn(name = "ID_EMPRESATELEFONIA", referencedColumnName = "ID_EMP_TELEFONIA", nullable = false)
    @ManyToOne(optional = false,fetch=FetchType.LAZY)
    private EmpresaTelefonia idEmpresatelefonia;
    @JoinColumn(name = "ID_TIPOTELEFONO", referencedColumnName = "ID_TIPOTEL")
    @ManyToOne(fetch=FetchType.LAZY)
    private Tipostelefono idTipotelefono;
     @OneToMany(mappedBy = "telefonos")
    private List<Personastelefonos> personastelefonosCollection;

    /**
     *
     */
    public Telefonos() {
    }

    /**
     *
     * @param telefonosPK
     */
    public Telefonos(TelefonosPK telefonosPK) {
        this.telefonosPK = telefonosPK;
    }

    /**
     *
     * @param numero
     * @param idPrefijo
     */
    public Telefonos(Integer numero, long idPrefijo) {
        this.telefonosPK = new TelefonosPK(numero, idPrefijo);
    }

    /**
     *
     * @return
     */
    public TelefonosPK getTelefonosPK() {
        return telefonosPK;
    }

    /**
     *
     * @param telefonosPK
     */
    public void setTelefonosPK(TelefonosPK telefonosPK) {
        this.telefonosPK = telefonosPK;
    }

    /**
     *
     * @return
     */
    public EmpresaTelefonia getIdEmpresatelefonia() {
        return idEmpresatelefonia;
    }

    /**
     *
     * @param idEmpresatelefonia
     */
    public void setIdEmpresatelefonia(EmpresaTelefonia idEmpresatelefonia) {
        this.idEmpresatelefonia = idEmpresatelefonia;
    }

    /**
     *
     * @return
     */
    public Tipostelefono getIdTipotelefono() {
        return idTipotelefono;
    }

    /**
     *
     * @param idTipotelefono
     */
    public void setIdTipotelefono(Tipostelefono idTipotelefono) {
        this.idTipotelefono = idTipotelefono;
    }

    /**
     *
     * @return
     */
    public List<Personastelefonos> getPersonastelefonosCollection() {
        return Collections.unmodifiableList(personastelefonosCollection);
    }

    /**
     *
     * @param personastelefonosCollection
     */
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
        // TODO: Warning - this method won't work in the case the id fields are not set
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

    /**
     *
     * @return
     */
    public String toXML() {
        StringBuilder xml = new StringBuilder("<telefono>\n");
                        xml.append("<idempresatelefonia>").append(this.getIdEmpresatelefonia().getidEmpTelefonia()).append("</idempresatelefonia>\n").append("<descripcion>").append(this.getIdEmpresatelefonia().getNombre()).append(
                                "</descripcion>\n");
                            xml.append("<prefijo>").append(this.getTelefonosPK().getIdPrefijo()).append("</prefijo>\n");
                            xml.append("<nrotelefono>").append(this.getTelefonosPK().getNumero()).append("</nrotelefono>\n");
                            xml.append("<tipotelefono>").append(this.getIdTipotelefono().getDescripcion()).append("</tipotelefono>\n");
                            xml.append("<idtipotel>").append(this.getIdTipotelefono().getIdTipotel()).append("</idtipotel>\n");
                      xml.append("</telefono>\n");
        return xml.toString();
    }
}
