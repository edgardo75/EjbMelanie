package com.melani.entity;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
@Entity
@Table(name = "NOTADEPEDIDO")
@XmlRootElement
@NamedQueries({@NamedQuery(name = "Notadepedido.findAll",
query = "SELECT n FROM Notadepedido n"), @NamedQuery(name = "Notadepedido.findById",
        query = "SELECT n FROM Notadepedido n WHERE n.id = :id"), @NamedQuery(name = "Notadepedido.findByObservaciones",
        query = "SELECT n FROM Notadepedido n WHERE n.observaciones = :observaciones"), @NamedQuery(name = "Notadepedido.findByPendiente",
        query = "SELECT n FROM Notadepedido n WHERE n.pendiente = :pendiente"), @NamedQuery(name = "Notadepedido.findByAnticipo",
        query = "SELECT n FROM Notadepedido n WHERE n.anticipo = :anticipo"), @NamedQuery(name = "Notadepedido.findByMontoiva",
        query = "SELECT n FROM Notadepedido n WHERE n.montoiva = :montoiva"), @NamedQuery(name = "Notadepedido.findByHoracompra",
        query = "SELECT n FROM Notadepedido n WHERE n.horacompra = :horacompra"), @NamedQuery(name = "Notadepedido.findByRecargo",
        query = "SELECT n FROM Notadepedido n WHERE n.recargo = :recargo"), @NamedQuery(name = "Notadepedido.findByIdUsuarioExpidioNota",
        query = "SELECT n FROM Notadepedido n WHERE n.idUsuarioExpidioNota = :idUsuarioExpidioNota"), @NamedQuery(name = "Notadepedido.findByIdusuarioAnulado",
        query = "SELECT n FROM Notadepedido n WHERE n.idusuarioAnulado = :idusuarioAnulado"), @NamedQuery(name = "Notadepedido.findByFechaAnulado",
        query = "SELECT n FROM Notadepedido n WHERE n.fechaAnulado = :fechaAnulado"), @NamedQuery(name = "Notadepedido.findByTotal",
        query = "SELECT n FROM Notadepedido n WHERE n.total = :total"), @NamedQuery(name = "Notadepedido.findByAnulado",
        query = "SELECT n FROM Notadepedido n WHERE n.anulado = :anulado"), @NamedQuery(name = "Notadepedido.findByNumerodecupon",
        query = "SELECT n FROM Notadepedido n WHERE n.numerodecupon = :numerodecupon"), @NamedQuery(name = "Notadepedido.findByEnefectivo",
        query = "SELECT n FROM Notadepedido n WHERE n.enefectivo = :enefectivo"), @NamedQuery(name = "Notadepedido.findByEntregado",
        query = "SELECT n FROM Notadepedido n WHERE n.entregado = :entregado"), @NamedQuery(name = "Notadepedido.findByFechaentrega",
        query = "SELECT n FROM Notadepedido n WHERE n.fechaentrega = :fechaentrega"), @NamedQuery(name = "Notadepedido.findByIdusuarioEntregado",
        query = "SELECT n FROM Notadepedido n WHERE n.idusuarioEntregado = :idusuarioEntregado"), @NamedQuery(name = "Notadepedido.findByStockfuturo",
        query = "SELECT n FROM Notadepedido n WHERE n.stockfuturo = :stockfuturo"), @NamedQuery(name = "Notadepedido.findBySaldo",
        query = "SELECT n FROM Notadepedido n WHERE n.saldo = :saldo"), @NamedQuery(name = "Notadepedido.findByFechadecompra",
        query = "SELECT n FROM Notadepedido n WHERE n.fechadecompra = :fechadecompra"), @NamedQuery(name = "Notadepedido.findByCancelado",
        query = "SELECT n FROM Notadepedido n WHERE n.cancelado = :cancelado"), @NamedQuery(name = "Notadepedido.findByFecancelado",
        query = "SELECT n FROM Notadepedido n WHERE n.fecancelado = :fecancelado"), @NamedQuery(name = "Notadepedido.findByIdUsuarioCancelo",
        query = "SELECT n FROM Notadepedido n WHERE n.idUsuarioCancelo = :idUsuarioCancelo"), @NamedQuery(name = "Notadepedido.findByDescuentoNota",
        query = "SELECT n FROM Notadepedido n WHERE n.descuentoNota = :descuentoNota"), @NamedQuery(name = "Notadepedido.findByMontototalapagar",
        query = "SELECT n FROM Notadepedido n WHERE n.montototalapagar = :montototalapagar"), @NamedQuery(name = "Notadepedido.findByPorcdesctotal",
        query = "SELECT n FROM Notadepedido n WHERE n.porcdesctotal = :porcdesctotal"), @NamedQuery(name = "Notadepedido.findByPorcrecargo",
        query = "SELECT n FROM Notadepedido n WHERE n.porcrecargo = :porcrecargo"),@NamedQuery(name ="Notadepedido.deleteById",
        query = "DELETE FROM Notadepedido n WHERE n.id = :id"),@NamedQuery(name = "Notadepedido.searchAllOrderDesc",query = "SELECT n FROM Notadepedido n ORDER BY n.id DESC"),
@NamedQuery(name = "Notadepedido.findClientFk",query = "SELECT n FROM Notadepedido n WHERE n.fkIdcliente.idPersona = :id")})
public class Notadepedido implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @TableGenerator(name="NoPeIdGen", table="ID_GEN_NOTAP",
    pkColumnName="FNAME",pkColumnValue="Notadepedido" , valueColumnName="FKEY",
    allocationSize=1) 
    @GeneratedValue(strategy=GenerationType.TABLE,generator="NoPeIdGen")
    private long id;
    @Column(name = "ANTICIPO",precision=15,scale=3)
    private double anticipo;
    @Column(name = "SALDO",precision=15,scale=3)
    private double saldo;
    @Column(name = "HORACOMPRA")
    @Temporal(TemporalType.TIME)
    private Date horacompra;
    @Column(name = "ENTREGADO")
    private Character entregado;
    @Column(name = "ID_USUARIO_EXPIDIO_NOTA")
    private long idUsuarioExpidioNota;
    @Column(name = "STOCKFUTURO")
    private int stockfuturo;
    @Column(name = "FECHA_ANULADO")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaAnulado;
    @Column(name = "IDUSUARIO_ENTREGADO")
    private Long idusuarioEntregado;
    @Column(name = "ANULADO")
    private Character anulado;
    @Column(name = "FECHADECOMPRA")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechadecompra;
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "OBSERVACIONES",length=32_000)
    private String observaciones;
    @Column(name = "PENDIENTE")
    private Character pendiente;
    @Column(name = "MONTOIVA",precision=12,scale=2)
    private double montoiva;
    @Column(name = "RECARGO",precision=15,scale=3)
    private double recargo;
    @Column(name = "IDUSUARIO_ANULADO")
    private Long idusuarioAnulado;
    @Column(name = "TOTAL",precision=15,scale=3)
    private double total;    
    @Column(name = "NUMERODECUPON",length=20)
    private String numerodecupon;
    @Column(name = "ENEFECTIVO")
    private Character enefectivo;
    @Column(name="FECHAENTREGA")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaentrega;
    @Column(name = "DESCUENTO_PESOS",precision=15,scale=3)
    private double descuentoPesos;
    @OneToMany(mappedBy = "notadepedido",orphanRemoval = true,fetch = FetchType.LAZY,cascade = CascadeType.REMOVE)
    private List<Detallesnotadepedido> detallesnotadepedidoList;
    @OneToMany(mappedBy = "fkidnotapedido",orphanRemoval = true,cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    private List<Historiconotapedido> historiconotapedidoList;
    @JoinColumn(name = "IDTARJETAFK_IDTARJETA", referencedColumnName = "IDTARJETA")
    @ManyToOne(fetch = FetchType.EAGER,optional = false)
    private TarjetasCreditoDebito idTarjetaFk;
    @JoinColumn(name = "FK_IDCLIENTE", referencedColumnName = "ID_PERSONA")
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    private Personas fkIdcliente;
    @JoinColumn(name = "FKIDPORCENTAJENOTA_ID", referencedColumnName = "ID_PORCENTAJES")
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    private Porcentajes fkidporcentajenotaId;
    @Column(name="CANCELADO",length=1)
    private Character cancelado;
    @Column(name="FECANCELADO")
    @Temporal(TemporalType.DATE)
    private Date fecancelado;
    @Column(name="ID_USUARIO_CANCELO")
    private long idUsuarioCancelo;
    @Column(name="DESCUENTO_NOTA",precision=15,scale=3)
    private double descuentoNota;
    @Column(name = "MONTOTOTALAPAGAR",precision=15,scale=3)
    private double montototalapagar;
    @Column(name = "PORCDESCTOTAL",precision=12,scale=2)
    private double porcdesctotal;
    @Column(name = "PORCRECARGO",precision=12,scale=2)
    private double porcrecargo;
    @Column(name = "ULTIMA_ACTUALIZACION",length = 255)
    private String ultimaActualizacion;
    public Notadepedido() {
    }

    public Notadepedido(Long id) {
        this.id = id;
    }
    public double getAnticipo() {
        return anticipo;
    }
    public void setAnticipo(double anticipo) {
        this.anticipo = anticipo;
    }
    public double getSaldo() {
        return saldo;
    }
    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }
    public Date getHoracompra() {
        return horacompra;
    }
    public void setHoracompra(Date horacompra) {
        this.horacompra = horacompra;
    }
    public Character getEntregado() {
        return entregado;
    }
    public void setEntregado(Character entregado) {
        this.entregado = entregado;
    }
    public Long getIdUsuarioExpidioNota() {
        return idUsuarioExpidioNota;
    }
    public void setIdUsuarioExpidioNota(Long idUsuarioExpidioNota) {
        this.idUsuarioExpidioNota = idUsuarioExpidioNota;
    }
    public Integer getStockfuturo() {
        return stockfuturo;
    }
    public void setStockfuturo(Integer stockfuturo) {
        this.stockfuturo = stockfuturo;
    }
    public Date getFechaAnulado() {
        return fechaAnulado;
    }
    public double getDescuentonota() {
        return descuentoNota;
    }
    public void setDescuentonota(double descuentonota) {
        this.descuentoNota = descuentonota;
    }
    public double getDescuentoPesos() {
        return descuentoPesos;
    }
    public void setDescuentoPesos(double descuentoPesos) {
        this.descuentoPesos = descuentoPesos;
    }
    public void setFechaAnulado(Date fechaAnulado) {
        this.fechaAnulado = fechaAnulado;
    }
    public Long getIdusuarioEntregado() {
        return idusuarioEntregado;
    }
    public void setIdusuarioEntregado(Long idusuarioEntregado) {
        this.idusuarioEntregado = idusuarioEntregado;
    }
    public Character getAnulado() {
        return anulado;
    }
    public void setAnulado(Character anulado) {
        this.anulado = anulado;
    }
    public String getObservaciones() {
        return observaciones;
    }
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    public Character getPendiente() {
        return pendiente;
    }
    public void setPendiente(Character pendiente) {
        this.pendiente = pendiente;
    }

    public double getMontoiva() {
        return montoiva;
    }
    public void setMontoiva(double montoiva) {
        this.montoiva = montoiva;
    }
    public double getRecargo() {
        return recargo;
    }
    public void setRecargo(double recargo) {
        this.recargo = recargo;
    }
    public Long getIdusuarioAnulado() {
        return idusuarioAnulado;
    }
    public void setIdusuarioAnulado(Long idusuarioAnulado) {
        this.idusuarioAnulado = idusuarioAnulado;
    }
    public double getTotal() {
        return total;
    }
    public void setTotal(double total) {
        this.total = total;
    }
    public Porcentajes getFkidporcentajenotaId() {
        return fkidporcentajenotaId;
    }
    public void setFkidporcentajenotaId(Porcentajes fkidporcentajenotaId) {
        this.fkidporcentajenotaId = fkidporcentajenotaId;
    }
    public String getNumerodecupon() {
        return numerodecupon;
    }
    public void setNumerodecupon(String numerodecupon) {
        this.numerodecupon = numerodecupon;
    }
    public Character getEnefectivo() {
        return enefectivo;
    }
    public void setEnefectivo(Character enefectivo) {
        this.enefectivo = enefectivo;
    }    
    public List<Detallesnotadepedido> getDetallesnotadepedidoList() {
        return Collections.unmodifiableList(detallesnotadepedidoList);
    }
    public void setDetallesnotadepedidoList(List<Detallesnotadepedido> detallesnotadepedidoList) {
        this.detallesnotadepedidoList = detallesnotadepedidoList;
    }
    public Date getFechaentrega() {
        return fechaentrega;
    }
    public void setFechaentrega(Date fechaentrega) {
        this.fechaentrega = fechaentrega;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }   
    public List<Historiconotapedido> getHistoriconotapedidoList() {
        return Collections.unmodifiableList(historiconotapedidoList);
    }
    public void setHistoriconotapedidoList(List<Historiconotapedido> historiconotapedidoList) {
        this.historiconotapedidoList = historiconotapedidoList;
    }
    public TarjetasCreditoDebito getIdTarjetaFk() {
        return idTarjetaFk;
    }
    public void setIdTarjetaFk(TarjetasCreditoDebito idTarjetaFk) {
        this.idTarjetaFk = idTarjetaFk;
    }
    public Personas getFkIdcliente() {
        return fkIdcliente;
    }
    public void setFkIdcliente(Personas fkIdcliente) {
        this.fkIdcliente = fkIdcliente;
    }
    public Date getFechadecompra() {
        return fechadecompra;
    }
    public void setFechadecompra(Date fechadecompra) {
        this.fechadecompra = fechadecompra;
    }
    public Character getCancelado() {
        return cancelado;
    }
    public void setCancelado(Character cancelado) {
        this.cancelado = cancelado;
    }
    public Date getFecancelado() {
        return fecancelado;
    }
    public void setFecancelado(Date fecancelado) {
        this.fecancelado = fecancelado;
    }
    public Long getIdusuariocancelo() {
        return idUsuarioCancelo;
    }
    public void setIdusuariocancelo(Long idusuariocancelo) {
        this.idUsuarioCancelo = idusuariocancelo;
    }
    public double getMontototalapagar() {
        return montototalapagar;
    }
    public void setMontototalapagar(double montototalapagar) {
        this.montototalapagar = montototalapagar;
    }
    public double getPorcdesctotal() {
        return porcdesctotal;
    }
    public void setPorcdesctotal(double porcdesctotal) {
        this.porcdesctotal = porcdesctotal;
    }
    public double getPorcrecargo() {
        return porcrecargo;
    }
    public void setPorcrecargo(double porcrecargo) {
        this.porcrecargo = porcrecargo;
    }
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + Objects.hashCode(this.id);
        return hash;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Notadepedido other = (Notadepedido) obj;
        return Objects.equals(this.id, other.id);
    }    
    

    public Long getIdUsuarioCancelo() {
        return idUsuarioCancelo;
    }

    public void setIdUsuarioCancelo(Long idUsuarioCancelo) {
        this.idUsuarioCancelo = idUsuarioCancelo;
    }

    public double getDescuentoNota() {
        return descuentoNota;
    }

    public void setDescuentoNota(double descuentoNota) {
        this.descuentoNota = descuentoNota;
    }

    public String getUltimaActualizacion() {
        return ultimaActualizacion;
    }

    public void setUltimaActualizacion(String ultimaActualizacion) {
        this.ultimaActualizacion = ultimaActualizacion;
    }
    
    @Override
    public String toString() {
        return "com.melani.entity.Notadepedido[ id=" + id + " ]";
    }
    public String toXML(){    
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfh = new SimpleDateFormat("HH:mm:ss");        
        String fechanulado="";
        if (this.getFechaAnulado()!=null) {
            fechanulado=sdf.format(this.getFechaAnulado());
         }
        String feentrega="";
       if (this.getFechaentrega()!=null) {
           feentrega =sdf.format(this.getFechaentrega());
         }
        String fecompra = "";
       if(this.getFechadecompra()!=null) {
           fecompra =  sdf.format(this.getFechadecompra());
         }
        String hocompra = "";
        if (this.getHoracompra()!=null) {
            hocompra=sdfh.format(this.getHoracompra());
         }
        String fecancel="";
        if(this.getFecancelado()!=null) {
            fecancel=sdf.format(this.getFecancelado());          
         }                    
            StringBuilder item = new StringBuilder(32);
            item.append("<item>\n<id>").append(this.getId()).append("</id>\n" ).append("<tarjetadecredito>").append(this.getIdTarjetaFk().getDescripcion()).append("</tarjetadecredito>\n")
                    .append("<idtarjeta>").append(this.getIdTarjetaFk().getIdtarjeta()).append("</idtarjeta>\n").append("<numerocupon>").append(this.getNumerodecupon()).append("</numerocupon>\n")
                    .append("<observaciones>").append(this.getObservaciones()).append("</observaciones>\n").append("<anticipo>").append(this.getAnticipo()).append("</anticipo>\n").append("<anulado>").append(this.getAnulado()).append("</anulado>\n").append("<cancelado>" ).append(this.getCancelado()).append("</cancelado>\n")
                                    .append("<efectivo>").append(this.getEnefectivo()).append("</efectivo>\n").append("<entregado>").append(this.getEntregado()).append("</entregado>\n")
                                    .append("<descuentonota>").append(this.getDescuentonota()).append("</descuentonota>\n").append(
                                            "<descuentopesos>").append(this.getDescuentoPesos()).append("</descuentopesos>\n").append("<fechaanulado>").append(fechanulado).append("</fechaanulado>\n")
                                    .append("<fecancel>").append(fecancel).append("</fecancel>\n").append("<fechacompra>").append(fecompra).append("</fechacompra>\n").append("<fechaentrega>")
                                    .append(feentrega).append("</fechaentrega>\n").append("<horacompra>").append(hocompra ).append( "</horacompra>\n" ).append( "<ultimaActualizacion>" ).append( this.getUltimaActualizacion() ).append( "</ultimaActualizacion>")
                                    .append("<stockfuturo>").append( this.getStockfuturo()).append("</stockfuturo>\n" ).append( "<cliente>\n").append( "<id>").append(this.getFkIdcliente().getIdPersona()).append(
                                            "</id>\n").append("<nrodocumento>").append(this.getFkIdcliente().getNrodocumento()).append( "</nrodocumento>\n" ).append( "<apellidoynombre>" ).append( this.getFkIdcliente().getApellido())
                                                    .append(" ").append(this.getFkIdcliente().getNombre()).append("</apellidoynombre>\n").append("</cliente>\n" ).append( "<idporcentajes>").append(this.getFkidporcentajenotaId().getIdPorcentajes())
                                                            .append("</idporcentajes>\n" ).append("<usuarioexpidionota>" ).append( this.getIdUsuarioExpidioNota() ).append("</usuarioexpidionota>\n" ).append( "<usuarioanulonota>" ).append(
                                                                    this.getIdusuarioAnulado()).append("</usuarioanulonota>\n").append( "<usuarioentregonota>" ).append(this.getIdusuarioEntregado()).append("</usuarioentregonota>\n")
                                                                            .append("<usuariocancelonota>").append(this.getIdusuariocancelo() ).append("</usuariocancelonota>\n" )
                                                                                    .append("<idusuarioexpidionota>" ).append(this.getIdUsuarioExpidioNota() )
                                                                                            .append("</idusuarioexpidionota>\n" ).append("<idusuarioanulonota>").append(
                                                                                                    this.getIdusuarioAnulado()).append( "</idusuarioanulonota>\n" ).append("<idusuarioentregonota>" )
                                                                                            .append(this.getIdusuarioEntregado() ).append( "</idusuarioentregonota>\n" ).append( "<idusuariocancelonota>" ).append(
                                                                                                    this.getIdusuariocancelo()).append("</idusuariocancelonota>\n" ).append("<montoiva>")
                                                                                                    .append(this.getMontoiva() ).append("</montoiva>\n" ).append("<pendiente>").append(this.getPendiente() )
                                                                                                    .append("</pendiente>\n" ).append("<recargo>" ).append(this.getRecargo() ).append("</recargo>\n" ).append( "<total>" ).append(this.getTotal()).append(
                                                                                                            "</total>\n" ).append("<saldo>" ).append(this.getSaldo() ).append("</saldo>\n" ).append("<montototalapagar>" ).append(this.getMontototalapagar() ).append(
                                                                                                                    "</montototalapagar>\n" ).append( "<porcentajedesctotal>" ).append(this.getPorcdesctotal() )
                                                                                                                    .append("</porcentajedesctotal>\n" ).append( "<recargototal>" ).append(this.getRecargo()).append("</recargototal>\n" ).append( "<porcrecargo>" )
                                                                                                                    .append(this.getPorcrecargo() ).append( "</porcrecargo>\n" ).append( "<detallenota>\n");
                            
                                if(this.getDetallesnotadepedidoList().isEmpty()) {
                                    item.append("</detallenota>\n");
                                } else{
                                    StringBuilder detalles = new StringBuilder(32);
                                    for (Detallesnotadepedido detallesnotadepedido : detallesnotadepedidoList) {
                                        detalles.append(detallesnotadepedido.toXML());
                                    }                                    
                                    item.append(detalles.append("</detallenota>\n"));
                                }
                                item.append("<detallehistorico>\n");
                                if(this.getHistoriconotapedidoList().isEmpty()) {
                                    item.append("</detallehistorico>\n");
                                } else{
                                    StringBuilder detallesHistorico = new StringBuilder(32);
                                    for(Historiconotapedido historico: historiconotapedidoList){
                                        detallesHistorico.append(historico.toXML());
                                    }                                    
                                    item.append(detallesHistorico.append("</detallehistorico>\n"));
                                }
                             item.append("</item>\n");                   
                    return item.toString();
    }
}