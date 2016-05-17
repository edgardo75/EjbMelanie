package com.melani.entity;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
@Entity
@Table(name = "PERSONASDOMICILIOS")
@NamedQueries({
    @NamedQuery(name = "PersonasDomicilios.findAll", query = "SELECT p FROM PersonasDomicilios p"),
    @NamedQuery(name = "PersonasDomicilios.findByFechaingresovivienda", query = "SELECT p FROM PersonasDomicilios p WHERE p.fechaingresovivienda = :fechaingresovivienda"),
    @NamedQuery(name = "PersonasDomicilios.findByEstado", query = "SELECT p FROM PersonasDomicilios p WHERE p.estado = :estado"),
    @NamedQuery(name = "PersonasDomicilios.findByIddomicilio", query = "SELECT p FROM PersonasDomicilios p WHERE p.personasdomiciliosPK.iddomicilio = :iddomicilio"),
    @NamedQuery(name = "PersonasDomicilios.findByIdPersona", query = "SELECT p FROM PersonasDomicilios p WHERE p.personasdomiciliosPK.idPersona = :idPersona")})
public class PersonasDomicilios implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected PersonasdomiciliosPK personasdomiciliosPK;
     @Column(name = "ESTADO", length = 20)
    private String estado;
    @Column(name = "FECHAINGRESOVIVIENDA")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaingresovivienda;
    @JoinColumn(name="ID_PERSONA",referencedColumnName="ID_PERSONA",insertable=false,updatable=false)
    @ManyToOne(optional = false)
    private Personas personas;
    @JoinColumn(name = "IDDOMICILIO", referencedColumnName = "ID_DOMICILIO",insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Domicilios domicilioss;

    public PersonasDomicilios() {
    }

    public PersonasDomicilios(PersonasdomiciliosPK personasdomiciliosPK) {
        this.personasdomiciliosPK = personasdomiciliosPK;
    }

    public PersonasDomicilios(long idDomicilio,long idPersona){
        this.personasdomiciliosPK = new PersonasdomiciliosPK(idDomicilio,idPersona);
    }

    public Domicilios getDomicilioss() {
        return domicilioss;
    }

    public void setDomicilioss(Domicilios domicilioss) {
        this.domicilioss = domicilioss;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaingresovivienda() {
        return fechaingresovivienda;
    }

    public void setFechaingresovivienda(Date fechaingresovivienda) {
        this.fechaingresovivienda = fechaingresovivienda;
    }

    public Personas getPersonas() {
        return personas;
    }

    public void setPersonas(Personas personas) {
        this.personas = personas;
    }

    public PersonasdomiciliosPK getPersonasdomiciliosPK() {
        return personasdomiciliosPK;
    }

    public void setPersonasdomiciliosPK(PersonasdomiciliosPK personasdomiciliosPK) {
        this.personasdomiciliosPK = personasdomiciliosPK;
    }

    
    public String toXML(){
        String item=domicilioss.toXML();
        return item;
    }
}