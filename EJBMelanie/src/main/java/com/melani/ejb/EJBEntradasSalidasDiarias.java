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
        xmlEntradasySalidas = lista.stream().map((next) -> next.toXML()).reduce(xmlEntradasySalidas, String::concat);
        
        return xmlEntradasySalidas+="</Lista>\n";
    }

    @Override
    public byte insertarEntradaSalidaManual(String monto, byte valorEntradaSalidaBit) {
        byte retorno; 
        EntradasySalidasCaja entradaSalida = procesarEntradaSalida();
        entradaSalida.setNumerocupon("0");        
        
            switch(valorEntradaSalidaBit){
                case 0:{
                    entradaSalida.setEntradas(0.00);
                    entradaSalida.setSalidas(Double.valueOf(monto));
                    entradaSalida.setDetalles("Salida Manual");
                    break;
                }default:{
                    entradaSalida.setEntradas(Double.valueOf(monto));
                    entradaSalida.setSalidas(0.00);
                    entradaSalida.setDetalles("Entrada Manual");
                }
            }
            
        
        entradaSalida.setEnefectivo('1');
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
        
        
                   Query consulta = em.createQuery("SELECT e FROM EntradasySalidasCaja e WHERE CURRENT_DATE = e.fecha AND e.hora  BETWEEN ?1 AND ?2 ORDER BY e.hora desc");
                            try {
                                consulta.setParameter("1", new SimpleDateFormat("hh").parse(horaManana1));
                                consulta.setParameter("2", new SimpleDateFormat("hh").parse(horaManana2));
                            } catch (ParseException ex) {
                                Logger.getLogger(EJBEntradasSalidasDiarias.class.getName()).log(Level.SEVERE, null, ex);
                            }
                   
                   List<EntradasySalidasCaja>lista = consulta.getResultList();
                   
                        if(consulta.getResultList().size()>0){
                                    //xml = lista.stream().map((next) -> next.toXML()).reduce(xml, String::concat);

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
                
                Query consulta = em.createQuery("SELECT e FROM EntradasySalidasCaja e WHERE CURRENT_DATE = e.fecha AND e.hora BETWEEN ?1 AND ?2 ORDER BY e.hora desc");
                                try {
                                    consulta.setParameter("1", new SimpleDateFormat("hh").parse(horaTarde1));
                                    consulta.setParameter("2", new SimpleDateFormat("hh").parse(horaTarde2));
                                } catch (ParseException ex) {
                                    Logger.getLogger(EJBEntradasSalidasDiarias.class.getName()).log(Level.SEVERE, null, ex);
                                }
                
                                List<EntradasySalidasCaja> lista = consulta.getResultList();
                                    
                                if(consulta.getResultList().size()>0){
                                        //xml = lista.stream().map((next) -> next.toXML()).reduce(xml, String::concat);
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
        EntradasySalidasCaja entradaSalidaCaja = procesarEntradaSalida();
            if(nota.getEntregado()=='1'||nota.getCancelado()=='1'){
                entradaSalidaCaja.setEntradaTarjeta(nota.getMontototalapagar().doubleValue());
                entradaSalidaCaja.setDetalles("Entrada por Tarjeta entregada o cancelada "+nota.getNumerodecupon()+" Nota Pedido "+nota.getId());
            }else{
                entradaSalidaCaja.setEntradaTarjeta(anticipo);
                entradaSalidaCaja.setDetalles("Entrada por Tarjeta "+nota.getNumerodecupon()+" Nota Pedido "+nota.getId()+" $"+anticipo);
            }
            entradaSalidaCaja.setNumerocupon(nota.getNumerodecupon());
            entradaSalidaCaja.setEnefectivo('0');
            entradaSalidaCaja.setSalidas(0.0);
            entradaSalidaCaja.setEntradas(0.0);
            entradaSalidaCaja.setAnticipo(0.0);            
            em.persist(entradaSalidaCaja);
        retorno = entradaSalidaCaja.getId();
        return retorno;
    }

    private EntradasySalidasCaja procesarEntradaSalida() {
        SimpleDateFormat sdfHora = new SimpleDateFormat("hh:mm:ss");        
        EntradasySalidasCaja entradaSalida = new EntradasySalidasCaja();
        entradaSalida.setFecha(new Date());
        entradaSalida.setHora(sdfHora.getCalendar().getTime());
        entradaSalida.setIdTarjetaFk(em.find(TarjetasCreditoDebito.class, 0));
        entradaSalida.setIdUsuario(0);
        return entradaSalida;
    }

    @Override
    public long calculosPorAnticipoNotaPedido(double anticipo,Notadepedido notadePedido) {
        long retorno;
        System.out.println("esto calculando el anticipo de la nota como entrada");
                            EntradasySalidasCaja entradasySalidasCaja = procesarEntradaSalida();
                            entradasySalidasCaja.setAnticipo(anticipo);
                            entradasySalidasCaja.setEnefectivo('1');
                            entradasySalidasCaja.setNumerocupon("0");
                            entradasySalidasCaja.setSalidas(0.0);
                            entradasySalidasCaja.setEntradaTarjeta(0.0);
                            entradasySalidasCaja.setEntradas(0.0);
                            entradasySalidasCaja.setDetalles("Entrada por Anticipo "+anticipo+" Nota Pedido "+notadePedido.getId());
                                em.persist(entradasySalidasCaja);
                            retorno = entradasySalidasCaja.getId();
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
        StringBuilder xmlCalculosES = new StringBuilder(10);
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

//    @Override
//    public long calculosVentasEfectivo(double totalAnticipo, double numeroNotaPEdido) {
//        long retorno;
//        EntradasySalidasCaja entradasySalidas = procesarEntradaSalida();
//                entradasySalidas.setAnticipo(totalAnticipo);
//                entradasySalidas.setVentasEfectivo(0);
//                    entradasySalidas.setEnefectivo('1');
//                    entradasySalidas.setNumerocupon("0");
//                    entradasySalidas.setSalidas(0.0);
//                    entradasySalidas.setEntradaTarjeta(0.0);
//                    entradasySalidas.setDetalles("Entradas por Ventas en Efectivo anticipo"+totalAnticipo+" N° nota "+numeroNotaPEdido);
//            em.persist(entradasySalidas);
//        retorno = entradasySalidas.getId();
//        
//        return retorno;
//    }
    
    
    

}
