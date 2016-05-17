package com.melani.ejb;
import com.melani.entity.Detallespresupuesto;
import com.melani.entity.DetallespresupuestoPK;
import com.melani.entity.Presupuestos;
import com.melani.entity.Productos;
import com.melani.utils.DatosPresupuestos;
import com.melani.utils.DetallesPresupuesto;
import com.melani.utils.ItemDetallesPresupuesto;
import com.melani.utils.ProjectHelpers;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
@Stateless(name="ejb/EJBPresupuestosBean")
@WebService(serviceName="ServicesPresupuestos",name="PresupuestoWs")
public class EJBPresupuestosBean implements EJBPresupuestosRemote {    
    @PersistenceContext
    private EntityManager em;  
    @Override
    @SuppressWarnings("FinallyDiscardsException")
    public long addBudgets(String xmlPresupuesto) {
        long retorno;        
            XStream xstream = new XStream(new StaxDriver());
            ProjectHelpers xmlParser = new ProjectHelpers();
                xstream.alias("presupuesto", DatosPresupuestos.class);
                xstream.alias("detallepresupuesto", DetallesPresupuesto.class);
                xstream.alias("itemdetallespresupuesto", ItemDetallesPresupuesto.class);
                xstream.addImplicitCollection(DetallesPresupuesto.class, "lista");
            DatosPresupuestos datospresupuesto = (DatosPresupuestos) xstream.fromXML(xmlParser.parsearCaracteresEspecialesXML(xmlPresupuesto));
            retorno = almacenarPresupuesto(datospresupuesto);        
            return retorno;        
    }
    private long almacenarPresupuesto(DatosPresupuestos datospresupuesto) {
        long result;        
            GregorianCalendar gc = new GregorianCalendar();
            GregorianCalendar fvalidez = new GregorianCalendar();
            fvalidez.add(GregorianCalendar.DATE,20);
                    Presupuestos presupuesto = new Presupuestos();
                            presupuesto.setTotalapagar(datospresupuesto.getTotalapagar());
                            presupuesto.setFechapresupuesto(gc.getTime());
                            presupuesto.setApellido(datospresupuesto.getApellido().toUpperCase());
                            presupuesto.setNombre(datospresupuesto.getNombre().toUpperCase());
                            presupuesto.setIva(datospresupuesto.getIva());
                            presupuesto.setValidez(fvalidez.getTime());
                            presupuesto.setPorcetajedescuentoTOTAL(datospresupuesto.getPorc_descuento_total());
                            presupuesto.setIdUsuarioFk(datospresupuesto.getId_usuario_expidio());
                            presupuesto.setObservaciones(datospresupuesto.getObservaciones());
                            presupuesto.setTotal(datospresupuesto.getTotal());
                            presupuesto.setRecargototal(datospresupuesto.getRecargototal());
                            presupuesto.setPorcentajerecargo(datospresupuesto.getPorcentajerecargo());
                            presupuesto.setDescuentoresto(datospresupuesto.getDescuentoresto());
                            presupuesto.setPorcetajedescuentoTOTAL(datospresupuesto.getPorc_descuento_total());
                            em.persist(presupuesto);
                List<ItemDetallesPresupuesto>lista = datospresupuesto.getDetallesPresupuesto().getLista();
            for (ItemDetallesPresupuesto itemDetallesPresupuesto : lista) {
                        Productos producto = em.find(Productos.class, Long.valueOf(itemDetallesPresupuesto.getFk_id_producto()));
                        DetallespresupuestoPK detallePresupuestoPK = new DetallespresupuestoPK(presupuesto.getIdPresupuesto(), 
                                itemDetallesPresupuesto.getFk_id_producto());
                        Detallespresupuesto detallePresupuesto = new Detallespresupuesto(detallePresupuestoPK);
                        detallePresupuesto.setCantidad(itemDetallesPresupuesto.getCantidad());
                        detallePresupuesto.setDescuento(itemDetallesPresupuesto.getDescuento());
                        detallePresupuesto.setPrecioDesc(itemDetallesPresupuesto.getPrecio_desc());
                        detallePresupuesto.setPrecio(itemDetallesPresupuesto.getPrecio());
                        detallePresupuesto.setDetallespresupuestoPK(detallePresupuestoPK);
                        detallePresupuesto.setPresupuestos(em.find(Presupuestos.class, presupuesto.getIdPresupuesto()));
                        detallePresupuesto.setProductos(producto);
                        detallePresupuesto.setSubtotal(itemDetallesPresupuesto.getSubtotal());
                     em.persist(detallePresupuesto);
                            Query consultaListProdPres = em.createQuery("SELECT d FROM Detallespresupuesto d WHERE "
                                    + "d.detallespresupuestoPK.fkProducto = :fkProducto");
                            consultaListProdPres.setParameter("fkProducto", itemDetallesPresupuesto.getFk_id_producto());
                            producto.setDetallepresupuestosList(consultaListProdPres.getResultList());
            }
                                Query consulta1 = em.createQuery("SELECT d FROM Detallespresupuesto "
                                        + "d WHERE d.detallespresupuestoPK.idDpFk = :idDpFk");
                                consulta1.setParameter("idDpFk", presupuesto.getIdPresupuesto());
                                presupuesto.setDetallepresupuestosList(consulta1.getResultList());
                                em.merge(presupuesto);
                        result=Long.valueOf(presupuesto.getIdPresupuesto());            
            return result;        
    }

