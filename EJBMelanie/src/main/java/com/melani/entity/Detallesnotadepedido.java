package com.melani.entity;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "DETALLESNOTADEPEDIDO")
@NamedQueries({
    @NamedQuery(name = "Detallesnotadepedido.findAll", query = "SELECT d FROM Detallesnotadepedido d"),
    @NamedQuery(name = "Detallesnotadepedido.findByCantidad", query = "SELECT d FROM Detallesnotadepedido d WHERE d.cantidad = :cantidad"),
    @NamedQuery(name = "Detallesnotadepedido.findByPrecio", query = "SELECT d FROM Detallesnotadepedido d WHERE d.precio = :precio"),
    @NamedQuery(name = "Detallesnotadepedido.findBySubtotal", query = "SELECT d FROM Detallesnotadepedido d WHERE d.subtotal = :subtotal"),
    @NamedQuery(name = "Detallesnotadepedido.findByEntregado", query = "SELECT d FROM Detallesnotadepedido d WHERE d.entregado = :entregado"),
    @NamedQuery(name = "Detallesnotadepedido.findByCancelado", query = "SELECT d FROM Detallesnotadepedido d WHERE d.cancelado = :cancelado"),
    @NamedQuery(name = "Detallesnotadepedido.findByPendiente", query = "SELECT d FROM Detallesnotadepedido d WHERE d.pendiente = :pendiente"),
    @NamedQuery(name = "Detallesnotadepedido.findByDescuento", query = "SELECT d FROM Detallesnotadepedido d WHERE d.descuento = :descuento"),
    @NamedQuery(name = "Detallesnotadepedido.findByIva", query = "SELECT d FROM Detallesnotadepedido d WHERE d.iva = :iva"),
    @NamedQuery(name = "Detallesnotadepedido.findByFkIdnota", query = "SELECT d FROM Detallesnotadepedido d WHERE d.detallesnotadepedidoPK.fkIdnota = :fkIdnota"),
    @NamedQuery(name = "Detallesnotadepedido.findByFkIdproducto", query = "SELECT d FROM Detallesnotadepedido d WHERE d.detallesnotadepedidoPK.fkIdproducto = :fkIdproducto"),
@NamedQuery(name = "Detallesnotadepedido.deleteById",query = "DELETE FROM Detallesnotadepedido d WHERE d.detallesnotadepedidoPK.fkIdnota = :idNota")})
public class Detallesnotadepedido implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected DetallesnotadepedidoPK detallesnotadepedidoPK;
    @Column(name = "CANTIDAD")
    private Integer cantidad;
    @Column(name = "PRECIO",precision=15,scale=2)
    private double precio;
    @Column(name = "SUBTOTAL",precision=15,scale=3)
    private double subtotal;
    @Column(name = "ENTREGADO")
    private Character entregado;
    @Column(name = "CANCELADO")
    private Character cancelado;
    @Column(name = "PENDIENTE")
    private Character pendiente;
    @Column(name = "DESCUENTO",precision=15,scale=2)
    private double descuento;
    @Column(name = "IVA")
    private double iva;
    @JoinColumn(name = "FK_IDNOTA", referencedColumnName = "ID", insertable = false, updatable = false)
    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    private Notadepedido notadepedido;
    @JoinColumn(name = "FK_IDPRODUCTO", referencedColumnName = "SID", insertable = false, updatable = false)
    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    private Productos productos;
    @Column(name = "ANULADO")
    private Character anulado;
    @Column(name="PRECIO_DESC",precision=15,scale=2)
    private double preciocondescuento;

    public Detallesnotadepedido() {
    }

 
    public Detallesnotadepedido(DetallesnotadepedidoPK detallesnotadepedidoPK) {
        this.detallesnotadepedidoPK = detallesnotadepedidoPK;
    }
    
    public Detallesnotadepedido(int fkIdnota, int fkIdproducto) {
        this.detallesnotadepedidoPK = new DetallesnotadepedidoPK(fkIdnota, fkIdproducto);
    }

    public DetallesnotadepedidoPK getDetallesnotadepedidoPK() {
        return detallesnotadepedidoPK;
    }

    public void setDetallesnotadepedidoPK(DetallesnotadepedidoPK detallesnotadepedidoPK) {
        this.detallesnotadepedidoPK = detallesnotadepedidoPK;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getPreciocondescuento() {
        return preciocondescuento;
    }

    public void setPreciocondescuento(double preciocondescuento) {
        this.preciocondescuento = preciocondescuento;
    }

    public Character getAnulado() {
        return anulado;
    }

    public void setAnulado(Character anulado) {
        this.anulado = anulado;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public Character getEntregado() {
        return entregado;
    }

    public void setEntregado(Character entregado) {
        this.entregado = entregado;
    }

    public Character getCancelado() {
        return cancelado;
    }

    public void setCancelado(Character cancelado) {
        this.cancelado = cancelado;
    }

    public Character getPendiente() {
        return pendiente;
    }

    public void setPendiente(Character pendiente) {
        this.pendiente = pendiente;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public double getIva() {
        return iva;
    }

    public void setIva(double iva) {
        this.iva = iva;
    }

    public Notadepedido getNotadepedido() {
        return notadepedido;
    }

    public void setNotadepedido(Notadepedido notadepedido) {
        this.notadepedido = notadepedido;
    }

    public Productos getProductos() {
        return productos;
    }

    public void setProductos(Productos productos) {
        this.productos = productos;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.detallesnotadepedidoPK);
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
        final Detallesnotadepedido other = (Detallesnotadepedido) obj;
        return Objects.equals(this.detallesnotadepedidoPK, other.detallesnotadepedidoPK);
    }
    
    @Override
    public String toString() {
        return "entity.Detallesnotadepedido[detallesnotadepedidoPK=" + detallesnotadepedidoPK + "]";
    }

    public String toXML(){
    StringBuilder item;
        item = new StringBuilder("<itemdetalle>\n").append("<idnota>")
                .append(this.getNotadepedido().getId()).append("</idnota>\n");
                                    item.append("<producto>\n").append("<id>").append(this.getProductos().getSid()).append("</id>\n");
                                        item.append("<code>").append(this.getProductos().getCodproducto()).append("</code>\n");
                                    item.append("<descripcion>").append(this.getProductos().getDescripcion()).append("</descripcion>\n");
                                    item.append("</producto>\n");
                                    item.append("<cantidad>").append(this.getCantidad()).append("</cantidad>\n");
                                    item.append("<precio>").append(this.getPrecio()).append("</precio>\n");
                                    item.append("<preciocondescuento>").append(this.getPreciocondescuento()).append("</preciocondescuento>\n");
                                    item.append("<descuento>").append(this.getDescuento()).append("</descuento>\n");
                                    item.append("<subtotal>").append(this.getSubtotal()).append("</subtotal>\n");
                                    item.append("<entregado>").append(this.getEntregado()).append("</entregado>\n");
                                    item.append("<iva>").append(this.getIva()).append("</iva>\n");
                                    item.append("<cancelado>").append(this.getCancelado()).append("</cancelado>\n");
                                    item.append("<anulado>").append(this.getAnulado()).append("</anulado>\n");
                                    item.append("</itemdetalle>\n");
        return item.toString();
    }
}