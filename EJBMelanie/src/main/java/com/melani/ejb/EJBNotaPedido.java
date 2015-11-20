/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.melani.ejb;
import com.melani.entity.Clientes;
import com.melani.entity.Detallesnotadepedido;
import com.melani.entity.DetallesnotadepedidoPK;
import com.melani.entity.Empleados;
import com.melani.entity.Historiconotapedido;
import com.melani.entity.Notadepedido;
import com.melani.entity.Personas;
import com.melani.entity.Porcentajes;

import com.melani.entity.Productos;
import com.melani.entity.TarjetasCreditoDebito;
import com.melani.utils.DatosNotaPedido;
import com.melani.utils.DetallesNotaPedido;
import com.melani.utils.Itemdetallesnota;


import com.melani.utils.ProjectHelpers;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import org.apache.log4j.Logger;
/**
 *
 * @author Edgardo
 */
@Stateless(name="ejb/EJBNotaPedido")
@WebService(serviceName="ServiceNotaPedido",name="NotaPedidoWs")
public class EJBNotaPedido implements EJBNotaPedidoRemote {
    private static final Logger logger = Logger.getLogger(EJBNotaPedido.class);
    private static final String FECHA_DEFAULT = "01/01/1900";
    @PersistenceContext()
    
    private EntityManager em;
    @EJB
      EJBProductosRemote producto;
    @EJB
    EJBPresupuestosRemote ejbpresupuesto;
    
    DatosNotaPedido notadepedido;
    
    volatile Double totalCompras;
    
    private DatosNotaPedido xestreaNotapedido(String xmlNotapedido){
            XStream xestream = new XStream(new StaxDriver());
                xestream.alias("notapedido", DatosNotaPedido.class);
                xestream.alias("personas", DatosNotaPedido.Personas.class);
                xestream.alias("tarjetacredito", DatosNotaPedido.TarjetaCredito.class);
                xestream.alias("porcentajes", DatosNotaPedido.Porcentajes.class);
                xestream.alias("itemdetallesnota", Itemdetallesnota.class);
                xestream.alias("detallesnotapedido", DetallesNotaPedido.class);
                xestream.addImplicitCollection(DetallesNotaPedido.class, "list");
            return notadepedido = (DatosNotaPedido) xestream.fromXML(ProjectHelpers.parsearCaracteresEspecialesXML(xmlNotapedido));
    }

    /**
     *
     * @param xmlNotaPedido
     * @return
     */
    @Override
    public long agregarNotaPedido(String xmlNotaPedido) {
        long retorno =0L;
        try {
             //---------------------------------------------------------------------------------
            //---------------------------------------------------------------------------------
            
            if(!xmlNotaPedido.isEmpty()){
                retorno = almacenarNotaPedido(xestreaNotapedido(xmlNotaPedido));
            }else{
                retorno = -5;
            }
        } catch (Exception e) {
            logger.error("Error en metodo agregarNotaPedido, verifique"+e.getMessage());
            retorno = -1;
        }finally{
            return retorno;
        }
    }
    private long almacenarNotaPedido(DatosNotaPedido notadepedido) {
        long retorno =0L;
        try {
            //---------------------------------------------------------------------------------
            GregorianCalendar gc = new GregorianCalendar();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            
            //---------------------------------------------------------------------------------
                            Clientes cliente = em.find(Clientes.class, notadepedido.getPersonas().getId());
                            Notadepedido notape = new Notadepedido();
                            
                            almacenarNotaVoid(notape,notadepedido,sdf,gc);
                                           
                            em.persist(notape);
                            /*
                             * trato la lista de productos de la nota de pedido, a continuación detalle*/
                                  long historico =0;
                                         switch(notadepedido.getStockfuturo()){
                                             case 0:{
                                                 retorno = almacenarDetalleNotaConControlStock(notadepedido,notape);
                                                             /*Almacenar el historico en el método para que quede bien registrada la operacion*/
                                                           historico =  almacenarHistorico(notadepedido,notape);
                                                    }
                                             break;
                                             default :
                                                    {
                                                        retorno = almacenarDetalleNota(notadepedido,notape);
                                                      /*Almacenar el historico en el método para que quede bien registrada la operacion*/
                                                       historico =  almacenarHistorico(notadepedido,notape);
                                                    }
                                         }
                                         Query queryFindByIdProducto =em.createNamedQuery("Notadepedido.findClientFk");
                                            queryFindByIdProducto.setParameter("id", cliente.getIdPersona());
                                            List<Notadepedido>lista=queryFindByIdProducto.getResultList();
                                                cliente.setNotadepedidoList(lista);
                                                    totalCompras = cliente.getTotalCompras().doubleValue()+notape.getMontototalapagar().doubleValue();
                                                    int totalPuntos = cliente.getTotalEnPuntos().intValue()+totalCompras.intValue();
                                                    cliente.setTotalCompras(BigDecimal.valueOf(totalCompras));
                                                    cliente.setTotalEnPuntos(BigInteger.valueOf(totalPuntos));
                                                    cliente.setFechaCarga(gc.getTime());                                           
                                                em.persist(cliente);
                                                
                                                
                                 if(historico<0) {
                                     retorno = historico;
                                 } else{
                                      retorno = notape.getId();
                                             
            }
        } catch (ParseException e) {
            logger.error("Error en metodo almacenarNotaPedido, EJBNotaPedido "+e.getMessage());
            retorno =-2;
        }finally{            
            return retorno;
        }
    }
    private long almacenarDetalleNota(DatosNotaPedido notadepedido, Notadepedido notape) {
        long retorno =0;
        try {
            /* a continuacion "desmenuzo" todo el detalle de la nota de pedido para
             * asi de esa manera poder amancenarla en los lugares correspondiente como lo es en detalles
             * de pedido de la nota y sus ligaduras con la nota de pedido propiamente dicha y los productos
             * para mantener la persistencia de las lista de manera real*/
            List<Itemdetallesnota>lista = notadepedido.getDetallesnotapedido().getDetallesnota();
            
                    for (Itemdetallesnota itemdetallesnota : lista) {
                            Productos productos = em.find(Productos.class,itemdetallesnota.getId_producto());
                            DetallesnotadepedidoPK detallespk = new DetallesnotadepedidoPK(notape.getId(), itemdetallesnota.getId_producto());
                            Detallesnotadepedido detalles = new Detallesnotadepedido();
                            
                            almacenarDetallesNota(productos,itemdetallesnota,notape,detallespk,detalles);
                            /*detalles.setCancelado(Character.valueOf(itemdetallesnota.getCancelado()));
                            detalles.setCantidad(itemdetallesnota.getCantidad());
                            detalles.setDescuento(BigDecimal.valueOf(itemdetallesnota.getDescuento()));
                            detalles.setEntregado(Character.valueOf(itemdetallesnota.getEntregado()));
                            detalles.setIva(BigDecimal.valueOf(itemdetallesnota.getIva()));
                            detalles.setNotadepedido(notape);
                            detalles.setPendiente(Character.valueOf(itemdetallesnota.getPendiente()));
                            detalles.setPrecio(BigDecimal.valueOf(itemdetallesnota.getPrecio()));
                            detalles.setProductos(productos);
                            detalles.setSubtotal(BigDecimal.valueOf(itemdetallesnota.getSubtotal()));
                            detalles.setDetallesnotadepedidoPK(detallespk);
                            detalles.setAnulado(Character.valueOf(itemdetallesnota.getAnulado()));
                            detalles.setPreciocondescuento(BigDecimal.valueOf(itemdetallesnota.getPreciocondescuento()));*/
                           
                    }
                    
                    unirRelacion(notape);
//                            Query queryDetailsOrders = em.createNamedQuery("Detallesnotadepedido.findByFkIdnota");
//                            queryDetailsOrders.setParameter("fkIdnota", notape.getId());
//                            notape.setDetallesnotadepedidoList(queryDetailsOrders.getResultList());
//                            em.merge(notape);
                            retorno = notape.getId();
        } catch (Exception e) {
            logger.error("Error en metdodo almacenarDetalleNota "+e.getMessage());
            retorno=-3;
        }finally{
            
            return retorno;
        }
    }
    
