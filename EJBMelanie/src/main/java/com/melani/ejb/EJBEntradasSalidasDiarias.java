package com.melani.ejb;
import com.melani.entity.EntradasySalidasCaja;
import com.melani.entity.Notadepedido;
import com.melani.entity.TarjetasCreditoDebito;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
@Stateless
@WebService(serviceName = "ServiceEntradasySalidas")
public class EJBEntradasSalidasDiarias implements EJBEntradasSalidaDiariasRemote{
    @PersistenceContext
    private EntityManager em;    
    @Override
    public String searchAllEntradasySalidasForCurrentDate() {
        String xmlEntradasySalidas = "<Lista>\n";
        Query query = em.createNamedQuery("EntradasySalidasCaja.findByCurrentDate");
        List<EntradasySalidasCaja>lista = query.getResultList();
        for (EntradasySalidasCaja entradasySalidasCaja : lista) {
            xmlEntradasySalidas+=entradasySalidasCaja.toXML();
        }  
        return xmlEntradasySalidas+="</Lista>\n";
    }

    @Override
    public byte insertarEntradaSalidaManual(String monto, byte valorEntradaSalidaBit,String detalle) {
        byte retorno; 
        EntradasySalidasCaja entradaSalida = new EntradasySalidasCaja();
        SimpleDateFormat sdfHora = new SimpleDateFormat("hh:mm:ss");    
                entradaSalida.setNumerocupon("0");        
                entradaSalida.setFecha(new Date());
                entradaSalida.setHora(sdfHora.getCalendar().getTime());        
                entradaSalida.setID_USUARIO(0);
                entradaSalida.setAnticipo(0.0);
                entradaSalida.setEnefectivo('1');
                entradaSalida.setEntradaTarjeta(0.0);
                entradaSalida.setIdTarjetaCreditoDebitoFk(em.find(TarjetasCreditoDebito.class, 0));
                entradaSalida.setID_USUARIO(0);
                entradaSalida.setNumerocupon("0");
                entradaSalida.setVentasEfectivo(0.0);                
            switch(valorEntradaSalidaBit){
                case 0:{
                    entradaSalida.setEntradas(0.00);
                    entradaSalida.setSalidas(Double.valueOf(monto));
                    entradaSalida.setDetalles((detalle!=null) ? detalle : "Salida Manual");
                    break;
                }default:{
                    entradaSalida.setEntradas(Double.valueOf(monto));
                    entradaSalida.setSalidas(0.00);
                    entradaSalida.setDetalles((detalle!=null)?detalle:"Entrada Manual");
                }
            }     
        em.persist(entradaSalida);
        retorno = 1;
        return (byte) retorno;
    }

