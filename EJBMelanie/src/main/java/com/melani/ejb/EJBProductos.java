package com.melani.ejb;
import com.melani.entity.ExistenciasProductos;
import com.melani.entity.ImagenesProductos;
import com.melani.entity.Productos;
import com.melani.utils.DatosProductos;
import com.melani.utils.Imagen;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
@Stateless(name="ejb/EJBProductos")
@WebService(serviceName="ServiceProductos",name="ProductosWs")
public class EJBProductos implements EJBProductosRemote {    
    @PersistenceContext()    
    private EntityManager em; 
    private final Imagen imagen = new Imagen();   
    @Override
    public String addProducto(String xmlProducto) {          
        
     
            return String.valueOf(agregarProductoyProcesar(xmlProducto));        
    }    
    private long agregarProductoyProcesar(String xmlProducto) {
        long retorno;        
        
                XStream xstream = new XStream(new StaxDriver());
                xstream.alias("producto", DatosProductos.class);        
                DatosProductos datosProducto = (DatosProductos) xstream.fromXML(xmlProducto);                
                
                retorno = procesarProducto(datosProducto);                
       return retorno;        
    }    
    private long existencias(Productos producto) {
        long retorno;    
            List<ExistenciasProductos>lista = em.createQuery("SELECT e FROM ExistenciasProductos e WHERE e.productos.sid = :sid")
                .setParameter("sid", producto.getSid()).getResultList();
                producto.setExistenciasProductoss(lista);
                em.merge(producto);
                retorno = producto.getSid();                    
            return retorno;        
    }
    @Override
    public String selectOneProducto(long idproducto) {
        String result = "NADA";       
            Productos producto = em.find(Productos.class, idproducto);
            result+=producto.toXML();        
            return result;        
    }    
    @Override    
    public Productos agregarProductos(Productos producto) {        
            GregorianCalendar calendario = new GregorianCalendar(Locale.getDefault());
                Productos produ = em.find(Productos.class, producto.getSid());
                    if(produ!=null){
                        produ.setFecha(calendario.getTime());
                        em.persist(producto);
                    }else{                        
                          produ.setFecha(calendario.getTime());
                          em.merge(producto);
                    }        
            return producto;        
    }
    @Override
    public String searchAllProductos() {
        String xml;        
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");            
            Query query = em.createNamedQuery("Productos.findAll");
            List<Productos> lista = query.getResultList();
                    if(lista.isEmpty()) {
                        xml="LA CONSULTA NO ARROJÓ RESULTADOS";
                    } else{
                        Iterator iter = lista.iterator();
                        xml="<Lista>\n";
                        StringBuilder xmlLoop= new StringBuilder(32);
                        while(iter.hasNext()){
                            Productos prod = (Productos) iter.next();
                            xmlLoop.append("<producto>\n");
                                    xmlLoop.append("<id>").append(prod.getSid()).append("</id>\n");
                                    xmlLoop.append("<idproduct>").append(prod.getCodproducto()).append("</idproduct>\n");
                                    xmlLoop.append("<descripcion>").append(prod.getDescripcion()).append("</descripcion>\n");
                                    xmlLoop.append("<cantidadDisponible>").append(prod.getCantidadDisponible()).append("</cantidadDisponible>\n");
                                    xmlLoop.append("<cantidadInicial>").append(prod.getCantidadInicial()).append("</cantidadInicial>\n");
                                    xmlLoop.append("<fecha>").append(sdf.format(prod.getFecha())).append("</fecha>\n");
                                    xmlLoop.append("<precio>").append(prod.getPrecioUnitario()).append("</precio>\n");                                    
                                    xmlLoop.append("<img>").append(prod.getImagenesProductosList().size()).append("</img>\n");
                                    xmlLoop.append("</producto>\n");                 
                        } 
                        xml+=xmlLoop;
                        xml+="</Lista>\n";
                    }       
            return xml;        
    }
    @Override
    public int controlStockProducto(long idProducto, int cantidad, long idUsuario) {
        int resultado;        
                        GregorianCalendar gc = new GregorianCalendar(Locale.getDefault());
                        Productos producto = em.find(Productos.class, idProducto);
                        producto.setCantidadDisponible(producto.getCantidadDisponible()-cantidad);                       
                            ExistenciasProductos existencias = new ExistenciasProductos();
                                    existencias.setCantidadactual(-cantidad);
                                    existencias.setCantidadinicial(0);
                                    existencias.setFechaagregado(gc.getTime());
                                    existencias.setIdUsuario(idUsuario);
                                    existencias.setPreciounitario(producto.getPrecioUnitario());
                                    existencias.setProductos(em.find(Productos.class, idProducto));
                                    em.persist(existencias);
                                        Query obtenerExistenciasDeUnProducto = em.createQuery("SELECT e FROM ExistenciasProductos e WHERE e.productos.sid = :idproducto");
                                        obtenerExistenciasDeUnProducto.setParameter("idproducto", producto.getSid());
                                            List<ExistenciasProductos>lista = obtenerExistenciasDeUnProducto.getResultList();
                                                producto.setExistenciasProductoss(lista);
                        em.persist(producto);
                        resultado = producto.getCantidadDisponible();                
            return resultado;
    }
    @Override
    public String actualizarProducto(String xmlProducto) {
          String retorno = String.valueOf(updateProducto(xmlProducto));
            return retorno;    
    }    
    private long updateProducto(String xmlProducto) {
         long retorno = 0;        
         Productos producto;
             XStream xstream = new XStream(new StaxDriver());
                xstream.alias("producto", DatosProductos.class);
                DatosProductos datosProducto = (DatosProductos) xstream.fromXML(xmlProducto);
                GregorianCalendar calendario = new GregorianCalendar(Locale.getDefault());
                if(datosProducto.getIdproducto()>0&&datosProducto.getDescripcion().length()>0) {
                   Query buscarPorProductoPorNombre = em.createNamedQuery("Productos.findByDescripcion");
                   buscarPorProductoPorNombre.setParameter("descripcion".toLowerCase(), datosProducto.getDescripcion().toLowerCase().trim());
                    
                    producto =em.find(Productos.class, datosProducto.getIdproducto());                             
                    
                                        producto.setDescripcion(buscarPorProductoPorNombre.getResultList().size()>0?producto.getDescripcion():datosProducto.getDescripcion());
                                        
                                        producto.setPrecioUnitario(datosProducto.getPreciounitario());
                                       em.flush();
                                                ExistenciasProductos existencias = new ExistenciasProductos();
                                                existencias.setCantidadactual(datosProducto.getCantidaddisponible());
                                                existencias.setCantidadinicial(0);
                                                existencias.setFechaagregado(calendario.getTime());
                                                existencias.setPreciounitario(datosProducto.getPreciounitario());
                                                existencias.setProductos(em.find(Productos.class, producto.getSid()));
                                                existencias.setIdUsuario(datosProducto.getIdusuario());
                                                em.persist(existencias);                                                                        
                                                retorno = producto.getSid();       
                    
                }
            return retorno;        
    }    
    