    private long almacenarDetalleNotaConControlStock(DatosNotaPedido notadepedido, Notadepedido notape) {
        long retorno =0;
        int stockDisponible=0;
        try{
/* Idem metodo almacenar nota con la inclusion del metodo para controlar el stock llamando
 a metodo remoto de EJBPRoductosRemote, perdon por no usar reusabilidad de codigo fuente, no se me ocurria otra alternativa
 */
            List<Itemdetallesnota>lista = notadepedido.getDetallesnotapedido().getDetallesnota();
            
                    for (Itemdetallesnota itemdetallesnota : lista) {                        
                                Productos productos = em.find(Productos.class,itemdetallesnota.getId_producto());
                                
                                DetallesnotadepedidoPK detallespk = new DetallesnotadepedidoPK(notape.getId(), itemdetallesnota.getId_producto());
                                Detallesnotadepedido detalles = new Detallesnotadepedido();
                                // metodo void para almacenar detalles de la nota de pedido
                                almacenarDetallesNota(productos,itemdetallesnota,notape,detallespk,detalles);                                
                                
                                stockDisponible=producto.controlStockProducto(itemdetallesnota.getId_producto(), itemdetallesnota.getCantidad(), notadepedido.getUsuario_expidio_nota());
                            if(stockDisponible<=50 &&stockDisponible>=0 ) {
                                logger.info("El stock Disponible para el producto "+" está bajando a nivel máximo debe actualizar o agregar mas productos");
                                } else{
                                if (stockDisponible<0) {
                                    logger.info("Ocurrió un Error o hay faltante de stock, valor devuelto por la función "+stockDisponible+" el producto es "+productos.getDescripcion()+" su stock disponible "+productos.getCantidadDisponible().intValue());
                                    }
                            }
                    }
                    unirRelacion(notape);
                                        
                                        
                    retorno = notape.getId();
        }catch(Exception e){
            logger.error("Error en metodo almacenarDetalleNotaConControlStock "+e.getMessage());
            retorno =-1;
        }finally{
            return retorno;
        }
    }
    private long almacenarHistorico(DatosNotaPedido notadepedido,Notadepedido notape) {
        long resultado =0L;
                    GregorianCalendar gc = new GregorianCalendar(Locale.getDefault());
                    
                                    Historiconotapedido historico = new Historiconotapedido();
                                    historico.setAnticipo(BigDecimal.valueOf(notadepedido.getAnticipo()));
                                    historico.setEntregado(notadepedido.getEntregado());
                                    historico.setFecharegistro(gc.getTime());
                                    historico.setFkidnotapedido(em.find(Notadepedido.class, notape.getId()));
                                        if(notadepedido.getObservaciones().length()>0){
                                            historico.setObservaciones(notadepedido.getObservaciones());
                                        }else{
                                            historico.setObservaciones("");
                                        }
                                    historico.setPendiente(notadepedido.getPendiente());
                                    historico.setPorcentajeaplicado(notadepedido.getPorcentajes().getId_porcentaje());
                                    historico.setSaldo(BigDecimal.valueOf(notadepedido.getSaldo()));
                                    historico.setTotal(BigDecimal.valueOf(notadepedido.getMontototal()));
                                    historico.setIdusuarioanulo(notadepedido.getId_usuario_anulado());
                                    historico.setIdusuariocancelo(notadepedido.getUsuario_cancelo_nota());
                                    historico.setIdusuarioentrega(notadepedido.getUsuario_entregado());
                                    historico.setPorcentajedesc(BigDecimal.valueOf(notadepedido.getPorc_descuento_total()));
                                    historico.setIdusuarioexpidio(notadepedido.getUsuario_expidio_nota());
                                    historico.setPorcrecargo(BigDecimal.valueOf(notadepedido.getPorcentajerecargo()));
                                    historico.setTotalapagar(BigDecimal.valueOf(notadepedido.getMontototalapagar()));
                                    historico.setDescuento(BigDecimal.valueOf(notadepedido.getDescuentonota()));
                                    historico.setRecargo(BigDecimal.valueOf(notadepedido.getRecargo()));
                                    historico.setHoraregistro(gc.getTime());
                                    historico.setCancelado(notadepedido.getCancelado());
                                
                                historico.setAnulado(notadepedido.getAnulado());
                                historico.setAccion("Historico Almacenado con exito nota de pedido N "+notape.getId());
                    em.persist(historico);
                    try {
                        Query queryFindByIdProducto = em.createNamedQuery("Historiconotapedido.findByFkidnotapedido");
                        queryFindByIdProducto.setParameter("idnota", notape.getId());
                        notape.setHistoriconotapedidoList(queryFindByIdProducto.getResultList());
                    } catch (Exception e) {
                        logger.error("Error en la queryFindByIdProducto historiconotadepedido, metodo almacenar historico "+e.getMessage());
                        resultado = -2;
                    }
                    em.persist(notape);
                    
                    resultado = historico.getIdhistorico();
        return resultado;
    }

    /**
     *
     * @param idnta
     * @return
     */
    @Override
    public String selectUnaNota(long idnta) {
        String xml = "<Lista>\n";
        try {
            //Si encuentro la nota de pedido devuelve el resultado si no mensaje de nota no encontrada
            Notadepedido nota = em.find(Notadepedido.class, idnta);
            if(nota != null) {               
                xml+=devolverNotaProcesadaSB(nota);
            } else {
                xml+="<nota>nota no encontrada</nota>";
            }
            
        } catch (Exception e) {
            logger.error("Error en metodo selectUnaNota "+e.getMessage());
            
        }finally{
            return xml+="</Lista>";
        }
    }

   

