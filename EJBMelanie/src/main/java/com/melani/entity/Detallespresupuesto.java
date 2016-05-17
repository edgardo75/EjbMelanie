package com.melani.entity;
import java.io.Serializable;
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
@Table(name = "DETALLESPRESUPUESTO")
@NamedQueries({@NamedQuery(name = "Detallespresupuesto.findAll", query = "SELECT d FROM Detallespresupuesto d"),
@NamedQuery(name = "Detallespresupuesto.findByDescuento", query = "SELECT d FROM Detallespresupuesto d WHERE d.descuento = :descuento"),
@NamedQuery(name = "Detallespresupuesto.findByCantidad", query = "SELECT d FROM Detallespresupuesto d WHERE d.cantidad = :cantidad"),
@NamedQuery(name = "Detallespresupuesto.findBySubtotal", query = "SELECT d FROM Detallespresupuesto d WHERE d.subtotal = :subtotal"),
@NamedQuery(name = "Detallespresupuesto.findByFkProducto", query = "SELECT d FROM Detallespresupuesto d WHERE d.detallespresupuestoPK.fkProducto = :fkProducto"),
@NamedQuery(name = "Detallespresupuesto.findByIdDpFk", query = "SELECT d FROM Detallespresupuesto d WHERE d.detallespresupuestoPK.idDpFk = :idDpFk"),
@NamedQuery(name = "Detallespresupuesto.findByPrecio", query = "SELECT d FROM Detallespresupuesto d WHERE d.precio = :precio"),
@NamedQuery(name = "Detallespresupuesto.findByPrecioDesc", query = "SELECT d FROM Detallespresupuesto d WHERE d.precioDesc = :precioDesc")})
public class Detallespresupuesto implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected DetallespresupuestoPK detallespresupuestoPK;
    @Column(name = "SUBTOTAL",precision=15,scale=3)
    private double subtotal;
    @Column(name = "DESCUENTO",precision=15,scale=2)
    private double descuento;
    @Column(name = "PRECIO",precision=12,scale=2)
    private double precio;
    @Column(name = "PRECIO_DESC",precision=12,scale=2)
    private double precioDesc;
    @Column(name = "CANTIDAD")
    private Short cantidad;
    @JoinColumn(name = "ID_DP_FK", referencedColumnName = "ID_PRESUPUESTO", insertable = false, updatable = false)
    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    private Presupuestos presupuestos;
    @JoinColumn(name = "FK_PRODUCTO", referencedColumnName = "SID", insertable = false, updatable = false)
    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    private Productos productos;

    public Detallespresupuesto() {
    }

    public Detallespresupuesto(DetallespresupuestoPK detallespresupuestoPK) {
        this.detallespresupuestoPK = detallespresupuestoPK;
    }

    public Detallespresupuesto(int idDpFk, int fkProducto) {
        this.detallespresupuestoPK = new DetallespresupuestoPK(idDpFk, fkProducto);
    }

    public DetallespresupuestoPK getDetallespresupuestoPK() {
        return detallespresupuestoPK;
    }

    public void setDetallespresupuestoPK(DetallespresupuestoPK detallespresupuestoPK) {
        this.detallespresupuestoPK = detallespresupuestoPK;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public double getPrecioDesc() {
        return precioDesc;
    }

    public void setPrecioDesc(double precioDesc) {
        this.precioDesc = precioDesc;
    }

    public Short getCantidad() {
        return cantidad;
    }

    public void setCantidad(Short cantidad) {
        this.cantidad = cantidad;
    }

    public Presupuestos getPresupuestos() {
        return presupuestos;
    }

    public void setPresupuestos(Presupuestos presupuestos) {
        this.presupuestos = presupuestos;
    }

    public Productos getProductos() {
        return productos;
    }

    public void setProductos(Productos productos) {
        this.productos = productos;
    }
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (detallespresupuestoPK != null ? detallespresupuestoPK.hashCode() : 0);
        return hash;
    }
    @Override
    public boolean equals(Object object) {        
        if (!(object instanceof Detallespresupuesto)) {
            return false;
        }
        Detallespresupuesto other = (Detallespresupuesto) object;
        return !((this.detallespresupuestoPK == null && other.detallespresupuestoPK != null) || (this.detallespresupuestoPK != null && !this.detallespresupuestoPK.equals(other.detallespresupuestoPK)));
    }
    @Override
    public String toString() {
        return "entity.Detallespresupuesto[detallespresupuestoPK=" + detallespresupuestoPK + "]";
    }

    public String toXML(){
        StringBuilder xml= new StringBuilder(32);
                xml.append("<itemdetallepresupuesto>\n<idproducto>").append(this.getProductos().getSid()).append("</idproducto>\n")
                        .append("<descripcion_prod>")
                        .append(this.getProductos().getDescripcion())
                        .append("</descripcion_prod>\n").append("<codigo_producto>")
                        .append(this.getProductos().getCodproducto()).append("</codigo_producto>\n")
                        .append("<idpresupuesto>").append(this.getPresupuestos().getIdPresupuesto())
                        .append("</idpresupuesto>\n")
                        .append("<precio>")
                        .append(this.getPrecio()).append("</precio>\n")
                        .append("<precio_desc>").append(this.getPrecioDesc())
                        .append("</precio_desc>\n").append("<descuento>")
                        .append(this.getDescuento()).append("</descuento>\n")
                        .append("<subtotal>").append(this.getSubtotal()).append("</subtotal>\n")
                        .append("<cantidad>").append(this.getCantidad()).append("</cantidad>\n").append("</itemdetallepresupuesto>\n");
        return xml.toString();
    }
}