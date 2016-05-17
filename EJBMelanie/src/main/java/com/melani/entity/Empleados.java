package com.melani.entity;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;

@Entity
@DiscriminatorValue("EMP")
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="emptype",discriminatorType=DiscriminatorType.STRING) 
@NamedQueries({@NamedQuery(name = "Empleados.deleteById",query = "DELETE FROM Empleados e WHERE e.idPersona = :idPersona"),
               @NamedQuery(name = "Empleados.chkpass",query ="SELECT e FROM Empleados e WHERE e.idPersona = ?1")})
public class Empleados extends Personas {

    @Column(name="PASSWORD",nullable=false,length = 100)    
    protected String password;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    protected Date fechacarga;

    @Column(name = "emptype",length = 10)
    protected String emptype;
    
    @Column(name="NAME_USER",unique=true,nullable = false)     
    private String nameuser;

    @Column(name="ESTADO")
     protected Short estado;
    
    @Column(name = "KEYPASSWORD",length = 100,nullable = false)
    protected String keyPassword;
   
    public Empleados(){}

    public Date getFechacarga() {
        return fechacarga;
    }

    public void setFechacarga(Date fechacarga) {
        this.fechacarga = fechacarga;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmptype() {
        return emptype;
    }

    public void setEmptype(String emptype) {
        this.emptype = emptype;
    }

    public String getNameuser() {
        return nameuser;
    }

    public void setNameuser(String nameuser) {
        this.nameuser = nameuser;
    }

    public Short getEstado() {
        return estado;
    }

    public void setEstado(Short estado) {
        this.estado = estado;
    }

    public String getKeyPassword() {
        return keyPassword;
    }

    public void setKeyPassword(String keyPassword) {
        this.keyPassword = keyPassword;
    }

    public String toXMLEmpleado(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fechaCarga = (this.getFechacarga()!=null)?sdf.format(this.getFechacarga()):"";
        String item = "<nameuser>"+this.getNameuser()+"</nameuser>"+"<emptype>"+this.getEmptype()+"</emptype>\n"+
                "<estado>"+this.getEstado()+"</estado>\n"+
                "<fechacarga>"+fechaCarga+"</fechacarga>\n";
        return item;
    }
}