    /**
     *
     * @param idnota
     * @param idusuariocancelo
     * @param estado
     * @return
     */
    @Override
    public long cancelarNotaPedido(long idnota,long idusuariocancelo,int estado) {
        long result = 0L;
        char cancelado ='0';
        try {
            //--------------------------------------------------------------------------
                       GregorianCalendar gc = new GregorianCalendar(Locale.getDefault());
                       SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
            //--------------------------------------------------------------------------
            //--------------------------------------------------------------------------
            Notadepedido nota = em.find(Notadepedido.class,idnota);
            Empleados empleado = em.find(Empleados.class, idusuariocancelo);
            if(estado==1){
                cancelado ='1';
                nota.setCancelado(cancelado);
                nota.setIdusuariocancelo(idusuariocancelo);
                nota.setFecancelado(gc.getTime());
                nota.setUltimaActualizacion(em.find(Empleados.class, idusuariocancelo)+" "+sdf.format(gc.getTime()));
            }else{
                nota.setCancelado(cancelado);
                nota.setIdusuariocancelo(0L);
                nota.setFecancelado(null);
                nota.setUltimaActualizacion(em.find(Empleados.class, idusuariocancelo)+" "+sdf.format(gc.getTime()));
            }
            //--------------------------------------------------------------------------
            List<Detallesnotadepedido>lista = nota.getDetallesnotadepedidoList();
            for (Detallesnotadepedido detallesnotadepedido : lista) {
                detallesnotadepedido.setCancelado(cancelado);
            }
            //--------------------------------------------------------------------------
                            Historiconotapedido historico = new Historiconotapedido();
                            if(estado==1){
                                historico.setAccion("Cancelada por "+empleado.getNameuser());
                                historico.setCancelado(cancelado);
                                historico.setIdusuariocancelo(idusuariocancelo);
                            }else{
                                historico.setAccion("No cancelada por "+empleado.getNameuser());
                                historico.setCancelado(cancelado);
                                historico.setIdusuariocancelo(0L);
                            }
                                       procesarHistorico(historico,gc,nota);
                                 em.persist(historico);
          //----------------------------------------------------------------------------------
                            long notaID = procesaListNotaHistorico(nota);
          //----------------------------------------------------------------------------------
            
                result = notaID;
        } catch (NumberFormatException e) {
            logger.error("Error en metodo cancelaNotaPedido "+e.getMessage());
            result = -1;
        }finally{
            
            return result;
        }
    }
    private long procesaListNotaHistorico(Notadepedido nota) {
        long result = 0L;
        try {
            //-----------------------------------------------------------------
                     try {
                            Query queryFindByIdProducto = em.createNamedQuery("Historiconotapedido.findByFkidnotapedido");
                             queryFindByIdProducto.setParameter("idnota", nota.getId());
                           nota.setHistoriconotapedidoList(queryFindByIdProducto.getResultList());
                      } catch (Exception e) {
                          logger.error("Error en la queryFindByIdProducto historiconotadepedido, metodo almacenar historico ");
                          result = -2;
                      }
                     //-----------------------------------------------------------------
                        em.persist(nota);
                 result = nota.getId();
        } catch (Exception e) {
            logger.error("Error en metodo procesaListNotaHistorico "+e.getMessage());
            result = -1;
        }finally{
            
            return result;
        }
    }

    /**
     *
     * @param idnota
     * @param idusuarioentrega
     * @param estado
     * @return
     */
    @Override
    public long entregarNotaPedido(long idnota, long idusuarioentrega, int estado) {
        long result = 0L;
        char pendiente = '0';
        char entregado ='0';
        try {
            
            //--------------------------------------------------------------------
                GregorianCalendar gc = new GregorianCalendar(Locale.getDefault());
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
            //--------------------------------------------------------------------
                Notadepedido nota = em.find(Notadepedido.class, idnota);
                Empleados empleado = em.find(Empleados.class, idusuarioentrega);
                if(estado==1){
                    entregado ='1';
                    nota.setEntregado(entregado);
                    nota.setFechaentrega(gc.getTime());
                    nota.setPendiente(pendiente);
                    nota.setIdusuarioEntregado(idusuarioentrega);
                    nota.setUltimaActualizacion(em.find(Empleados.class, idusuarioentrega)+" "+sdf.format(gc.getTime()));
                }else{
                    pendiente ='1';
                     nota.setEntregado(entregado);
                     nota.setFechaentrega(null);
                     nota.setPendiente(pendiente);
                     nota.setIdusuarioEntregado(0L);
                     nota.setUltimaActualizacion(em.find(Empleados.class, idusuarioentrega)+" "+sdf.format(gc.getTime()));
                }
                List<Detallesnotadepedido>lista = nota.getDetallesnotadepedidoList();
            for (Detallesnotadepedido detallesnotadepedido : lista) {
                detallesnotadepedido.setEntregado(entregado);
                detallesnotadepedido.setPendiente(pendiente);
            }
            em.persist(nota);
            
                Historiconotapedido historico = new Historiconotapedido();
                        if(estado==1){
                                historico.setAccion("Entregado por "+empleado.getNameuser());
                                historico.setEntregado('1');
                                historico.setPendiente('0');
                        }else{
                                historico.setAccion("No entregada por "+empleado.getNameuser());
                                historico.setEntregado('0');
                                historico.setPendiente('1');
                        }
                        
                        procesarHistorico(historico,gc,nota);
                                        
                                 em.persist(historico);
                //----------------------------------------------------------------------------------
                            long notaID = procesaListNotaHistorico(nota);
          //----------------------------------------------------------------------------------
                            
                       result = notaID;
        } catch (NumberFormatException e) {
            logger.error("Error en metodo entregarNotaPedido "+e.getMessage());
            result = -1;
        }finally{
            
           return result;
        }
    }
//-----------------------------------------------------------------------------------------------