    @Override
    public String selectAllEntradasYSalidasPorTurno() {
        String retorno = "<Lista>\n";
        LocalDateTime timePoint = LocalDateTime.now(ZoneId.systemDefault());                        
                            if(timePoint.getHour()>7 && timePoint.getHour()<15){        
                                ///consulto las entradas y salidas de este horario y la fecha
                                    retorno += obtenerEntradasYSalidasDeMañana();                                
                            } else {
                                if(timePoint.getHour()>15&&timePoint.getHour()<22){                                    
                                        retorno += obtenerEntradasYSalidasDeTarde();                                    
                                }
                            }        
            retorno+="</Lista>";             
      return retorno;  
    }
    private String obtenerEntradasYSalidasDeMañana() {
        String horaManana1 = ResourceBundle.getBundle("config").getString("HORA_MANANA1");
        String horaManana2 = ResourceBundle.getBundle("config").getString("HORA_MANANA2");
        StringBuilder xmlES;        
        String xml ="";
                   Query consulta = em.createQuery("SELECT e FROM EntradasySalidasCaja e WHERE CURRENT_DATE = CAST(e.fecha AS DATE) AND e.hora  BETWEEN ?1 AND ?2 ORDER BY e.hora desc");
                            try {
                                consulta.setParameter("1", new SimpleDateFormat("hh").parse(horaManana1));
                                consulta.setParameter("2", new SimpleDateFormat("hh").parse(horaManana2));
                            } catch (ParseException ex) {
                                Logger.getLogger(EJBEntradasSalidasDiarias.class.getName()).log(Level.SEVERE, null, ex);
                            }                   
                   List<EntradasySalidasCaja>lista = consulta.getResultList();                   
                        if(consulta.getResultList().size()>0){ 
                            xml = hacerCalculosDeEntradaSalida(lista);                                    
                                             xmlES = new StringBuilder(xml);
                                             xmlES.replace(0, 0, "<totales>\n<turno>mañana</turno>\n");
                                    xml = xmlES.toString();
                        }                        
       return xml;
    }
    private String obtenerEntradasYSalidasDeTarde() {
        String horaTarde1 = ResourceBundle.getBundle("config").getString("HORA_TARDE1");
        String horaTarde2 = ResourceBundle.getBundle("config").getString("HORA_TARDE2");
        StringBuilder xmlES;
        String xml ="";                
                Query consulta = em.createQuery("SELECT e FROM EntradasySalidasCaja e WHERE CURRENT_DATE = CAST(e.fecha AS DATE) AND e.hora BETWEEN ?1 AND ?2 ORDER BY e.hora desc");
                                try {
                                    consulta.setParameter("1", new SimpleDateFormat("hh").parse(horaTarde1));
                                    consulta.setParameter("2", new SimpleDateFormat("hh").parse(horaTarde2));
                                } catch (ParseException ex) {
                                    Logger.getLogger(EJBEntradasSalidasDiarias.class.getName()).log(Level.SEVERE, null, ex);
                                }                
                                List<EntradasySalidasCaja> lista = consulta.getResultList();                                    
                                if(consulta.getResultList().size()>0){                                        
                                         xml = hacerCalculosDeEntradaSalida(lista);
                                        xmlES = new StringBuilder(xml);
                                        xmlES.replace(0, 0, "<totales>\n<turno>tarde</turno>\n");
                                        xml = xmlES.toString();
                                }
        return xml;
    }
    @Override
    public long calculosPorNumerodeCupon(double anticipo,Notadepedido nota){
       long retorno;
        EntradasySalidasCaja entradaSalidaCaja = new EntradasySalidasCaja();
            SimpleDateFormat sdfHora = new SimpleDateFormat("hh:mm:ss");        
                EntradasySalidasCaja entradaSalida = new EntradasySalidasCaja();
                entradaSalida.setFecha(new Date());
                entradaSalida.setHora(sdfHora.getCalendar().getTime());        
                entradaSalida.setID_USUARIO(0);
                        if(nota.getEntregado()=='1'||nota.getCancelado()=='1'){
                            entradaSalidaCaja.setEntradaTarjeta(nota.getMontototalapagar());
                            entradaSalidaCaja.setDetalles("Anticipo por Tarjeta entregada o cancelada "+nota.getNumerodecupon()+" Nota Pedido "+nota.getId());
                        }else{
                            entradaSalidaCaja.setEntradaTarjeta(anticipo);
                            entradaSalidaCaja.setDetalles("Anticipo por Tarjeta "+nota.getNumerodecupon()+" Nota Pedido "+nota.getId()+" $"+anticipo);
                        }
                entradaSalidaCaja.setNumerocupon(nota.getNumerodecupon());
                entradaSalidaCaja.setEnefectivo('0');
                entradaSalidaCaja.setSalidas(0.0);
                entradaSalidaCaja.setEntradas(0.0);
                entradaSalidaCaja.setAnticipo(0.0);  
                entradaSalidaCaja.setIdTarjetaCreditoDebitoFk(em.find(TarjetasCreditoDebito.class, nota.getIdTarjetaFk().getIdtarjeta()));
                entradaSalidaCaja.setID_USUARIO(0);                
                entradaSalidaCaja.setVentasEfectivo(0.0);                
            em.persist(entradaSalidaCaja);
        retorno = entradaSalidaCaja.getId_EntradasySalidas();
        return retorno;
    }
    @Override
    public long calculosPorAnticipoNotaPedido(double anticipo,Notadepedido notadePedido) {
        long retorno;        
                            EntradasySalidasCaja entradasySalidasCaja = new EntradasySalidasCaja();
                            SimpleDateFormat sdfHora = new SimpleDateFormat("hh:mm:ss");                                        
                            entradasySalidasCaja.setFecha(new Date());
                            entradasySalidasCaja.setHora(sdfHora.getCalendar().getTime());        
                            entradasySalidasCaja.setID_USUARIO(0);
                            entradasySalidasCaja.setAnticipo(anticipo);
                            entradasySalidasCaja.setEnefectivo('1');
                            entradasySalidasCaja.setNumerocupon("0");
                            entradasySalidasCaja.setSalidas(0.0);
                            entradasySalidasCaja.setEntradaTarjeta(0.0);
                            entradasySalidasCaja.setEntradas(0.0);
                            entradasySalidasCaja.setDetalles("Entrada por Anticipo "+anticipo+" Nota Pedido "+notadePedido.getId());                            
                            entradasySalidasCaja.setSalidas(0.0);
                            entradasySalidasCaja.setVentasEfectivo(notadePedido.getEnefectivo());
                            entradasySalidasCaja.setIdTarjetaCreditoDebitoFk(em.find(TarjetasCreditoDebito.class, notadePedido.getIdTarjetaFk().getIdtarjeta()));
                                em.persist(entradasySalidasCaja);
                            retorno = entradasySalidasCaja.getId_EntradasySalidas();
        return retorno;
    }

