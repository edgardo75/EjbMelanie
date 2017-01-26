package com.melani.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import javax.persistence.TableGenerator;

@Entity
@Table(name = "IMAGEN_PRODUCTO")
@NamedQueries({@NamedQuery(name = "ImagenesProductos.findById",query = "SELECT i FROM ImagenesProductos i WHERE i.productos.sid = :sid"),
@NamedQuery(name = "ImagenesProductos.findByIdProduct", query = "SELECT i FROM ImagenesProductos i WHERE i.productos.sid = :idProducto")})
public class ImagenesProductos implements Serializable{
    private static final long serialVersionUID = 1L;
    @Id    
    @GeneratedValue(strategy=GenerationType.TABLE,generator="ImagenProductoIdGen")
    @TableGenerator(name="ImagenProductoIdGen", table="ID_GEN_IMAGE",
    pkColumnName="FNAME",pkColumnValue="IMAGE_ID", valueColumnName="FKEY",
    allocationSize=1)
    @Column(name = "ID_IMAGE")    
    private int idImagenProducto;
    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    private Productos productos;
    @Column(name = "PATHIMAGEN")
    private String pathImagenEnDisco;
    @Column(name="NOMBRE_IMAGEN")
    private String nombreImagen;
    @Column(name = "EXTENSION")
    private String extension;
    @Column(name = "MAGNITUD")
    private String magnitud;

    public ImagenesProductos() {
    }  
   
    public int getId_image() {
        return idImagenProducto;
    }

    public void setId_image(int id) {
        this.idImagenProducto = id;
    }

    public Productos getProductos() {
        return productos;
    }

    public void setProductos(Productos productos) {
        this.productos = productos;
    }

    public String getPathImagenEnDisco() {
        return pathImagenEnDisco;
    }

    public void setPathImagenEnDisco(String pathImagenEnDisco) {
        this.pathImagenEnDisco = pathImagenEnDisco;
    }

    
    public String getNombreImagen() {
        return nombreImagen;
    }

    public void setNombreImagen(String nombreImagen) {
        this.nombreImagen = nombreImagen;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getMagnitud() {
        return magnitud;
    }

    public void setMagnitud(String magnitud) {
        this.magnitud = magnitud;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.idImagenProducto);
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
        final ImagenesProductos other = (ImagenesProductos) obj;
        return Objects.equals(this.idImagenProducto, other.idImagenProducto);
    } 

    @Override
    public String toString() {
        return "ImagenesProductos{" + "idImagenProducto=" + idImagenProducto + '}';
    }
}