    /**
     *
     * @param desde
     * @param hasta
     * @param idvendedor
     * @return
     */
    @Override
    public String selectNotaEntreFechasCompra(String desde, String hasta,long idvendedor) {
        String xml ="<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"+"<Lista>\n";
        List<Notadepedido>lista = null;
        try {
            
            String resultProcFechas=chequearFechas(desde,hasta);
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            if(resultProcFechas.equals("TODO OK")){    
                Query jpasql=em.createQuery("SELECT n FROM Notadepedido n WHERE  "
                        + "n.fechadecompra BETWEEN ?1 and ?2 AND n.entregado = ?3 AND n.pendiente = ?4 ORDER BY n.id desc",Notadepedido.class);                
                jpasql.setParameter("1", sdf.parse(desde),TemporalType.TIMESTAMP);
                jpasql.setParameter("2", sdf.parse(hasta),TemporalType.TIMESTAMP);
                jpasql.setParameter("3", '0');
                jpasql.setParameter("4", '1');
                   
                                                    lista= jpasql.getResultList();
////                                                    StringBuilder xmlLoop = new StringBuilder();
                                                    if(lista.size()>0){
//                                                            for (Notadepedido notadepedido1 : lista) {
//                                                                xmlLoop.append(notadepedido1.toXML());
//                                                            }
                                                          xml+=iterateOverListNote(lista);
                                                          xml+=agregarDatosAlxml(xml,desde,hasta);
                                                    }else {
                                                        xml+="<result>lista vacia</result>\n";
                    }
            }else {
                xml+=resultProcFechas;
            }
            
            
            
        } catch(ParseException e){
            
            xml+="<error>Error</error>\n";
            logger.error("Error en metodo selectnotaEntreFechas",e.fillInStackTrace());
        }finally{
                return xml+="</Lista>\n";
        }
    }

    /**
     *
     * @return
     */
    @Override
    public int getRecorCountNotas() {
        int retorno =0;
        try {
            Query notas = em.createNamedQuery("Notadepedido.findAll");
            retorno =notas.getResultList().size();
        } catch (Exception e) {
            retorno =-1;
            logger.error("Error en metodo getRecorCountNotas ");
        }finally{
            
            return retorno;
        }
    }

    /**
     *
     * @return
     */
    @Override
    public String selectAllNotas() {
        String lista = "\"<Lista>\\n\"";
        List<Notadepedido>result=null;
        try {
            
            
            Query queryFindByIdProducto =em.createQuery("Select n From Notadepedido n ORDER BY n.id DESC, "
                    + "n.fechadecompra DESC,n.fkIdcliente.idPersona",Notadepedido.class);
            
            result = queryFindByIdProducto.getResultList();
            
            if(result.isEmpty()) {
                lista+="LA CONSULTA NO ARROJÓ RESULTADOS";
            } else{
                for (Notadepedido notape : result) {
                    lista+=devolverNotaProcesadaSB(notape);
                }
            }
           
        } catch (Exception e) {
            
            logger.error("Error en metodo selecAllNotas "+e.getMessage());
        }finally{                                
                return lista+="</Lista>";
        }
    }
    private StringBuilder devolverNotaProcesadaSB(Notadepedido nota) {
                long idusuarioexpidio=0;
                String usuarioexpidio ="";
                long idusuarioanulonota=0;
                String usuarioanulonota ="";
                long idusuarioentregonota=0;
                String usuarioentregonota ="";
                long idusuariocancelonota =0;
                String usuariocancelonota="";
                StringBuilder sb=null;
        try {
                
                try {
                    sb=new StringBuilder();
                    sb.append(nota.toXML());
                } catch (Exception e) {
                    logger.error("error al convertir StringBuilder "+e.getMessage());
                }
                
               
            if(nota.getIdUsuarioExpidioNota()>0){
                
                 idusuarioexpidio = (long) nota.getIdUsuarioExpidioNota();
                
                Empleados persona = em.find(Empleados.class, idusuarioexpidio);
                
                usuarioexpidio=persona.getNombre();
                
                sb.replace(sb.indexOf("dionota>")+8, sb.indexOf("</usuarioex"), usuarioexpidio);
                
            }
                    if(nota.getIdusuarioAnulado()>0){
              
                        idusuarioanulonota = nota.getIdusuarioAnulado();
                        
                        Empleados persona = em.find(Empleados.class, idusuarioanulonota);
                        
                        usuarioanulonota=persona.getNombre();
                        
                        sb.replace(sb.indexOf("anulonota>")+10, sb.indexOf("</usuarioanul"), usuarioanulonota.toUpperCase());
                        
                    }
                            if(nota.getIdusuarioEntregado()>0){
                          
                                idusuarioentregonota=nota.getIdusuarioEntregado();
                                
                                Empleados persona = em.find(Empleados.class, idusuarioentregonota);
                                
                                usuarioentregonota=persona.getNombre();
                                
                                sb.replace(sb.indexOf("oentregonota>")+13, sb.indexOf("</usuarioent"), usuarioentregonota.toUpperCase());
                                
                            }
                                if(nota.getIdusuariocancelo()>0){
                            
                                        idusuariocancelonota=nota.getIdusuariocancelo();
                                       
                                        Empleados persona = em.find(Empleados.class, idusuariocancelonota);
                                       
                                        usuariocancelonota=persona.getNombre();
                                        
                                        sb.replace(sb.indexOf("ocancelonota>")+13, sb.indexOf("</usuariocan"), usuariocancelonota.toUpperCase());
                                       
                                }
        } catch (Exception e) {
            logger.error("Error en metodo devolverNotaProcesadaSB "+e.getMessage());
        }finally{
            return sb;
        }
    }

    /**
     *
     * @param index
     * @param recordCount
     * @return
     */
    @Override
    
    public String verNotasPedidoPaginadas(int index, int recordCount) {
        String result = "\"<Lista>\\n\"";
        try {
            
            
            Query queryFindByIdProducto = em.createNamedQuery("Notadepedido.searchAllOrderDesc",Notadepedido.class);
             queryFindByIdProducto.setMaxResults(recordCount);
             queryFindByIdProducto.setFirstResult(index*recordCount);
            List<Notadepedido>lista = queryFindByIdProducto.getResultList();
            
            if(lista.isEmpty()) {
                result+="LA CONSULTA NO ARROJÓ RESULTADOS!!!";
            } else{
                    StringBuilder processNote = new StringBuilder(10);
                for (Notadepedido notape : lista) {   
                    
                    processNote.append(devolverNotaProcesadaSB(notape));
                }
                result+=processNote;
            }
            
        } catch (Exception e) {
            
            logger.error("Error en metodo vernotasPedidoPaginadas "+e.getMessage());
        }finally{
            result+="</Lista>";
            
        return result;
        }
    }

