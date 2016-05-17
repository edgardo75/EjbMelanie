package com.melani.entity;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name="HISTORICODATOSCLIENTE")
public class HistoricoDatosClientes implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @TableGenerator(name="HistCliIdGen", table="ID_GEN_HCL",
    pkColumnName="FNAME",pkColumnValue="HistoricoDatosClientes", valueColumnName="FKEY",
    allocationSize=1,initialValue=1)
    @GeneratedValue(generator="HistCliIdGen",strategy=GenerationType.TABLE)
    private long id;
    @Column(name="ID_CLI",nullable=false)
    private long idCliente;
    @Column(name="EMAIL",columnDefinition="VARCHAR(50)")
    private String email;
    @Basic(fetch = FetchType.LAZY)
    @Column(name="OBSERVACIONES",columnDefinition="VARCHAR(32000)")
    private String Observaciones;
    @Column(name="IDGENERO")
    private short idgenero;
    @Column(name="NOMBRE",columnDefinition="VARCHAR(30)")
    private String nombre;
    @Column(name="APELLIDO",columnDefinition="VARCHAR(30)")
    private String apellido;
    @Column(precision = 15,scale=3,name="TOTALCOMPRAS")
    private double totalCompras;
    @Column(precision = 15,name = "TOTALPUNTOS")
    private int totalEnPuntos;
    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private Clientes cliente;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getObservaciones() {
        return Observaciones;
    }

    public void setObservaciones(String Observaciones) {
        this.Observaciones = Observaciones;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(long idCliente) {
        this.idCliente = idCliente;
    }

    public short getIdgenero() {
        return idgenero;
    }

    public void setIdgenero(short idgenero) {
        this.idgenero = idgenero;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getTotalCompras() {
        return totalCompras;
    }

    public void setTotalCompras(double totalCompras) {
        this.totalCompras = totalCompras;
    }

    public int getTotalEnPuntos() {
        return totalEnPuntos;
    }

    public void setTotalEnPuntos(int totalEnPuntos) {
        this.totalEnPuntos = totalEnPuntos;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

    public Clientes getCliente() {
        return cliente;
    }

    public void setCliente(Clientes cliente) {
        this.cliente = cliente;
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
        final HistoricoDatosClientes other = (HistoricoDatosClientes) obj;
        return this.id == other.id;
    }
    
    @Override
    public String toString() {
        return "com.melani.entity.HistoricoDatosClientes[id=" + id + "]";
    }
}