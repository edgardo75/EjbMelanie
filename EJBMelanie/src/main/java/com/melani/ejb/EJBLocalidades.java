package com.melani.ejb;
import com.melani.entity.Localidades;
import com.melani.entity.Provincias;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
@Stateless(name="ejb/EJBLocalidades")
@WebService(serviceName="ServicesLocalidades",name="LocalidadesWs")
public class EJBLocalidades implements EJBLocalidadesRemote {    
     @PersistenceContext()
     private EntityManager em; 
    @Override
    public String searchLocXProvincia(short idProvincia) {
        String xml = "<Lista>\n";
        Query jpql;
            jpql = em.createNamedQuery("Localidades.findByLatLongNotNull");
            jpql.setParameter("1",idProvincia);
            List<Localidades>localidad = jpql.getResultList();
            StringBuilder xmlLoop = new StringBuilder(32);
            for (Localidades localidades : localidad) {
               xmlLoop.append(localidades.toXML());
            }           
            xml+=xmlLoop;        
           return xml+="</Lista>\n";        
    }
    @Override
    public long addLocalidadCompleto(String descripcion, short idProvincia, int codigopostal) {
        long retorno;
        String internalDescripcion = null;                
        try {
            internalDescripcion =new String(descripcion.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            java.util.logging.Logger.getLogger(EJBLocalidades.class.getName()).log(Level.SEVERE, null, ex);
        }
            if(internalDescripcion!=null){
                Query consulta = em.createQuery("SELECT l FROM Localidades l WHERE l.descripcion LIKE :descripcion AND l.codigopostal = :codigoPostal AND l.provincias.idProvincia = :idProvincia");
                    consulta.setParameter("descripcion",descripcion+"%");
                    consulta.setParameter("codigoPostal", codigopostal);
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
            return retorno;
        
    }
    @Override
    public String searchAllLocalidadesByIdProvincia(Short idProvincia) {
         String resultado = "<Lista>\n";        
             Query consulta = em.createNamedQuery("Localidades.findByLatLongNotNull");
                   consulta.setParameter("1", idProvincia);
             List<Localidades>lista = consulta.getResultList();
                        if(lista.isEmpty()) {
                            resultado+="NO HAY LOCALIDADES CARGADAS en "+em.find(Provincias.class, idProvincia).getProvincia();
                        } else{
                            StringBuilder xmlLooop = new StringBuilder(32);
                            for (Localidades localidades : lista) {
                                xmlLooop.append(localidades.toXML());
                            }                            
                            resultado+=xmlLooop;
                        }        
              resultado+="</Lista>\n";              
            return resultado;        
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