    /**
     *
     * @param idnota
     * @param idusuario
     * @param estado
     * @return
     */
    @Override
    public long anularNotaPedido(long idnota, long idusuario, int estado) {
        long result = 0L;
        char anulada ='0';
        try {
            //--------------------------------------------------------------------
                GregorianCalendar gc = new GregorianCalendar(Locale.getDefault());
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
            //--------------------------------------------------------------------
                Notadepedido nota = em.find(Notadepedido.class,idnota);
                Empleados empleado = em.find(Empleados.class,idusuario);
                if(estado==1){
                    anulada ='1';
                    nota.setFechaAnulado(gc.getTime());
                    nota.setIdusuarioAnulado(idusuario);
                    nota.setAnulado(anulada);
                    nota.setUltimaActualizacion(em.find(Empleados.class, idusuario)+" "+sdf.format(gc.getTime()));
                }else{
                     nota.setAnulado(anulada);
                     nota.setFechaAnulado(null);
                     nota.setIdusuarioAnulado(0L);
                     nota.setUltimaActualizacion(em.find(Empleados.class, idusuario)+" "+sdf.format(gc.getTime()));
                }
                List<Detallesnotadepedido>lista = nota.getDetallesnotadepedidoList();
            for (Detallesnotadepedido detallesnotadepedido : lista) {
                detallesnotadepedido.setAnulado(anulada);
            }
                Historiconotapedido historico = new Historiconotapedido();
                        if(estado==1){
                                historico.setAccion("Nota anulada "+empleado.getNameuser());
                                historico.setAnulado(anulada);
                                historico.setIdusuarioanulo(idusuario);
                        }else{
                                historico.setAccion("NOTa no anulada por "+empleado.getNameuser());
                                historico.setAnulado(anulada);
                                historico.setIdusuarioanulo(0L);
                        }
                                procesarHistorico(historico,gc,nota);
                                
                                em.persist(historico);
                //----------------------------------------------------------------------------------
                            long notaID = procesaListNotaHistorico(nota);
          //----------------------------------------------------------------------------------
                            
                       result = notaID;
        } catch (NumberFormatException e) {
            logger.error("Error en metodo anularNotaPedido "+e.getMessage());
            result = -1;
        }finally{
            
            return result;
        }
    }

    /**
     *
     * @param xmlnotapedidomodificada
     * @return
     */
    @Override
    public long actualizarNotaPedido(String xmlnotapedidomodificada) {
        DatosNotaPedido datosnotapedido;
        long retorno =0L;
        try {
            
            if(!xmlnotapedidomodificada.isEmpty()){
                datosnotapedido=xestreaNotapedido(xmlnotapedidomodificada);
                if(datosnotapedido.getIdnota()>0){
                    Notadepedido nota = em.find(Notadepedido.class, datosnotapedido.getIdnota());
                    retorno = procesarNotaaActualizar(datosnotapedido,nota);
                }else {
                    retorno =-2;
                }
            }else{
                retorno = -5;
            }
        } catch (Exception e) {
            retorno =-1;
            logger.error("Error en metodo actualizarNotapedido"+e.getMessage());
        }finally{
            
            return retorno;
        }
    }
    private long procesarNotaaActualizar(DatosNotaPedido datosnotapedido, Notadepedido nota) {
        long result =-3;
        try {
              //---------------------------------------------------------------------------------
            GregorianCalendar gc = new GregorianCalendar();
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
            //---------------------------------------------------------------------------------
            
                                           
                                            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                                            Clientes cliente =em.find(Clientes.class, datosnotapedido.getPersonas().getId());
                                                Double montotoalapagar = nota.getMontototalapagar().doubleValue();
                                                Double restomontoapagar = cliente.getTotalCompras().doubleValue()- montotoalapagar;
                                                cliente.setTotalCompras(BigDecimal.valueOf(restomontoapagar+datosnotapedido.getMontototalapagar()));
                                                em.merge(cliente);
                                            //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                                             
                                                        nota.setAnulado(datosnotapedido.getAnulado());
                                                        nota.setEnefectivo(datosnotapedido.getEnefectivo());
                                                        nota.setEntregado(datosnotapedido.getEntregado());
                                                        nota.setFkIdcliente(cliente);
                                                        nota.setFkidporcentajenotaId(em.find(Porcentajes.class, 
                                                                datosnotapedido.getPorcentajes().getId_porcentaje()));
                                                        nota.setIdTarjetaFk(em.find(TarjetasCreditoDebito.class, 
                                                                datosnotapedido.getTarjetacredito().getId_tarjeta()));
                                                        nota.setIdUsuarioExpidioNota(datosnotapedido.getUsuario_expidio_nota());
                                                        nota.setIdusuarioAnulado(datosnotapedido.getId_usuario_anulado());
                                                        nota.setIdusuarioEntregado(datosnotapedido.getUsuario_entregado());
                                                        nota.setMontoiva(BigDecimal.valueOf(datosnotapedido.getMontoiva()));
                                                        nota.setNumerodecupon(datosnotapedido.getNumerodecupon());
                                                        if(datosnotapedido.getObservaciones().length()>0){
                                                            nota.setObservaciones(datosnotapedido.getObservaciones());
                                                        }else{
                                                            nota.setObservaciones("");
                                                        }
                                                        nota.setUltimaActualizacion(em.find(Empleados.class, datosnotapedido.getIdVendedor()).getNameuser()+" "+sdf.format(gc.getTime()));
                                                        nota.setPendiente(datosnotapedido.getPendiente());
                                                        nota.setRecargo(BigDecimal.valueOf(datosnotapedido.getRecargo()));
                                                        nota.setSaldo(BigDecimal.valueOf(datosnotapedido.getSaldo()));
                                                        nota.setStockfuturo(datosnotapedido.getStockfuturo());
                                                        nota.setTotal(BigDecimal.valueOf(datosnotapedido.getMontototal()));
                                                        nota.setFechadecompra(DateFormat.getDateInstance().parse(datosnotapedido.getFechacompra()));
                                                        nota.setFechaentrega(DateFormat.getDateInstance().parse(datosnotapedido.getFechaentrega()));
                                                        nota.setCancelado(datosnotapedido.getCancelado());
                                                        nota.setDescuentonota(BigDecimal.valueOf(datosnotapedido.getDescuentonota()));
                                                        nota.setDescuentoPesos(BigDecimal.valueOf(datosnotapedido.getDescuentopesos()));
                                                        nota.setIdusuariocancelo(datosnotapedido.getUsuario_cancelo_nota());
                                                        
                                                            try {
                                                                nota.setMontototalapagar(BigDecimal.valueOf(datosnotapedido.getMontototalapagar()));
                                                            } catch (Exception ex) {
                                                                logger.error("Error al seteal el montotoal a pagar ");
                                                            }
                                                            nota.setPorcdesctotal(BigDecimal.valueOf(datosnotapedido.getPorc_descuento_total()));
                                                            nota.setPorcrecargo(BigDecimal.valueOf(datosnotapedido.getPorcentajerecargo()));
                                                                    if(datosnotapedido.getCancelado()=='1') {
                                                                        nota.setFecancelado(gc.getTime());
                                                                    }
                                                                    if(datosnotapedido.getAnulado()=='1') {
                                                                        nota.setFechaAnulado(gc.getTime());
                                                                   }
                                                
                                                   em.persist(nota);     
                                                       // backupLogListDetalleNotaAntesdeBorrar(nota);
                                  //-------------------DETALLE---------------------------------------------------
                                                List<Itemdetallesnota>lista = datosnotapedido.getDetallesnotapedido().getDetallesnota();
                                                //----------------------------------------------------------------------------------
                                                
                                                Query deletsql = em.createNamedQuery("Detallesnotadepedido.deleteById");
                                                deletsql.setParameter("idNota", nota.getId());
                                                int retorno = deletsql.executeUpdate();
                                                
                                                Query queryDetailsOrders1 = em.createNamedQuery("Detallesnotadepedido.findByFkIdnota");
                                                queryDetailsOrders1.setParameter("fkIdnota", nota.getId());
                                                
                                                 Productos productos=null;
                                                 Itemdetallesnota itemdetallesnota=null;
                                                    for (Iterator<Itemdetallesnota> it = lista.iterator(); it.hasNext();) {
                                                         itemdetallesnota= it.next();
                                                         productos =em.find(Productos.class, itemdetallesnota.getId_producto());
                                                            DetallesnotadepedidoPK pkdetalle = new DetallesnotadepedidoPK(itemdetallesnota.getId_nota()
                                                                                                        ,itemdetallesnota.getId_producto());
                                                                                Detallesnotadepedido detallesnotadepedido = new Detallesnotadepedido();
                                                                                
                                                           almacenarDetallesNota(productos, itemdetallesnota,nota, pkdetalle, detallesnotadepedido);                     
                                                                                
//                                                                                detallesnotadepedido.setAnulado(itemdetallesnota.getAnulado());
//                                                                                detallesnotadepedido.setCancelado(itemdetallesnota.getCancelado());
//                                                                                detallesnotadepedido.setCantidad(itemdetallesnota.getCantidad());
//                                                                                
//                                                                                detallesnotadepedido.setDescuento(BigDecimal.valueOf(
//                                                                                        itemdetallesnota.getDescuento()));
//                                                                                detallesnotadepedido.setDetallesnotadepedidoPK(pkdetalle);
//                                                                                detallesnotadepedido.setEntregado(Character.valueOf(
//                                                                                        itemdetallesnota.getEntregado()));
//                                                                                detallesnotadepedido.setIva(BigDecimal.valueOf(itemdetallesnota.getIva()));
//                                                                                detallesnotadepedido.setNotadepedido(em.find(Notadepedido.class, itemdetallesnota.getId_nota()));
//                                                                                detallesnotadepedido.setPendiente(Character.valueOf(
//                                                                                        itemdetallesnota.getPendiente()));
//                                                                                detallesnotadepedido.setPrecio(BigDecimal.valueOf(
//                                                                                        itemdetallesnota.getPrecio()));
//                                                                                detallesnotadepedido.setPreciocondescuento(BigDecimal.valueOf(
//                                                                                        itemdetallesnota.getPreciocondescuento()));
//                                                                                detallesnotadepedido.setProductos(productos);
//                                                                                detallesnotadepedido.setSubtotal(BigDecimal.valueOf(
//                                                                                        itemdetallesnota.getSubtotal()));
//                                                                                em.persist(detallesnotadepedido);
//                                                                                
//                                                                                
//                                                           ///+++++++++++++++++++++++++++++++++++++++++++++++++++
//                                                  List<Detallesnotadepedido> queryDetailsOrdersFindProduc= 
//                                                          em.createNamedQuery("Detallesnotadepedido.findByFkIdproducto")
//                                                            .setParameter("fkIdproducto", itemdetallesnota.getId_producto()).getResultList();
//                                                            productos.setDetallesnotadepedidoList(queryDetailsOrdersFindProduc);
//                                                            em.merge(productos);
                                                            
                                                  ///*****************************************************+
                                                     }//end for
                                            List<Detallesnotadepedido> queryDetailsOrdersFindIdOrder= em.createNamedQuery(
                                                    "Detallesnotadepedido.findByFkIdnota")
                                               .setParameter("fkIdnota", nota.getId()).getResultList();
                                               nota.setDetallesnotadepedidoList(queryDetailsOrdersFindIdOrder);
                                               em.merge(nota);
                                if(datosnotapedido.getAnticipoacum()!=nota.getAnticipo().doubleValue()){
                                    nota.setAnticipo(BigDecimal.valueOf(datosnotapedido.getAnticipoacum()));
                                    result =  almacenarHistorico(datosnotapedido,nota);
                                }
                           
              
              
                  result=nota.getId();
                                  //-----------------------------------------------------------------------------
        } catch (ParseException e) {
            result=-4;
            logger.error("Error en metodo procesarNotaaActualizar "+e.getMessage());
        }finally{            
            return result;
        }
    }

