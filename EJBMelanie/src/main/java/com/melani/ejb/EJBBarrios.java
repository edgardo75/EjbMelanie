package com.melani.ejb;
import com.melani.entity.Barrios;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
@Stateless(name="ejb/EJBBarrios")
@WebService(serviceName="ServiceBarrios",name="BarriosWs")
public class EJBBarrios implements EJBBarriosRemote {   
   @PersistenceContext
   private EntityManager em;
   @Override
   @SuppressWarnings("FinallyDiscardsException")
    public long addBarrio(String descripcion,int idUsuario) {
        long retorno;
        String nombreDeBarrio = "";
      
       try {
           //convierto string a su correspondiente encoding para evitar errores de parseo
           nombreDeBarrio = new String(descripcion.getBytes("ISO-8859-1"), "UTF-8");
       } catch (UnsupportedEncodingException ex) {
           Logger.getLogger(EJBBarrios.class.getName()).log(Level.SEVERE, null, ex);
       }
      
            if(nombreDeBarrio.length()>0){
                      Query consulta =  em.createQuery("SELECT b FROM Barrios b WHERE LOWER(b.descripcion) LIKE LOWER(?1)",Barrios.class);
                            consulta.setParameter("1",nombreDeBarrio+"%".toLowerCase());
                                List<Barrios> listaDeBarrios = consulta.getResultList();
                                    //inserto en la base de datos el nombre de barrio
                                        if (listaDeBarrios.isEmpty()) {
                                            Barrios barrio = new Barrios();
                                            barrio.setDescripcion(descripcion.toUpperCase());
                                            em.persist(barrio);
                                            retorno = barrio.getId();
                                        }else{
                                            retorno = -4;
                                        }                    
            }else {
              retorno=-2;
            }
        return retorno;        
    }
   @Override
    public String searchAllBarrios() {        
        String xml = "<?xml version='1.0' encoding='ISO-8859-1'?>\n"
                + "<Lista>\n"; 
                    Query consulta = em.createNamedQuery("Barrios.findAll");
                         List<Barrios>lista = consulta.getResultList();
                            if(lista.isEmpty()) {
                                xml+="LA CONSULTA NO ARROJÓ RESULTADOS";
                                }else{
                                    StringBuilder xmlLoop = new StringBuilder(32);
                                    for (Barrios barrios : lista) {
                                        
                                        try {
                                            xmlLoop.append(barrios.toXML());
                                        } catch (UnsupportedEncodingException ex) {
                                            Logger.getLogger(EJBBarrios.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                        
                                }                                
                                    xml+=xmlLoop;
                                }                         
                xml+="</Lista>";          
                return xml;
    }
    @Override
    public int recordCountBarrios() {
        Query consulta = em.createQuery("SELECT b FROM Barrios b");
        return consulta.getResultList().size();
    } 
}
