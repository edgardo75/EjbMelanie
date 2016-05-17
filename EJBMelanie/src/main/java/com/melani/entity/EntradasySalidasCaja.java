package com.melani.entity;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity(name="EntradasySalidasCaja")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "EntradasySalidasCaja.findAll", query = "SELECT e FROM EntradasySalidasCaja e"),
    @NamedQuery(name = "EntradasySalidasCaja.findById", query = "SELECT e FROM EntradasySalidasCaja e WHERE e.id_EntradasySalidas = :id_EntradasySalidas"),
    @NamedQuery(name = "EntradasySalidasCaja.findByDetalles", query = "SELECT e FROM EntradasySalidasCaja e WHERE e.detalles = :detalles"),
    @NamedQuery(name = "EntradasySalidasCaja.findByFecha", query = "SELECT e FROM EntradasySalidasCaja e WHERE e.fecha = :fecha"),
    @NamedQuery(name = "EntradasySalidasCaja.findByEntradas", query = "SELECT e FROM EntradasySalidasCaja e WHERE e.entradas = :entradas"),
    @NamedQuery(name = "EntradasySalidasCaja.findBySalidas", query = "SELECT e FROM EntradasySalidasCaja e WHERE e.salidas = :salidas"),
    @NamedQuery(name = "EntradasySalidasCaja.findByIdUsuario", query = "SELECT e FROM EntradasySalidasCaja e WHERE e.ID_USUARIO = :ID_USUARIO"),
    @NamedQuery(name = "EntradasySalidasCaja.findByNumerocupon", query = "SELECT e FROM EntradasySalidasCaja e WHERE e.numerocupon = :numerocupon"),
    @NamedQuery(name = "EntradasySalidasCaja.findByEnefectivo", query = "SELECT e FROM EntradasySalidasCaja e WHERE e.enefectivo = :enefectivo"),
    @NamedQuery(name = "EntradasySalidasCaja.findByHora", query = "SELECT e FROM EntradasySalidasCaja e WHERE e.hora = :hora"),
    @NamedQuery(name = "EntradasySalidasCaja.findByCurrentDate", query = "SELECT e FROM EntradasySalidasCaja e WHERE e.fecha = CURRENT_DATE")})