    /**
     *
     * @param fecha1
     * @param fecha2
     * @param idvendedor
     * @return
     */
    @Override
    public String selecNotaEntreFechasEntrega(String fecha1, String fecha2, long idvendedor) {
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"+"<Lista>\n";
        List<Notadepedido>lista = null;
        try {
            
            String resultchequearFechas=chequearFechas(fecha1, fecha2);
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            if(resultchequearFechas.equals("TODO OK")){
                Query jpasql = em.createQuery("SELECT n FROM Notadepedido n WHERE n.fechaentrega "
                        + "BETWEEN ?1 AND ?2 AND n.entregado = ?3 and n.pendiente = ?4 ORDER BY n.horacompra,n.id desc",Notadepedido.class);
                jpasql.setParameter("1", sdf.parse(fecha1),TemporalType.TIMESTAMP);
                jpasql.setParameter("2", sdf.parse(fecha2),TemporalType.TIMESTAMP);
                jpasql.setParameter("3", '0');
                jpasql.setParameter("4", '1');
                        
                            lista= jpasql.getResultList();
                            if(lista.size()>0){
                                
                                
                                    
//                                    StringBuilder xmlLoop = new StringBuilder();
//                                    for (Notadepedido notadepedido1 : lista) {
//                                        xmlLoop.append(notadepedido1.toXML());
//                                   }
                                
                                    
                                 xml+=iterateOverListNote(lista);
                                 xml+=agregarDatosAlxml(xml, fecha1, fecha2);
                            }else {
                                xml+="<result>lista vacia</result>\n";
                        }
            }else {
                xml+=resultchequearFechas;
            }
                         
        } catch (ParseException e) {
            xml+="<error>Error</error>\n";
            logger.error("Error en metodo selectnotaEntreFechasEntrega "+e.getMessage());
        }finally{                
                return  xml+="</Lista>\n";  
        }
    }


