package com.melani.entity;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PersonasdomiciliosPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "IDDOMICILIO", nullable = false)
    private long iddomicilio;

    @Basic(optional = false)
    @Column(name = "ID_PERSONA",precision = 16)
    protected long idPersona;

    public PersonasdomiciliosPK(){}

    public PersonasdomiciliosPK(long idDomicilio,long idPersona){
        this.iddomicilio=idDomicilio;
        this.idPersona = idPersona;
    }

    public long getId() {
        return iddomicilio;
    }

    public void setId(long idDomicilio) {
        this.iddomicilio = idDomicilio;
    }

    public long getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(long idPersona) {
        this.idPersona = idPersona;
    }
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (long) iddomicilio;
        hash += (long) idPersona;
        return hash;
    }
    @Override
    public boolean equals(Object object) {
       
        if (!(object instanceof PersonasdomiciliosPK)) {
            return false;
        }
        PersonasdomiciliosPK other = (PersonasdomiciliosPK) object;
        if (!Objects.equals(this.iddomicilio, other.iddomicilio)) {
            return false;
        }
        return Objects.equals(this.idPersona, other.idPersona);
    }
    @Override
    public String toString() {
        return "entity.PersonasdomiciliosPK[iddomicilio=" + iddomicilio + ", idPersona=" + idPersona + "]";
    }
}