    @Override
    public String selectAllPresupuestosJPA() {
        String xmlpresupuesto = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+"<Lista>\n";        
            Query consulta  = em.createNamedQuery("Presupuesto.findPresupuestoOrderByFechaIdPresupesto");
                        List<Presupuestos>lista = consulta.getResultList();
                        if(lista.isEmpty()) {
                            xmlpresupuesto+="LA CONSULTA NO ARROJÓ RESULTADOS!!!";
                        } else{
                            for (Presupuestos presupuestos : lista) {
                                xmlpresupuesto+=presupuestos.toXML();
                            }
                        }        
                        xmlpresupuesto+="</Lista>\n";                    
     return xmlpresupuesto;    
    }

    @Override
    public Integer getRecordCount() {        
        Query presupuesto = em.createNamedQuery("Presupuestos.findAll");
        return presupuesto.getResultList().size();            
    }

    @Override
    public String verPresupuestosPaginados(Integer index, Integer record) {
        String result ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<Lista>\n";
                    Query paging = em.createQuery("Select p From Presupuestos p Order by p.fechapresupuesto desc,p.idPresupuesto desc",Presupuestos.class);
                          paging.setMaxResults(record);
                          paging.setFirstResult(index*record);
                        List<Presupuestos>lista = paging.getResultList();            
                            if(lista.isEmpty()) {
                                result+="LA CONSULTA NO ARROJÓ RESULTADOS!!!";
                            } else{
                                for (Presupuestos presupuestos : lista) {
                                    result+=presupuestos.toXML();
                                }
                            }  
            result +="</Lista>\n";
        return result;
    }    
    @Override
    public String searchOneBudget(int idpresupuesto) {
        String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+"<Lista>\n";
           Query paging =em.createNamedQuery("Presupuesto.findIdPresupuestoOrderByFechaIdPresupesto");            
                paging.setParameter("1", idpresupuesto);
                    List<Presupuestos>lista = paging.getResultList();
                        if(lista.isEmpty()) {
                            result+="LA CONSULTA NO ARROJÓ RESULTADOS!!!";
                        } else{
                                result+=processPresupuesto(lista);
                        }    
            result+="</Lista>\n";
        return result;    
    }
    
    @Override
    public String ShowReportPresupuesto(Integer idPresupuesto) {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+ "<Lista>\n";  
            Query consulta = em.createQuery("SELECT p FROM Presupuestos p WHERE p.idPresupuesto = :idPresupuesto");
                consulta.setParameter("idPresupuesto", idPresupuesto);            
                            List<Presupuestos>lista = consulta.getResultList();
                            if(!lista.isEmpty()){
                                xml+=processPresupuesto(lista);     

                            }else {
                                xml+="<result>La consulta no arrojó resultados</result>";
                            }           
                return xml+="</Lista>\n";     
    }
    public String ShowReportVerPresupuesto(Long first, Long last) {  
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+ "<Lista>\n";
        long uno=first;
        long dos=last;        
                String sql =   "Select p FROM Presupuestos p Where p.idPresupuesto BETWEEN :uno and :dos "
                    + "order by p.fechapresupuesto desc ,p.idPresupuesto desc";            
                    Query consulta = em.createQuery(sql);
                    consulta.setParameter("uno", uno);
                    consulta.setParameter("dos", dos);
                        List<Presupuestos>lista = consulta.getResultList();           
                            if(!lista.isEmpty()){                
                                    xml+=processPresupuesto(lista);
                            }else {
                                xml+="<result>La consulta no arrojó resultados</result>";
                            }      
            return xml+"</Lista>";        
    }    
    private StringBuilder processPresupuesto(List<Presupuestos>lista){
        StringBuilder xmlLoop = new StringBuilder(32);
        for (Presupuestos presupuestos : lista) {
             xmlLoop.append(presupuestos.toXML());
        }   
         return xmlLoop;
    }
}