    @Override
    public long modificarCantidadDisponible(long idProducto,int cantidad){
    Productos producto = em.find(Productos.class, idProducto);
    
    producto.setCantidadDisponible(producto.getCantidadDisponible()+cantidad);
    em.flush();
    ExistenciasProductos existencias = new ExistenciasProductos();
                                                existencias.setCantidadactual(producto.getCantidadDisponible()+cantidad);
                                                existencias.setCantidadinicial(0);
                                                existencias.setFechaagregado(Calendar.getInstance().getTime());
                                                existencias.setPreciounitario(producto.getPrecioUnitario());
                                                existencias.setProductos(em.find(Productos.class, producto.getSid()));
                                                existencias.setIdUsuario(0);
                                                em.persist(existencias);                                                                        
                                                
    return producto.getSid();
    }
    @Override
    public int grabarImagen(long id_producto, byte[] longitudImagen,String nameImage,String magnitud) {
        int retorno;
        Productos producto = em.find(Productos.class, id_producto);
        retorno= grabarPathImagenEnBaseDeDatos(producto,imagen.procesarImagen(longitudImagen, nameImage, magnitud));
        return retorno; 
    }
    @Override
    public byte[] obtenerImagenProducto(long idProducto) {        
            Query buscarImagenesDelProducto = em.createNamedQuery("ImagenesProductos.findById");
                buscarImagenesDelProducto.setParameter("sid", idProducto);
            List<ImagenesProductos>lista = buscarImagenesDelProducto.getResultList();
            return imagen.obtenerImagenByteArray(lista);   
    }
    private int grabarPathImagenEnBaseDeDatos(Productos producto, String[] procesarImagen) {
        long retorno;        
            ImagenesProductos imgProd =new ImagenesProductos();            
                    imgProd.setExtension(procesarImagen[1]);            
                    imgProd.setMagnitud(procesarImagen[2]);            
                    imgProd.setNombreImagen(procesarImagen[3]);            
                    imgProd.setPathImagenEnDisco(procesarImagen[0]);            
                    imgProd.setProductos(producto);            
                    em.persist(imgProd);  
                Query obtenerImagenPorIdProducto = em.createNamedQuery("ImagenesProductos.findByIdProduct");
                    obtenerImagenPorIdProducto.setParameter("idProducto", producto.getSid());
               List<ImagenesProductos>lista = obtenerImagenPorIdProducto.getResultList();               
               producto.setImagenesProductosList(lista);               
               em.merge(producto);            
            retorno=producto.getSid(); 
            return (int) retorno;
    }
    public String actualizarPathImagenesEnBaseDeDatos(){       
            Query listaDeImagenes = em.createQuery("Select i FROM ImagenesProductos i");        
                List<ImagenesProductos> listado = listaDeImagenes.getResultList();        
                listado.stream().forEach((imagenesGuardadas) -> {
                    imagenesGuardadas.setPathImagenEnDisco(ResourceBundle.getBundle("config").getString("PATH_IMAGE")+imagenesGuardadas.getNombreImagen());
        });        
            em.flush();
        return "Echo";
    }
    private long procesarProducto(DatosProductos datosProducto) {
        GregorianCalendar calendario = new GregorianCalendar(Locale.getDefault());        
        String descripcionDeProducto=datosProducto.getDescripcion();
        String codigo =datosProducto.getCodproducto();
        long retorno;
                if(!descripcionDeProducto.isEmpty()){
                    if(codigo.length()>0){            
                            Productos producto =em.find(Productos.class, datosProducto.getIdproducto());
                                        Query buscarProductoPorCodigoDeProducto = em.createQuery("SELECT p FROM Productos p WHERE LOWER(p.codproducto) LIKE LOWER(:codigoproducto)");
                                        buscarProductoPorCodigoDeProducto.setParameter("codigoproducto", codigo.concat("%"));
                                        Query obtenerProductoPorDescripcion = em.createQuery("SELECT p FROM Productos p WHERE LOWER(p.descripcion) LIKE LOWER(:descripcion)");
                                        obtenerProductoPorDescripcion.setParameter("descripcion", descripcionDeProducto.concat("%"));
                                            if(buscarProductoPorCodigoDeProducto.getResultList().isEmpty()){
                                                if(obtenerProductoPorDescripcion.getResultList().isEmpty()){
                                                    
                                                            producto = new Productos();
                                                            producto.setCantidadDisponible(datosProducto.getCantidaddisponible());
                                                            producto.setCantidadInicial(datosProducto.getCantidadinicial());
                                                            producto.setPrecioUnitario(datosProducto.getPreciounitario());
                                                            producto.setDescripcion(datosProducto.getDescripcion().toUpperCase());
                                                            producto.setFecha(calendario.getTime());                                    
                                                            producto.setCodproducto(datosProducto.getCodproducto().toUpperCase());
                                                            em.persist(producto);                                                            
                                                                ExistenciasProductos existencias = new ExistenciasProductos();
                                                                existencias.setCantidadactual(datosProducto.getCantidaddisponible());
                                                                existencias.setCantidadinicial(datosProducto.getCantidadinicial());
                                                                existencias.setFechaagregado(calendario.getTime());
                                                                existencias.setPreciounitario(datosProducto.getPreciounitario());
                                                                existencias.setIdUsuario(datosProducto.getIdusuario());
                                                                existencias.setProductos(em.find(Productos.class, producto.getSid()));
                                                                em.persist(existencias);
                                                            
                                                            retorno = producto.getSid();
                                                     
                                                        }else {
                                                          retorno = -5;
                                                        }
                                            }else {
                                            retorno = -6;
                                            }
                          }else {
                             retorno=-8;
                          }
                }else {
                    retorno=-7;
                }
                
                    return retorno;
    }
}