    /**
     *
     * @param idnota
     * @param idEmpleado
     * @return
     */
    @Override
    public int eliminarNotaDePedido(long idnota, long idEmpleado) {
        int idRetorno=0;
        try {
           
            Query queryFindByIdProducto=em.createQuery("SELECT n FROM Notadepedido n WHERE n.id = :id");
            queryFindByIdProducto.setParameter("id", idnota);
            
            if(!queryFindByIdProducto.getResultList().isEmpty()){
                Notadepedido nota =em.find(Notadepedido.class, idnota);
                em.remove(nota);
                
                idRetorno=1;
                GregorianCalendar gc = new GregorianCalendar();
                logger.info(new StringBuilder("El Empleado ")
                        .append(em.find(Empleados.class, idEmpleado))
                        .append(" elimino la nota de pedido N° ")
                        .append(idnota).append(" el dia ")
                        .append(gc.getTime()).toString());
            }else {
                idRetorno=-2;
            }

        } catch (Exception e) {
            idRetorno=-1;
            logger.error("Error en metodo eliminarNotaDePedido "+e.getMessage());
        }finally{            
            return idRetorno;
        }
    }

    /**
     *
     * @return
     */
    @Override
    @SuppressWarnings("FinallyDiscardsException")
    public String calcularVentasMensualesHastaFechaYAnoActual() {
        String xml = "<Lista>\n";
        try {
            GregorianCalendar gc = new GregorianCalendar();
            SimpleDateFormat sdf = new SimpleDateFormat("MM");
            String year = new SimpleDateFormat("YYYY").format(gc.getTime());       
           int month=0;
           StringBuilder xmlLoop = new StringBuilder(10);
            for (int i = 0; i < Integer.valueOf(sdf.format(gc.getTime())); i++) {
                            month++;
                            String ventaMensual="0";
                            Query queryFindByIdProducto = em.createQuery("SELECT SUM(n.montototalapagar) "
                                    + "FROM Notadepedido n WHERE SQL('EXTRACT(MONTH FROM ?)',n.fechadecompra) = ?1 "
                                    + "AND SQL('EXTRACT(YEAR FROM ?)',n.fechadecompra) = ?2");
                            queryFindByIdProducto.setParameter("1", month);
                            queryFindByIdProducto.setParameter("2", year);
                            ventaMensual=queryFindByIdProducto.getResultList().toString().replace("[", "").replace("]", "");
                            xmlLoop.append("<Item>\n");
                            switch(i){
                                case 0:xmlLoop.append("<month>ENE</month>\n");
                                    break;
                                case 1:xmlLoop.append("<month>FEB</month>\n");    
                                    break;
                                case 2:xmlLoop.append("<month>MAR</month>\n");       
                                    break;
                                case 3:xmlLoop.append("<month>ABR</month>\n");       
                                    break;
                                case 4:xmlLoop.append("<month>MAY</month>\n");       
                                    break;
                                case 5:xmlLoop.append("<month>JUN</month>\n");       
                                    break;
                                case 6:xmlLoop.append("<month>JUL</month>\n");       
                                    break;
                                case 7:xmlLoop.append("<month>AGO</month>\n");       
                                    break;
                                case 8:xmlLoop.append("<month>SEP</month>\n");       
                                    break;
                                case 9:xmlLoop.append("<month>OCT</month>\n");       
                                    break;    
                                case 10:xmlLoop.append("<month>NOV</month>\n");       
                                    break;        
                                case 11:xmlLoop.append("<month>DIC</month>\n");       
                                            
                            }
                            xml+=xmlLoop;
                                    
                                  if(!"null".equals(ventaMensual)) {
                                      xml+="<totalMonthlySales>"+ventaMensual+"</totalMonthlySales>\n";
                            } else {
                                      xml+="<totalMonthlySales>0</totalMonthlySales>\n";
                            }
                                    xml+="</Item>\n";
                
            }
            xml+="</Lista>";
            
        } catch (NumberFormatException e) {
            logger.error("Error en metodo calcularVentasMensualesHastaFechaYAnoActual "+e.getMessage());
        }finally{
            
            return xml;
        }
    }

