package com.melani.entity;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
@Entity
@Table(name="PERSONAS")
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="pertype",discriminatorType=DiscriminatorType.STRING)
@NamedQueries({
    @NamedQuery(name = "Personas.findAll", query = "SELECT p FROM Personas p"),
    @NamedQuery(name = "Personas.findByIdPersona", query = "SELECT p FROM Personas p WHERE p.idPersona = :idPersona"),
    @NamedQuery(name = "Personas.findByPertype", query = "SELECT p FROM Personas p WHERE p.pertype = :pertype"),
    @NamedQuery(name = "Personas.findByObservaciones", query = "SELECT p FROM Personas p WHERE p.observaciones = :observaciones"),
    @NamedQuery(name = "Personas.findByNombre", query = "SELECT p FROM Personas p WHERE p.nombre = :nombre"),
    @NamedQuery(name = "Personas.findByNrodocumento", query = "SELECT p FROM Personas p WHERE p.nrodocumento = :nrodocumento"),
    @NamedQuery(name = "Personas.findByEmail", query = "SELECT p FROM Personas p WHERE p.email = :email"),
    @NamedQuery(name = "Personas.findByApellido", query = "SELECT p FROM Personas p WHERE p.apellido = :apellido"),
@NamedQuery(name = "Personas.deleteById",query = "DELETE FROM Personas p WHERE p.idPersona = :idPersona"),
@NamedQuery(name = "Personas.searchByEmailAndNroDocu",query = "SELECT p FROM Personas p WHERE p.email = :email and p.nrodocumento = :nrodocumento"),
@NamedQuery(name = "Personas.searchByNroDocuAndPertype",query = "SELECT p FROM Personas p WHERE p.nrodocumento = :nrodocumento and " +
                    "p.pertype = :pertype")})
public class Personas implements Serializable {
    private static final long serialVersionUID = 1L;    
    @TableGenerator(name="PersonaIdGen", table="ID_GEN_PER",
    pkColumnName="FNAME",pkColumnValue="Personas", valueColumnName="FKEY",
    allocationSize=1)
    @Id
    @GeneratedValue(generator="PersonaIdGen",strategy=GenerationType.TABLE)
    @Column(name = "ID_PERSONA")
    private long idPersona;
    @Column(name = "NOMBRE",length=30)
    private String nombre;
    @Column(name = "APELLIDO",length=30)
    private String apellido;
    @Column(name = "EMAIL",length=50, unique=true)
    private String email;
    @Column(name = "pertype",length=3)
    private String pertype;
    @Column(name="OBSERVACIONES",columnDefinition="VARCHAR(32000)")
    private String observaciones;    
    @Column(name = "NRODOCUMENTO",precision=19,unique=true,updatable=false,nullable=false)
    protected Integer nrodocumento;
    @JoinColumn(name="ID",referencedColumnName="ID",nullable=false,updatable=false)
    @ManyToOne(fetch=FetchType.LAZY,optional = false)
    private Tiposdocumento tipodocumento;    
    @OneToMany(mappedBy = "personas",fetch = FetchType.LAZY)
    private List<PersonasDomicilios> personasDomicilioss;    
    @OneToMany(mappedBy = "idPersona",fetch = FetchType.LAZY)
    private List<Personastelefonos> personastelefonoss;
    @JoinColumn(name = "ID_GENERO", referencedColumnName = "ID_GENERO")
    @ManyToOne(fetch=FetchType.LAZY,optional = false)
    private Generos generos;        

    public Personas(){
    }

    public long getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Long idPersona) {
        this.idPersona = idPersona;
    }

    public Integer getNrodocumento() {
        return nrodocumento;
    }

    public void setNrodocumento(Integer nrodocumento) {
        this.nrodocumento = nrodocumento;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getPertype() {
        return pertype;
    }

    public Tiposdocumento getTipodocumento() {
        return tipodocumento;
    }

    public void setTipodocumento(Tiposdocumento tipodocumento) {
        this.tipodocumento = tipodocumento;
    }

    public void setPertype(String pertype) {
        this.pertype = pertype;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<PersonasDomicilios> getPersonasDomicilioss() {
        return Collections.unmodifiableList(personasDomicilioss);
    }

    public void setPersonasDomicilioss(List<PersonasDomicilios> personasDomicilioss) {
        this.personasDomicilioss = personasDomicilioss;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<Personastelefonos> getPersonastelefonoss() {
        return Collections.unmodifiableList(personastelefonoss);
    }

    public void setPersonastelefonoss(List<Personastelefonos> personastelefonoss) {
        this.personastelefonoss = personastelefonoss;
    }

    public Generos getGeneros() {
        return generos;
    }

    public void setGeneros(Generos generos) {
        this.generos = generos;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (int) (this.idPersona ^ (this.idPersona >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Personas other = (Personas) obj;
        return this.idPersona == other.idPersona;
    }
    
    
    @Override
    public String toString() {
        return "entity.Personas[idPersona=" + idPersona + "]";
    }

    public String toXML(){
        StringBuilder item=new StringBuilder(32); 
        item.append("<id>").append(this.getIdPersona()).append("</id>\n");
        item.append("<apellido>").append(this.getApellido()).append("</apellido>\n");
                item.append("<nombre>").append(this.getNombre()).append("</nombre>\n");
                item.append("<idtipodocu>").append(this.getTipodocumento().getId())
                        .append("</idtipodocu>\n" ).append( "<nrodocu>").append(this.getNrodocumento())
                        .append("</nrodocu>\n").append("<observaciones>")
                        .append(this.getObservaciones())
                        .append("</observaciones>\n").append("<Genero>\n").append("<generoId>")
                        .append(this.getGeneros().getIdGenero()).append("</generoId>\n").append("<generoDescripcion>")
                        .append(this.getGeneros().getDescripcion()).append("</generoDescripcion>\n")
                        .append("</Genero>\n").append("<email>").append(this.getEmail()).append("</email>\n");
                item.append("<personadomicilio>\n");
                   if(this.getPersonasDomicilioss().isEmpty()) {
                        item.append("</personadomicilio>\n");
                    } else{
                            List<PersonasDomicilios>lista = this.getPersonasDomicilioss();
                                    for (PersonasDomicilios personasDomicilios : lista) {
                                        item.append(personasDomicilios.toXML());
                                    }
                        item.append("</personadomicilio>\n");
                    }
                item.append("<personatelefono>\n");
                    if(this.getPersonastelefonoss().isEmpty()) {
                        item.append("</personatelefono>\n");
        } else{
                        List<Personastelefonos>lista=this.getPersonastelefonoss();
            for (Personastelefonos personastelefonos : lista) {
                item.append(personastelefonos.toXML());
            }
                        item.append("</personatelefono>\n");
                    } 
         return item.toString();
    }
}