    private String hacerCalculosDeEntradaSalida(List<EntradasySalidasCaja> lista) {
        double acumEntradasAnticipo = 0;
        double acumEntradasTarjeta = 0;
        double acumEntradasEfectivo = 0;
        double acumEntradaManual = 0;
        double acumTotalAnticipoManualTarjeta = 0;
        double acumSalidaManual = 0;
        double arqueoGral;
        double caja;
        StringBuilder xmlCalculosES = new StringBuilder(32);
        String xml = "";
        Date fecha = null;        
        for (EntradasySalidasCaja entradasySalidasCaja : lista) {            
                        acumEntradaManual +=entradasySalidasCaja.getEntradas();
                        acumEntradasAnticipo +=entradasySalidasCaja.getAnticipo();              
                        acumEntradasTarjeta+=entradasySalidasCaja.getEntradaTarjeta();
                        acumSalidaManual +=entradasySalidasCaja.getSalidas();
                        xml+=entradasySalidasCaja.toXML();
                        fecha = entradasySalidasCaja.getFecha();
        }
                      acumEntradasEfectivo+=acumEntradaManual+acumEntradasAnticipo;  
                      acumTotalAnticipoManualTarjeta+=acumEntradasEfectivo+acumEntradasTarjeta;
                      arqueoGral = acumTotalAnticipoManualTarjeta - acumSalidaManual;
                      caja = acumEntradasEfectivo-acumSalidaManual;
                      xmlCalculosES.append("<totalanticipo>").append(acumEntradasAnticipo).append("</totalanticipo>\n").append("<totaltarjetas>").append(acumEntradasTarjeta).append("</totaltarjetas>\n").
                              append("<totalenefectivo>").append(acumEntradasEfectivo).append("</totalenefectivo>\n").append("<totalentradamanual>").append(acumEntradaManual).append("</totalentradamanual>\n").
                              append("<totalanticipoymanual>").append(acumEntradasEfectivo).append("</totalanticipoymanual>\n").append("<totalsalidas>").append(acumSalidaManual).append("</totalsalidas>\n")
                              .append("<arqueo>").append(arqueoGral).append("</arqueo>\n")
                              .append("<caja>").append(caja).append("</caja>")
                              .append("<fecha>").append(new SimpleDateFormat("dd/MM/yyyy").format(fecha)).append("</fecha>\n")
                              .append("</totales>\n")
                              .append(xml);           
                return xmlCalculosES.toString();
    }
}