    private String chequearFechas(String fecha1, String fecha2) {
        String xml = null;
    
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            if(fecha1 == null) {
                xml+="<error>Error fecha vacia</error>\n";
            } else {
                if(fecha2==null) {
                    xml+="<error>Error fecha vacia</error>\n";
                } else{
                    if(fecha1.trim().length()!=sdf.toPattern().length()) {
                        xml+="<error>Error fecha1 con patron desconocido o incorrecto</error>\n";
                    } else{
                        if(fecha2.trim().length()!=sdf.toPattern().length()) {
                            xml+="<error>Error fecha2 con patron desconocido o incorrecto</error>\n";
                        } else{
                                sdf.setLenient(false);
                                    try {
                                        sdf.parse(fecha1.trim());
                                        sdf.parse(fecha2.trim());
                                        

                                           if(sdf.parse(fecha1).compareTo(sdf.parse(fecha2))<=0) {
                                              xml+="TODO OK";
                                           } else {                                   
                                              xml+="<result>rango no correcto de fechas elegido</result>\n";
                                            }


                                        
                                    } catch (ParseException e) {
                                        xml+="<error>Error en parse de fechas</error>\n";                            
                                        logger.error("Error en parseo de fechas ");
                                    }
                        }
                    }
                }
            }
        } catch (Exception e) {
                xml+="<error>Error en metodo chequeo fechas</error>";
                logger.error("Error en metodo cheque de fechas "+e.getMessage());
                
        }finally{
            return xml;
        }
    }

    private StringBuilder agregarDatosAlxml(String xml,String fecha1,String fecha2) {
        
         fecha1=fecha1.substring(3, 5)+"/"+fecha1.substring(0, 2)+"/"+fecha1.substring(6, 10);
         
                                                                fecha2=fecha2.substring(3, 5)+"/"+fecha2.substring(0, 2)+"/"+fecha2.substring(6, 10);
                                                                
                                                                StringBuilder sb =new StringBuilder(xml);
                                                                
                                                                String periodoqueryFindByIdProductodo = 
                                                                        "<fechainicio>" + fecha1 + "</fechainicio>\n" + "<fechafinal>" + fecha2 + "</fechafinal>\n";
                                                                
                                                                 sb.replace(sb.indexOf("</numerocupon>")+14, sb.indexOf("<observaciones>")
                                                                         , "\n"+periodoqueryFindByIdProductodo);
                                                                 
      return sb;
    }

    

    

    private void almacenarDetallesNota(Productos productos, Itemdetallesnota itemdetallesnota, Notadepedido notape, DetallesnotadepedidoPK detallespk, Detallesnotadepedido detalles) {
        
                                detalles.setCancelado(itemdetallesnota.getCancelado());
                                detalles.setCantidad(itemdetallesnota.getCantidad());
                                detalles.setDescuento(BigDecimal.valueOf(itemdetallesnota.getDescuento()));
                                detalles.setPreciocondescuento(BigDecimal.valueOf(itemdetallesnota.getPreciocondescuento()));
                                detalles.setEntregado(itemdetallesnota.getEntregado());
                                detalles.setIva(BigDecimal.valueOf(itemdetallesnota.getIva()));
                                detalles.setNotadepedido(notape);
                                detalles.setPendiente(itemdetallesnota.getPendiente());
                                detalles.setPrecio(BigDecimal.valueOf(itemdetallesnota.getPrecio()));
                                detalles.setProductos(productos);
                                detalles.setSubtotal(BigDecimal.valueOf(itemdetallesnota.getSubtotal()));
                                detalles.setDetallesnotadepedidoPK(detallespk);
                                
                                
                                em.persist(detalles);
                                
                                    Query queryFindByIdProducto = em.createNamedQuery("Detallesnotadepedido.findByFkIdproducto");
                                        queryFindByIdProducto.setParameter("fkIdproducto", itemdetallesnota.getId_producto());
                                        productos.setDetallesnotadepedidoList(queryFindByIdProducto.getResultList());
                                        
                                         
                               
    }

    private void unirRelacion(Notadepedido notape) {
        Query queryDetailsOrders = em.createNamedQuery("Detallesnotadepedido.findByFkIdnota");
                                        queryDetailsOrders.setParameter("fkIdnota", notape.getId());
                                        notape.setDetallesnotadepedidoList(queryDetailsOrders.getResultList());
                                        em.merge(notape);
    }

    

    private void procesarHistorico(Historiconotapedido historico, GregorianCalendar gc, Notadepedido nota) {
                                        historico.setAnticipo(BigDecimal.ZERO);                                
                                        historico.setFecharegistro(gc.getTime());
                                        historico.setFkidnotapedido(nota);
                                        historico.setHoraregistro(gc.getTime());
                                        historico.setPendiente('0');
                                        historico.setEntregado('0');
                                        historico.setIdusuarioanulo(0L);
                                        historico.setIdusuarioentrega(0L);
                                        historico.setIdusuarioexpidio(0L);
                                        historico.setObservaciones("");
                                        historico.setPorcentajeaplicado(Short.valueOf("0"));
                                        historico.setSaldo(BigDecimal.ZERO);
                                        historico.setTotal(BigDecimal.ZERO);
                                        historico.setTotalapagar(BigDecimal.ZERO);
                                        historico.setRecargo(BigDecimal.ZERO);
                                        historico.setPorcrecargo(BigDecimal.ZERO);
                                        historico.setPorcentajedesc(BigDecimal.ZERO);
                                        historico.setDescuento(BigDecimal.ZERO);
                                        historico.setAnulado('0');
    }

    private void almacenarNotaVoid(Notadepedido notape, DatosNotaPedido notadepedido, SimpleDateFormat sdf, GregorianCalendar gc) throws ParseException {
        
                                            notape.setAnticipo(BigDecimal.valueOf(notadepedido.getAnticipo()));
                                            notape.setAnulado(notadepedido.getAnulado());
                                            notape.setEnefectivo(notadepedido.getEnefectivo());
                                            notape.setPendiente(notadepedido.getPendiente());
                                            notape.setEntregado(notadepedido.getEntregado());
                                            notape.setFkIdcliente(em.find(Personas.class, notadepedido.getPersonas().getId()));
                                            notape.setFkidporcentajenotaId(em.find(Porcentajes.class, notadepedido.getPorcentajes().getId_porcentaje()));                
                                            notape.setIdTarjetaFk(em.find(TarjetasCreditoDebito.class, notadepedido.getTarjetacredito().getId_tarjeta()));
                                            notape.setIdUsuarioExpidioNota(notadepedido.getUsuario_expidio_nota());
                                            notape.setIdusuarioAnulado(notadepedido.getId_usuario_anulado());
                                            notape.setIdusuarioEntregado(notadepedido.getUsuario_entregado());
                                            notape.setMontoiva(BigDecimal.valueOf(notadepedido.getMontoiva()));
                                            notape.setNumerodecupon(notadepedido.getNumerodecupon());
                                            if(notadepedido.getObservaciones().length()>0){
                                                notape.setObservaciones(notadepedido.getObservaciones());
                                            }else{
                                                notape.setObservaciones("");
                                            }
                                            notape.setRecargo(BigDecimal.valueOf(notadepedido.getRecargo()));
                                            notape.setSaldo(BigDecimal.valueOf(notadepedido.getSaldo()));
                                            notape.setStockfuturo(notadepedido.getStockfuturo());
                                            notape.setTotal(BigDecimal.valueOf(notadepedido.getMontototal()));
                                            notape.setHoracompra(gc.getTime());
                                            notape.setFechadecompra(gc.getTime());                                               
                                            notape.setFechaentrega(sdf.parse(notadepedido.getFechaentrega()));                                 
                                            notape.setCancelado(notadepedido.getCancelado());
                                            notape.setDescuentonota(BigDecimal.valueOf(notadepedido.getDescuentonota()));
                                            notape.setDescuentoPesos(BigDecimal.valueOf(notadepedido.getDescuentopesos()));
                                            notape.setUltimaActualizacion(FECHA_DEFAULT);
                                            notape.setIdusuariocancelo(notadepedido.getUsuario_cancelo_nota());
                                                    try {
                                                        notape.setMontototalapagar(BigDecimal.valueOf(notadepedido.getMontototalapagar()));
                                                    } catch (Exception e) {
                                                        logger.error("Error en metodo almacenar nota, monto total a pagar "+e.getMessage());
                                                    }
                                            notape.setPorcdesctotal(BigDecimal.valueOf(notadepedido.getPorc_descuento_total()));
                                            notape.setPorcrecargo(BigDecimal.valueOf(notadepedido.getPorcentajerecargo()));
                                                    if(notadepedido.getCancelado()=='1') {
                                                        notape.setFecancelado(gc.getTime());
                                                    }else{
                                                        notape.setFecancelado(sdf.parse(FECHA_DEFAULT));
                                                        
                                                    }
                                                    if(notadepedido.getAnulado()=='1') {
                                                        notape.setFechaAnulado(gc.getTime());
                                                    }else{
                                                        notape.setFechaAnulado(sdf.parse(FECHA_DEFAULT));                                            
                                                    }
    }

//    private StringBuilder iterateNote(List<Notadepedido> lista) {
//                            StringBuilder xmlLoop = new StringBuilder(10);
//                                    for (Notadepedido notadepedido1 : lista) {
//                                        xmlLoop.append(notadepedido1.toXML());
//                                   }
//                                    return xmlLoop;
//    }

    
    private StringBuilder iterateOverListNote(List<Notadepedido> lista) {
        StringBuilder xmlLoop = new StringBuilder(10);
                                    for (Notadepedido notadepedido1 : lista) {
                                        xmlLoop.append(notadepedido1.toXML());
                                   }
                                    return xmlLoop;
    }

    

    

    

   

    

    
    
}