public class EntradasySalidasCaja implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy=GenerationType.TABLE,generator="EntradasySalidasCajaIdGen")
    @TableGenerator(name="EntradasySalidasCajaIdGen", table="ID_GEN_ENTRADASYSALIDASCAJA",
    pkColumnName="FNAME",pkColumnValue="EntradasySalidasCaja", valueColumnName="FKEY",
    allocationSize=1)
    @Basic(fetch = FetchType.LAZY)
    private Integer id_EntradasySalidas;
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "DETALLES",columnDefinition = "VARCHAR(255) default'-'")
    private String detalles;
    @Column(name = "FECHA")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "ENTRADAS",columnDefinition = "DECIMAL(15, 2) default'0.00'")    
    private Double entradas;
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "SALIDAS",columnDefinition = "DECIMAL(15, 2) default'0.00'")
    private Double salidas;
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "ANTICIPO", columnDefinition = "DECIMAL(15, 2) default'0.00'")
    private double anticipo;    
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "ENTRADATARJETA", columnDefinition = "DECIMAL(15, 2) default'0.00'")
    private double entradaTarjeta;    
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "VENTASEFECTIVO", columnDefinition = "DECIMAL(15, 2) default'0.00'")
    private double ventasEfectivo; 
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "ID_USUARIO")    
    private Integer ID_USUARIO;
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "NUMEROCUPON",columnDefinition = "VARCHAR(20) default '0'")
    private String numerocupon;
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "ENEFECTIVO",columnDefinition = "CHAR(1) default '0'")
    private Character enefectivo;
    @Basic(fetch = FetchType.LAZY)        
    @Column(name = "HORA")
    @Temporal(TemporalType.TIME)
    private Date hora;
    @JoinColumn(name = "ID_TARJETA_FK", referencedColumnName = "IDTARJETA")
    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    private TarjetasCreditoDebito idTarjetaFk;

    public EntradasySalidasCaja() {
    }

    public EntradasySalidasCaja(Integer id_EntradasySalidas) {
        this.id_EntradasySalidas = id_EntradasySalidas;
    }

    public Integer getId_EntradasySalidas() {
        return id_EntradasySalidas;
    }

    public void setId_EntradasySalidas(Integer id_EntradasySalidas) {
        this.id_EntradasySalidas = id_EntradasySalidas;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Double getEntradas() {
        return entradas;
    }

    public void setEntradas(Double entradas) {
        this.entradas = entradas;
    }

    public Double getSalidas() {
        return salidas;
    }

    public void setSalidas(Double salidas) {
        this.salidas = salidas;
    }

    public double getAnticipo() {
        return anticipo;
    }

    public void setAnticipo(double anticipo) {
        this.anticipo = anticipo;
    }

    public double getEntradaTarjeta() {
        return entradaTarjeta;
    }

    public void setEntradaTarjeta(double entradaTarjeta) {
        this.entradaTarjeta = entradaTarjeta;
    }

    public double getVentasEfectivo() {
        return ventasEfectivo;
    }

    public void setVentasEfectivo(double ventasEfectivo) {
        this.ventasEfectivo = ventasEfectivo;
    }

    public Integer getID_USUARIO() {
        return ID_USUARIO;
    }

    public void setID_USUARIO(Integer ID_USUARIO) {
        this.ID_USUARIO = ID_USUARIO;
    }

    public String getNumerocupon() {
        return numerocupon;
    }

    public void setNumerocupon(String numerocupon) {
        this.numerocupon = numerocupon;
    }

    public Character getEnefectivo() {
        return enefectivo;
    }

    public void setEnefectivo(Character enefectivo) {
        this.enefectivo = enefectivo;
    }

    public Date getHora() {
        return hora;
    }

    public void setHora(Date hora) {
        this.hora = hora;
    }

    public TarjetasCreditoDebito getIdTarjetaCreditoDebitoFk() {
        return idTarjetaFk;
    }

    public void setIdTarjetaCreditoDebitoFk(TarjetasCreditoDebito idTarjetaFk) {
        this.idTarjetaFk = idTarjetaFk;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.id_EntradasySalidas);
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
        final EntradasySalidasCaja other = (EntradasySalidasCaja) obj;
        return Objects.equals(this.id_EntradasySalidas, other.id_EntradasySalidas);
    }
    public String toXML(){
    SimpleDateFormat sdf =new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat sdfh = new SimpleDateFormat("HH:mm:ss");
    StringBuilder item = new StringBuilder("<item>\n");
            item.append("<id_EntradasySalidas>").append(this.getId_EntradasySalidas()).append("</id_EntradasySalidas>\n");
            item.append("<detalles>").append(this.getDetalles()).append("</detalles>\n");
            item.append("<numerocupon>").append(this.getNumerocupon()).append( "</numerocupon>\n");
            item.append("<enefectivo>").append(this.getEnefectivo()).append( "</enefectivo>\n");
            item.append("<entradas>").append(this.getEntradas()).append( "</entradas>\n");
            item.append("<fecha>").append(sdf.format(this.getFecha())).append( "</fecha>\n");
            item.append("<hora>").append(sdfh.format(this.getHora())).append( "</hora>\n");
            item.append("<idtarjeta>").append(this.getIdTarjetaCreditoDebitoFk().getIdtarjeta()).append( "</idtarjeta>\n");
            item.append("<usuario>").append(this.getID_USUARIO()).append( "</usuario>\n");
            item.append("<salidas>").append(this.getSalidas()).append( "</salidas>\n");
            item.append("<anticipo>").append(this.getAnticipo()).append( "</anticipo>\n");
            item.append("<anticipoTarjeta>").append(this.getEntradaTarjeta()).append( "</anticipoTarjeta>\n");
            item.append( "</item>\n");
return item.toString();
}    
}
