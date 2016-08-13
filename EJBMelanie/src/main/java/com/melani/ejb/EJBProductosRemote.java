package com.melani.ejb;
import com.melani.entity.Productos;
import javax.ejb.Remote;

@Remote
public interface EJBProductosRemote {   
    
    String addProducto(String xmlProducto);
    String selectOneProducto(long idproducto);
    Productos agregarProductos(Productos producto);
    String searchAllProductos();
    int controlStockProducto(long idProducto, int cantidad, long idUsuario);
    String actualizarProducto(String xmlProducto);
    int grabarImagen(long id_producto, byte[] longitudImagen,String nameImage,String magnitud);
    byte[] obtenerImagenProducto(long idProducto);
}