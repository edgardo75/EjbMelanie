package com.melani.entity;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;

@Entity
@Table
public class HistPersonasTelefonos implements Serializable {
    @Id
    @TableGenerator(name="HistPerTelIdGen", table="ID_GEN_HPT",
    pkColumnName="FNAME",pkColumnValue="HistPersonasTelefonos", valueColumnName="FKEY",
    allocationSize=1)
    @GeneratedValue(generator="HistPerTelIdGen",strategy=GenerationType.TABLE)
    @Basic(fetch = FetchType.LAZY)
    private Long idhistperdom;
    private Long numerotel;
    private Long prefijo;
    private Long idpersona;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechacambio;
    private Integer idusuario;
    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumns(value = @PrimaryKeyJoinColumn)
    private Personastelefonos personaTelefono;

    public Long getIdhistperdom() {
        return idhistperdom;
    }

    public void setIdhistperdom(Long idhistperdom) {
        this.idhistperdom = idhistperdom;
    }

    
    public Date getFechacambio() {
        return fechacambio;
    }

    public void setFechacambio(Date fechacambio) {
        this.fechacambio = fechacambio;
    }

    public Long getIdpersona() {
        return idpersona;
    }

    public void setIdpersona(Long idpersona) {
       this.idpersona = idpersona;
    }

    public Integer getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(Integer idusuario) {
        this.idusuario = idusuario;
    }

    public Long getNumerotel() {
        return numerotel;
    }

    public void setNumerotel(Long numerotel) {
        this.numerotel = numerotel;
    }

    public Long getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(Long prefijo) {
        this.prefijo = prefijo;
    }

    public Personastelefonos getPersonaTelefono() {
        return personaTelefono;
    }

    public void setPersonaTelefono(Personastelefonos personaTelefono) {
        this.personaTelefono = personaTelefono;
    }

    public String toXML(){
        StringBuilder item = new StringBuilder("<item>\n");
                item.append("<id>").append(this.getIdhistperdom()).append("</id>\n");
                item.append("<prefijo>").append(this.getPrefijo()).append("</prefijo>\n");
                item.append("<numerotel>").append(this.getNumerotel()).append("</numerotel>\n");
                item.append("<fecha>").append(this.getFechacambio()).append("</fecha>\n");
                item.append("<idusuario>").append(this.getIdusuario()).append("</idusuario>\n");
               item.append("</item>\n");
        return item.toString();
    }
}
