package com.melani.ejb;
import com.melani.entity.Clientes;
import com.melani.entity.Domicilios;
import com.melani.entity.PersonasDomicilios;
import com.melani.entity.PersonasdomiciliosPK;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
@Stateless(name="ejb/EJBClienteDomicilio")
public class EJBClienteDomicilio implements EJBClienteDomicilioRemote {    
    @PersistenceContext
    private EntityManager em;
    @EJB
    EJBHistoricoPersonaDomicilioRemote ejbhistperdom;
    @Override
    public String addRelacionClienteDomicilio(long idCliente, long idDomicilio,int idUsuario) {
        String retorno = "NADA";
        
            if((idDomicilio>0) && (idCliente>0)&&idUsuario>=0){
                    GregorianCalendar calendario = new GregorianCalendar(Locale.getDefault());
                    PersonasdomiciliosPK perpk = new PersonasdomiciliosPK(idDomicilio, idCliente);
                        renovarDomicilio(idCliente,idUsuario);                    
                        PersonasDomicilios personadomicilio = new PersonasDomicilios();
                        personadomicilio.setDomicilioss(em.find(Domicilios.class, idDomicilio));
                        personadomicilio.setEstado("Habitable".toUpperCase());
                        personadomicilio.setPersonasdomiciliosPK(perpk);
                        personadomicilio.setFechaingresovivienda(calendario.getTime());
                        personadomicilio.setPersonas(em.find(Clientes.class, idCliente));
                        em.persist(personadomicilio);                        
                        retorno ="InyectóRelacion";
            }
        return retorno;        
    }
  
private void renovarDomicilio(long idCliente,int idUsuario) {
                    Query consulta = em.createQuery("SELECT p FROM PersonasDomicilios p WHERE p.personasdomiciliosPK.idPersona = :idPersona");
                    consulta.setParameter("idPersona", idCliente);
                    List<PersonasDomicilios>lista= consulta.getResultList();                    
                    
                                if(!lista.isEmpty()){
                                    for (PersonasDomicilios personasDomicilios : lista) {
                                        ejbhistperdom.addHistoricoPersonaDomicilio(personasDomicilios.getDomicilioss().getId().intValue(), (int) personasDomicilios.getPersonas().getIdPersona(), idUsuario);                                                                                
                                        Query eda = em.createQuery("DELETE FROM PersonasDomicilios p WHERE p.personasdomiciliosPK.idPersona = :idPersona AND p.personasdomiciliosPK.iddomicilio = :idDomicilio");
                                        eda.setParameter("idPersona", personasDomicilios.getPersonas().getIdPersona());
                                        eda.setParameter("idDomicilio", personasDomicilios.getDomicilioss().getId());
                                        eda.executeUpdate();
                                    
                                    }                                    
                                    
                                }
                        em.flush();
            
    }
}