/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.melani.ejb;
import com.melani.entity.Localidades;
import com.melani.entity.Provincias;
import java.io.UnsupportedEncodingException;
import java.util.List;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;

/**
 *
 * @author Edgardo
 */
@Stateless(name="ejb/EJBLocalidades")
@WebService(serviceName="ServicesLocalidades",name="LocalidadesWs")
@SOAPBinding(style=SOAPBinding.Style.RPC)
public class EJBLocalidades implements EJBLocalidadesRemote {
    private static final Logger logger = Logger.getLogger(EJBLocalidades.class);
     @PersistenceContext(unitName="EJBMelaniPU2")
     private EntityManager em;  
     /**
      * 
      * @param idProvincia representa un identificador de una provincia en la base de datos
      * @return devuleve un listado de las provincias argentinas en con estructura xml
      */
    @Override
    public String searchLocXProvincia(short idProvincia) {
        String xml = "<Lista>\n";
        Query jpql = null;
        try {
            
            jpql = em.createNamedQuery("Localidades.findByLatLongNotNull");
            jpql.setParameter("1",idProvincia);
            List<Localidades>localidad = jpql.getResultList();
            StringBuilder xmlLoop = new StringBuilder(10);
            for (Localidades localidades : localidad) {
                xmlLoop.append(localidades.toXML());
            }
            xml+=xmlLoop;
        } catch (Exception e) {            
            logger.error("error en metodo searchLocXProvincia "+e.getMessage());
        }finally{
           return xml+="</Lista>\n";
        }
    }
/**
 * 
 * @param descripcion el nombre de la localidad
 * @param idProvincia el id de la provincia
 * @param codigopostal numero postal de la localidad en la provincia
 * @return devuelve el id de la localidad almancenada con éxito en la base de datos, caso contrario numero negativo, se produjo un error al 
 * ejecutar el método, se validó mal la descripcion de la localidad, si es cero no paso nada.
 */
    @Override
    public long addLocalidadCompleto(String descripcion, short idProvincia, int codigopostal) {
        long retorno = 0;
        String internalDescripcion;
        String out = null;
        try {
            internalDescripcion =new String(descripcion.getBytes("ISO-8859-1"), "UTF-8");
            
            
            
            if(internalDescripcion.length()>0){
                    
                    internalDescripcion+="%";
                    Query consulta = em.createQuery("SELECT l FROM Localidades l WHERE l.descripcion LIKE "
                            + ":descripcion and l.codigopostal = :codigopostal and  l.provincias.idProvincia = :idProvincia");
                    consulta.setParameter("descripcion",internalDescripcion.toLowerCase());
                    consulta.setParameter("codigopostal", codigopostal);
                    consulta.setParameter("idProvincia", idProvincia);
                            List<Localidades> lista = consulta.getResultList();
                            if (lista.isEmpty()) {
                                Localidades depto = new Localidades();
                                depto.setDescripcion(internalDescripcion.toUpperCase());
                                depto.setProvincias(em.find(Provincias.class, idProvincia));
                                depto.setCodigopostal(codigopostal);
                                depto.setLatitud("0");
                                depto.setLongitud("0");
                                
                                em.persist(depto);
                                
                                retorno = depto.getIdLocalidad();
                            } else {
                                retorno = -6;
                            }
            }else {
                retorno = -7;
            }
            
        } catch (UnsupportedEncodingException e) {
            retorno =-1;
            logger.error("Error en metodo addLocalidades "+e.getMessage());
       }finally{
            
            return retorno;
        }
    }
    //--------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------
    /**
     * 
     * @param idProvincia representa el identificador de la provincia
     * @return devuelve un listado de las localidades de la provincia correspondiente
     */
    @Override
    public String searchAllLocalidadesByIdProvincia(Short idProvincia) {
         String resultado = "<Lista>\n";
         try {
             Query consulta = em.createNamedQuery("Localidades.findByLatLongNotNull");
                   consulta.setParameter("1", idProvincia);
             List<Localidades>lista = consulta.getResultList();
             if(lista.isEmpty()) {
                 resultado+="NO HAY LOCALIDADES CARGADAS en "+em.find(Provincias.class, idProvincia).getProvincia();
             } else{
                 StringBuilder xmlLooop = new StringBuilder(10);
                 for (Localidades localidades : lista) {
                     xmlLooop.append(localidades.toXML());
                 }
                 resultado+=xmlLooop;
             }
        } catch (Exception e) {
            logger.error("Error en metodo searchAllLocalidadesByIdProvincia "+e.getMessage());
            resultado+="<error>Se produjo un error</error>";
        }finally{
              resultado+="</Lista>\n";              
            return resultado;
        }
    }

    @Override
    public short addLatitudLongitud(long idProvincia, long idLocalidad, String latitud, String longitud) {
        Localidades localidad = em.find(Localidades.class, idLocalidad);
        
        localidad.setLatitud(latitud);
        localidad.setLongitud(longitud);
        em.flush();
        short retorno = Short.valueOf(String.valueOf(localidad.getIdLocalidad()));
        return retorno;
    }
    
}