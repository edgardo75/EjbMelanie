package com.melani.entity;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
@Entity
@Table(name = "HISTORICONOTAPEDIDO", catalog = "", schema = "")
@NamedQueries({@NamedQuery(name = "Historiconotapedido.findAll", query = "SELECT h FROM Historiconotapedido h"),
@NamedQuery(name = "Historiconotapedido.findByIdhistorico", query = "SELECT h FROM Historiconotapedido h WHERE h.id = :id"),
@NamedQuery(name = "Historiconotapedido.findByAnticipo", query = "SELECT h FROM Historiconotapedido h WHERE h.anticipo = :anticipo"),
@NamedQuery(name = "Historiconotapedido.findByIdusuariocancelo", query = "SELECT h FROM Historiconotapedido h WHERE h.idusuariocancelo = :idusuariocancelo"),
@NamedQuery(name = "Historiconotapedido.findByPendiente", query = "SELECT h FROM Historiconotapedido h WHERE h.pendiente = :pendiente"),
@NamedQuery(name = "Historiconotapedido.findByTotal", query = "SELECT h FROM Historiconotapedido h WHERE h.total = :total"),
@NamedQuery(name = "Historiconotapedido.findByIdusuarioanulo", query = "SELECT h FROM Historiconotapedido h WHERE h.idusuarioanulo = :idusuarioanulo"),
@NamedQuery(name = "Historiconotapedido.findBySaldo", query = "SELECT h FROM Historiconotapedido h WHERE h.saldo = :saldo"),
@NamedQuery(name = "Historiconotapedido.findByHoraregistro", query = "SELECT h FROM Historiconotapedido h WHERE h.horaregistro = :horaregistro"),
@NamedQuery(name = "Historiconotapedido.findByFecharegistro", query = "SELECT h FROM Historiconotapedido h WHERE h.fecharegistro = :fecharegistro"),
@NamedQuery(name = "Historiconotapedido.findByAccion", query = "SELECT h FROM Historiconotapedido h WHERE h.accion = :accion"),
@NamedQuery(name = "Historiconotapedido.findByPorcentajeaplicado", query = "SELECT h FROM Historiconotapedido h WHERE h.porcentajeaplicado = :porcentajeaplicado"),
@NamedQuery(name = "Historiconotapedido.findByDescuento", query = "SELECT h FROM Historiconotapedido h WHERE h.descuento = :descuento"),
@NamedQuery(name = "Historiconotapedido.findByIdusuarioexpidio", query = "SELECT h FROM Historiconotapedido h WHERE h.idusuarioexpidio = :idusuarioexpidio"),
@NamedQuery(name = "Historiconotapedido.findByTotalapagar", query = "SELECT h FROM Historiconotapedido h WHERE h.totalapagar = :totalapagar"),
@NamedQuery(name = "Historiconotapedido.findByObservaciones", query = "SELECT h FROM Historiconotapedido h WHERE h.observaciones = :observaciones"),
@NamedQuery(name = "Historiconotapedido.findByRecargo", query = "SELECT h FROM Historiconotapedido h WHERE h.recargo = :recargo"),
@NamedQuery(name = "Historiconotapedido.findByIdusuarioentrega", query = "SELECT h FROM Historiconotapedido h WHERE h.idusuarioentrega = :idusuarioentrega"),
@NamedQuery(name = "Historiconotapedido.findByPorcrecargo", query = "SELECT h FROM Historiconotapedido h WHERE h.porcrecargo = :porcrecargo"),
@NamedQuery(name = "Historiconotapedido.findByEntregado", query = "SELECT h FROM Historiconotapedido h WHERE h.entregado = :entregado"),
@NamedQuery(name = "Historiconotapedido.findByCancelado", query = "SELECT h FROM Historiconotapedido h WHERE h.cancelado = :cancelado"),
@NamedQuery(name = "Historiconotapedido.findByAnulado", query = "SELECT h FROM Historiconotapedido h WHERE h.anulado = :anulado"),
@NamedQuery(name = "Historiconotapedido.findByPorcdesc", query = "SELECT h FROM Historiconotapedido h WHERE h.porcdesc = :porcdesc"),
@NamedQuery(name = "Historiconotapedido.findByFkIdNotaPedido",query = "SELECT h FROM Historiconotapedido h WHERE h.fkidnotapedido.id = :idnota")})
public class Historiconotapedido implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false,fetch = FetchType.LAZY)
    @GeneratedValue(strategy=GenerationType.TABLE,generator="HistoriconotapedidoIdGen")
    @TableGenerator(name="HistoriconotapedidoIdGen", table="ID_GEN_HIST_NOTAP",
    pkColumnName="FNAME",pkColumnValue="Historiconotapedido" , valueColumnName="FKEY",
    allocationSize=1)
    @Column(name = "IDHISTORICO")    
    private Long id;
    @Column(name = "ANTICIPO",precision=15,scale=3)
    private double anticipo;
    @Column(name = "IDUSUARIOCANCELO")
    private Long idusuariocancelo;
    @Column(name = "PENDIENTE",length=1)
    private Character pendiente;
    @Column(name = "TOTAL",precision=15,scale=3)
    private double total;
    @Column(name = "IDUSUARIOANULO")
    private Long idusuarioanulo;
    @Column(name = "SALDO",precision=15,scale=3)
    private double saldo;
    @Column(name = "HORAREGISTRO")
    @Temporal(TemporalType.TIME)
    private Date horaregistro;
    @Column(name = "FECHAREGISTRO")
    @Temporal(TemporalType.DATE)
    private Date fecharegistro;
    @Column(name = "ACCION",length=100)
    private String accion;
    @Column(name = "PORCENTAJEAPLICADO")
    private Short porcentajeaplicado;
    @Column(name = "DESCUENTO",precision=15,scale=3)
    private double descuento;
    @Column(name = "IDUSUARIOEXPIDIO")
    private Long idusuarioexpidio;
    @Column(name = "TOTALAPAGAR",precision=15,scale=3)
    private double totalapagar;
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "OBSERVACIONES",length=32_000)
    private String observaciones;
    @Column(name = "RECARGO",precision=15,scale=3)
    private double recargo;
    @Column(name = "IDUSUARIOENTREGA")
    private Long idusuarioentrega;
    @Column(name = "PORCRECARGO",precision=15,scale=3)
    private double porcrecargo;
    @Column(name = "ENTREGADO",length=1)
    private Character entregado;
    @Column(name = "CANCELADO",length=1)
    private Character cancelado;
    @Column(name = "ANULADO",length=1)
    private Character anulado;
    @Column(name = "PORCDESC",precision=15,scale=3)
    private double porcdesc;
    @JoinColumn(name="FKIDNOTAPEDIDO_ID",referencedColumnName="ID")
    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    private Notadepedido fkidnotapedido;

    public Historiconotapedido() {
    }

    public Historiconotapedido(long id) {
        this.id = id;
    }

    public Notadepedido getFkidnotapedido() {
        return fkidnotapedido;
    }

    public void setFkidnotapedido(Notadepedido fkidnotapedido) {
        this.fkidnotapedido = fkidnotapedido;
    }

    public double getPorcentajedesc() {
        return porcdesc;
    }

    public void setPorcentajedesc(double porcentajedesc) {
        this.porcdesc = porcentajedesc;
    }

    public Short getPorcentajeaplicado() {
        return porcentajeaplicado;
    }

    public void setPorcentajeaplicado(Short porcentajeaplicado) {
        this.porcentajeaplicado = porcentajeaplicado;
    }

    public Character getPendiente() {
        return pendiente;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }   

    
    public void setPendiente(Character pendiente) {
        this.pendiente = pendiente;
    }

    public Character getEntregado() {
        return entregado;
    }

    public void setEntregado(Character entregado) {
        this.entregado = entregado;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Long getIdusuarioexpidio() {
        return idusuarioexpidio;
    }

    public void setIdusuarioexpidio(long idusuarioexpidio) {
        this.idusuarioexpidio = idusuarioexpidio;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public Long getIdusuarioentrega() {
        return idusuarioentrega;
    }

    public void setIdusuarioentrega(long idusuarioentrega) {
        this.idusuarioentrega = idusuarioentrega;
    }

    public Date getFecharegistro() {
        return fecharegistro;
    }

    public Long getIdusuariocancelo() {
        return idusuariocancelo;
    }

    public void setIdusuariocancelo(long idusuariocancelo) {
        this.idusuariocancelo = idusuariocancelo;
    }    

    public void setFecharegistro(Date fecharegistro) {
        this.fecharegistro = fecharegistro;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public double getAnticipo() {
        return anticipo;
    }

    public Date getHoraregistro() {
        return horaregistro;
    }

    public void setHoraregistro(Date horaregistro) {
        this.horaregistro = horaregistro;
    }

    public void setAnticipo(double anticipo) {
        this.anticipo = anticipo;
    }

    public Long getIdhistorico() {
        return id;
    }

    public void setIdhistorico(long id) {
        this.id = id;
    }

    public Long getIdusuarioanulo() {
        return idusuarioanulo;
    }

    public void setIdusuarioanulo(Long idusuarioanulo) {
        this.idusuarioanulo = idusuarioanulo;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public double getPorcrecargo() {
        return porcrecargo;
    }

    public void setPorcrecargo(double porcrecargo) {
        this.porcrecargo = porcrecargo;
    }

    public double getRecargo() {
        return recargo;
    }

    public void setRecargo(double recargo) {
        this.recargo = recargo;
    }

    public double getTotalapagar() {
        return totalapagar;
    }

    public void setTotalapagar(double totalapagar) {
        this.totalapagar = totalapagar;
    }

    public Character getCancelado() {
        return cancelado;
    }

    public void setCancelado(Character cancelado) {
        this.cancelado = cancelado;
    }

    public Character getAnulado() {
        return anulado;
    }

    public void setAnulado(Character anulado) {
        this.anulado = anulado;
    }
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }
    @Override
    public boolean equals(Object object) {      
        if (!(object instanceof Historiconotapedido)) {
            return false;
        }
        Historiconotapedido other = (Historiconotapedido) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }
    @Override
    public String toString() {
        return "com.melani.entidades.Historiconotapedido[id=" + id + "]";
    }

    public String toXML(){        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fereg = "";
        if(this.getFecharegistro()!=null) {
            fereg =sdf.format(this.getFecharegistro());
        }
        String hourreg = "";
        if(this.getHoraregistro()!=null) {
            hourreg=sdf.format(this.getHoraregistro());
        }       
            StringBuilder item = new StringBuilder(32);
        
              item.append("<item>\n").append("<id>").append(this.getIdhistorico()).append("</id>\n").append("<anticipo>").append(this.getAnticipo()).append("</anticipo>\n");
                           item.append("<entregado>").append(this.getEntregado().toString()).append("</entregado>\n");
                           item.append("<fecharegistro>").append(fereg).append("</fecharegistro>\n");
                           item.append("<horaregistro>").append(hourreg).append("</horaregistro>\n");
                          item.append("<cancelado>").append(this.getCancelado()).append("</cancelado>\n");
                          item.append("<anulado>").append(this.getAnulado()).append("</anulado>\n");
                           item.append("<idnota>").append(this.getFkidnotapedido().getId()).append("</idnota>\n");
                           item.append("<iduseranulo>").append(this.getIdusuarioanulo()).append("</iduseranulo>\n");
                           item.append("<iduserentrega>").append(this.getIdusuarioentrega()).append("</iduserentrega>\n");
                           item.append("<iduserexpidio>").append(this.getIdusuarioexpidio()).append("</iduserexpidio>\n");
                           item.append("<idusuariocancelo>").append(this.getIdusuariocancelo())
                           .append("</idusuariocancelo>\n" + "<recargo>").append(this.getRecargo())
                           .append("</recargo>\n" + "<totalapagar>").append(this.getTotalapagar())
                                   .append("</totalapagar>\n").append("<porcrecargo>")
                                   .append(this.getPorcrecargo()).append("</porcrecargo>\n")
                                   .append("<porcentajedescuento>").append(this.getPorcentajedesc())
                                   .append("</porcentajedescuento>\n" ).append("<descuento>")
                                   .append(this.getDescuento()).append("</descuento>\n" )
                                   .append("<accion>").append(this.getAccion())
                                   .append("</accion>\n" ).append("<saldo>").append(this.getSaldo())
                                   .append("</saldo>\n" ).append("<total>").append(this.getTotal()).append("</total>\n");
                   item.append("</item>\n");
        
        return item.toString();        
    }
}