package com.melani.ejb;
import com.melani.entity.Clientes;
import com.melani.entity.Domicilios;
import com.melani.entity.Generos;
import com.melani.entity.HistoricoDatosClientes;
import com.melani.entity.Personas;
import com.melani.entity.PersonasDomicilios;
import com.melani.entity.Personastelefonos;
import com.melani.entity.Telefonos;
import com.melani.entity.TelefonosPK;
import com.melani.entity.Tiposdocumento;
import com.melani.utils.ClienteDomicilioTelefono;
import com.melani.utils.DatosCliente;
import com.melani.utils.DatosDomicilios;
import com.melani.utils.DatosTelefonos;
import com.melani.utils.ListaTelefonos;
import com.melani.utils.ProjectHelpers;
import com.melani.utils.ValidateClientandUserData;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
@Stateless(name="ejb/EJBClientes")
@WebService(serviceName="ServiceClientes",name="ClientesWs")
public class EJBClientes implements EJBClientesRemote {    
    @PersistenceContext()   
    private EntityManager em;              
    @EJB
    EJBDomiciliosRemote ejbdomici;
    @EJB
    EJBTelefonosRemote ejbtele;
    @EJB
    EJBClienteDomicilioRemote ejbclidom;
    @EJB
    EJBClienteTelefonoRemote ejbclitel;
    private volatile long chequear__email_numDoc=0L;
    ValidateClientandUserData validateDataUser;
    String nombreyApellidoPattern;
    String numberPattern_Dni;
    String email_Pattern;
    String telefonoPattern;
    String prefijoPattern;
    public EJBClientes() {
        this.nombreyApellidoPattern = "(?=^.{1,30}$)[[A-Z][a-z]\\p{IsLatin}]* ?[[a-zA-Z]\\p{IsLatin}]* ?[[a-zA-Z]\\p{IsLatin}]+$";
        this.numberPattern_Dni="(?=^.{1,10}$)\\d+$";
        this.email_Pattern="^[\\w\\-\\+\\*]+[\\w\\S]@(\\w+\\.)+[\\w]{2,4}$";
        this.telefonoPattern ="^(4|15)(\\d){6,}+$";
        this.prefijoPattern ="^(\\d){1,8}+$";     
        validateDataUser = new ValidateClientandUserData();
    }    
        public String addCliente(String xmlClienteDomicilioTelefono) {
        long retorno=0; 
        Personas persona = null; 
        StringBuilder xmlCliente = new StringBuilder(32);
            ClienteDomicilioTelefono getAllDatos = parsear_a_objetos(xmlClienteDomicilioTelefono);
             DatosCliente datosClientePersonales = getAllDatos.getCliente();
            if(validateDataUser.validate(datosClientePersonales.getNombre(), nombreyApellidoPattern)){             
                 if(validateDataUser.validate(datosClientePersonales.getApellido(),nombreyApellidoPattern)){             
                    String numeroDocu = String.valueOf(datosClientePersonales.getNrodocu());             
                     if(validateDataUser.validate(numeroDocu,numberPattern_Dni)){                     
                         long idPersona = existePersona(datosClientePersonales.getNrodocu());                         
                                if(idPersona>0) {
                                    persona = em.find(Personas.class, idPersona);
                                }             
                                    if(!datosClientePersonales.getEmail().isEmpty()){
                                           chequear__email_numDoc = chequearEmail(datosClientePersonales.getEmail(),datosClientePersonales.getNrodocu());
                                           
                                           switch((int)chequear__email_numDoc){              
                                               
                                               case -7:{Logger.getLogger("Error en metodo chequear email");
                                               retorno = chequear__email_numDoc;
                                               break;
                                               }
                                               case -8:{Logger.getLogger("Email encontrado en metodo chequearEmail");
                                               retorno = chequear__email_numDoc;
                                                break;
                                               }
                                               case -11:{Logger.getLogger("Email no válido");
                                               retorno = chequear__email_numDoc;
                                                break;
                                               }
                                               default:{                                                   
                                                     if(persona!=null){
                                                            retorno = buscarPersonaSiEsCliente(persona, datosClientePersonales, 
                                                            getAllDatos, xmlClienteDomicilioTelefono);
                                                       }else{
                                                            retorno = agregarTodosLosDatosCliente(getAllDatos,datosClientePersonales,
                                                            xmlClienteDomicilioTelefono);
                                                       }                                              
                                                      break;
                                                  }
                                           }
                                    }else{
                                       if(persona!=null) {
                                           retorno = buscarPersonaSiEsCliente(persona, datosClientePersonales, 
                                                   getAllDatos, xmlClienteDomicilioTelefono);
                                       } else {
                                           retorno = agregarTodosLosDatosCliente(getAllDatos,
                                                   datosClientePersonales,xmlClienteDomicilioTelefono);
                                       }
                                    }
                     }else{
                          retorno = -14;
                        Logger.getLogger("Numero de documento no válido");
                     }
                 }else{
                     retorno = -13;
                    Logger.getLogger("Apellido no válido");
                 }
             }else{
                 retorno = -12;
                 Logger.getLogger("Nombre no válido");
             } 
           
             if(retorno>0){
                 xmlCliente.append("<Lista>\n");
                       Clientes cliente = em.find(Clientes.class, retorno);
                       xmlCliente.append("<item>\n").append("<id>").append(cliente.getIdPersona())
                               .append("</id>").append("<nrodocu>").append(cliente.getNrodocumento()).append("</nrodocu>")
                               .append("<nombre>").append(cliente.getNombre()).append("</nombre>").append("<apellido>")
                               .append(cliente.getApellido()).append("</apellido>").append("</item>\n").append("</Lista>\n");
                       return xmlCliente.toString();
             }else{
                 return String.valueOf(retorno);        
             }
                
    }       
    private long buscarPersonaSiEsCliente(Personas persona,DatosCliente datosClientePersonales,
            ClienteDomicilioTelefono todosDatos,String xmlClienteDomicilioTelefono){
        long retorno;
                                
           if(persona.getPertype().equals("CLI")) {
               retorno=actualizarDatos(todosDatos,datosClientePersonales,xmlClienteDomicilioTelefono,persona.getIdPersona());
           } else {
               retorno=-9;
           }       
            return retorno;         
    }  
    private long existePersona(int nrodocu) {
        long retorno =0;
        
            Query consulta = em.createNamedQuery("Personas.findByNrodocumento");
                consulta.setParameter("nrodocumento", nrodocu);
                        List<Personas>lista = consulta.getResultList();
                        if(lista.size()==1)      {
                                for (Personas personas : lista) {
                                    retorno=personas.getIdPersona();
                                }
                        }                      
                return retorno;        
    }
   
    private long agregarTodosLosDatosCliente(ClienteDomicilioTelefono todosDatos, 
            DatosCliente datosClientePersonales,String xmlClienteDomicilioTelefono) {
        long retorno;      
            GregorianCalendar calendario = new GregorianCalendar(Locale.getDefault());
                       Clientes cliente = new Clientes();                
                                cliente.setApellido(datosClientePersonales.getApellido().toUpperCase()); 
                                if(datosClientePersonales.getEmail().length()>0){
                                    cliente.setEmail(datosClientePersonales.getEmail());                
                                }else{
                                    cliente.setEmail("");                
                                }
                                cliente.setFechaCarga(calendario.getTime());                
                                cliente.setGeneros(em.find(Generos.class, datosClientePersonales.getGeneros().getIdgenero()));                
                                cliente.setNombre(datosClientePersonales.getNombre().toUpperCase());                  
                                cliente.setNrodocumento(datosClientePersonales.getNrodocu());     
                                if(datosClientePersonales.getObservaciones().length()>0){
                                    cliente.setObservaciones(datosClientePersonales.getObservaciones().toUpperCase());                
                                }else{
                                        cliente.setObservaciones("");                
                                }
                                cliente.setTipodocumento(em.find(Tiposdocumento.class, datosClientePersonales.getIdtipodocu()));                
                                cliente.setTotalCompras(datosClientePersonales.getTotalcompras());                
                                cliente.setTotalEnPuntos(datosClientePersonales.getTotalpuntos());                
                                em.persist(cliente);                                
                                retorno = guardarDomicilioyTelefonoCliente(xmlClienteDomicilioTelefono, cliente, todosDatos);                 
            return retorno;        
    }
    
    @Override
    public String obtenerCliente(long idCliente) {
        String cli = "<Lista>\n";        
            Clientes cliente = em.find(Clientes.class, idCliente);
            if(cliente!=null){
                cli+="<item>\n";
                cli+=cliente.toXML();
                cli+=cliente.toXMLCLI();
                cli+="</item>\n";
            }else {
                cli+="<cliente>NO ENCONTRADO</cliente>";
            }                  
            return cli+="</Lista>\n";        
    }
   
private long actualizarDatos(ClienteDomicilioTelefono todosDatos, 
        DatosCliente datosClientePersonales,String xmlClienteDomicilioTelefono,long idcliente) {
        long retorno = 0;        
           Clientes cliente = em.find(Clientes.class, idcliente);
            if(cliente!=null){           
                                HistoricoDatosClientes historicoClient = new HistoricoDatosClientes();
                                        historicoClient.setApellido(cliente.getApellido().toUpperCase());                
                                        historicoClient.setIdCliente(idcliente);
                                        historicoClient.setIdgenero(cliente.getGeneros().getIdGenero());
                                        historicoClient.setNombre(cliente.getNombre().toUpperCase());    
                                        historicoClient.setObservaciones(cliente.getObservaciones());                                        
                                if(datosClientePersonales.getObservaciones().length()>0){        
                                    cliente.setObservaciones(datosClientePersonales.getObservaciones());            
                                }else{
                                    cliente.setObservaciones("");            
                                }
                                cliente.setApellido(datosClientePersonales.getApellido().toUpperCase());            
                                cliente.setNombre(datosClientePersonales.getNombre().toUpperCase());      
                                cliente.setGeneros(em.find(Generos.class, datosClientePersonales.getGeneros().getIdgenero()));
                                 if(chequear__email_numDoc != -5){
                                    
                                            historicoClient.setEmail(cliente.getEmail());
                                            cliente.setEmail(datosClientePersonales.getEmail());
                                 }                                        
                                 double acumTotalCompras = 0;
                              if(cliente.getTotalCompras()!=0 &&datosClientePersonales.getTotalcompras()!=0){   
                                    double totalCompras = Double.valueOf(String.valueOf(cliente.getTotalCompras()));
                                    acumTotalCompras = totalCompras+Double.parseDouble(String.valueOf(datosClientePersonales.getTotalcompras()));
                                    cliente.setTotalCompras(acumTotalCompras); 
                              }                                  
                              int acumTotalPuntos = 0;
                              if(cliente.getTotalEnPuntos()!=0&&datosClientePersonales.getTotalpuntos()!=0){
                                int totalPuntos = cliente.getTotalEnPuntos();
                                acumTotalPuntos = totalPuntos+datosClientePersonales.getTotalpuntos();
                                cliente.setTotalEnPuntos(acumTotalPuntos);
                              }                                
                                cliente.setFechaCarga(new GregorianCalendar().getTime());
                            historicoClient.setTotalCompras(acumTotalCompras);
                            historicoClient.setTotalEnPuntos(acumTotalPuntos);
                            retorno = guardarDomicilioyTelefonoCliente(xmlClienteDomicilioTelefono,cliente,todosDatos); 
                                em.merge(cliente);
                                em.persist(historicoClient);        
            }                   
            return retorno;      
    }

    @Override
    public String obtenerClienteXTipoAndNumeroDocu(short idTipo, int nrodDocu) {
        String result = "<Lista>\n";           
            long idPersona = existePersona(nrodDocu);    
                    switch((int)idPersona){
                        case 0:{
                             result+="<result>NO PASO NADA</result>\n";                        
                             break;
                        }
                        case -1:{
                            result+="<result>ERROR EN METODO EXISTE Persona</result>\n";                        
                             break;
                        }
                        default:{                            
                            Clientes cliente = em.find(Clientes.class, idPersona);                  
                                    if(cliente != null){
                                        result+="<item>\n";
                                        result+=cliente.toXML();
                                        result+="</item>\n";
                                    }else {
                                        result+="<cliente>NO ES UN CLIENTE</cliente>\n";
                                    }                                         
                        }
            }           
            return result+="</Lista>\n";        
    }
private long guardarDomicilioyTelefonoCliente(String xmlClienteDomicilioTelefono,Clientes cliente, ClienteDomicilioTelefono todosDatos) {
        long retorno;
        long idDomicilio; 
        String result="";          
           if(xmlClienteDomicilioTelefono.contains("<Domicilio>")){               
               if(todosDatos.getDomicilio().getDomicilioId()>0){
                   idDomicilio=todosDatos.getDomicilio().getDomicilioId();
                   result= encontrarRelacion(idDomicilio, cliente, todosDatos.getIdusuario());                   
               }else{                    
                        idDomicilio = ejbdomici.addDomicilios(todosDatos.getDomicilio());                                                                           
                               switch((int)idDomicilio){
                                   case -1:{Logger.getLogger("Error No se pudo agregar domicilio Verifique!!!");
                                   retorno = -1;
                                   break;}
                                   case -2:{Logger.getLogger("Error No se pudo agregar domicilio Verifique!!!");
                                   retorno = -2;
                                   break;}
                                   case 0:{Logger.getLogger("Error no se pudo agregar domicilio Verifique!!!");
                                   retorno = 0;
                                   break;}
                                   case -3:{Logger.getLogger("Error en metodo actualizar domicilio Verifique!!!");
                                   retorno = -3;
                                   break;}
                                   default:{
                                       result = encontrarRelacion(idDomicilio,cliente,todosDatos.getIdusuario());
                                       
                                   }
                               }
               }//end else
                                if(result.contains("InyectóRelacion")){
                                    List<PersonasDomicilios>listaPD = em.createQuery("SELECT p FROM PersonasDomicilios p "
                                            + "WHERE p.personasdomiciliosPK.idPersona = :idPersona").setParameter("idPersona",
                                                    cliente.getIdPersona()).getResultList(); 
                                    cliente.setPersonasDomicilioss(listaPD);
                                    Domicilios domici = em.find(Domicilios.class, idDomicilio);
                                    domici.setPersonaDomicilio(listaPD);
                                    em.persist(domici);
                                    em.persist(cliente);
                                } 
           }            
           
                    if(xmlClienteDomicilioTelefono.contains("<telefono>")){  
                        
                           if(todosDatos.getListaTelefonos().getList().size()>0){            
                               Iterator iter = todosDatos.getListaTelefonos().getList().iterator();
                               String resultTC="";
                               DatosTelefonos datosTel;
                               while (iter.hasNext())
                               {    
                                    datosTel = (DatosTelefonos) iter.next();
                                    if(validateDataUser.validate(datosTel.getNumero(),telefonoPattern)&&validateDataUser.validate(datosTel.getPrefijo(),prefijoPattern)){
                                                long rettelefono = ejbtele.addTelefonos(datosTel);
                                                if(rettelefono==2){
                                                    resultTC = ejbclitel.addClienteTelefono(datosTel.getNumero(), 
                                                            datosTel.getPrefijo(), cliente.getIdPersona());
                                                }else{
                                                    if(rettelefono==1){                                                  
                                                         resultTC = ejbclitel.addClienteTelefono(datosTel.getNumero(), 
                                                                 datosTel.getPrefijo(), cliente.getIdPersona());                                                    
                                                    }
                                                }
                                    }else{
                                        Logger.getLogger("NUMERO DE TELEFONO O PREFIJO NO VÁLIDO NO ALMACENADO ");
                                        return retorno =-15;
                                    }
                               }
                                       Query clitele = em.createNamedQuery("Personastelefonos.findByIdPersona");
                                       clitele.setParameter("idPersona", cliente.getIdPersona());
                                       List<Personastelefonos>listaTel = clitele.getResultList();
                                       cliente.setPersonastelefonoss(listaTel);
                                        Telefonos telef=null;
                               for (Personastelefonos personastelefonos : listaTel) {
                                   telef = em.find(Telefonos.class, new TelefonosPK(personastelefonos.getPersonastelefonosPK().getNumerotel(), 
                                           personastelefonos.getPersonastelefonosPK().getPrefijo()));
                                   telef.setPersonastelefonosCollection(listaTel);
                               }
                                           em.persist(telef);
                                           em.persist(cliente);                              
                      }                         
                    }
                    
                retorno = cliente.getIdPersona();        
           return retorno;       
    }
    @Override
    public String getCustomerDocNumber(Integer docNumber) {
        String xml = null;
        
            Query jsql=em.createNamedQuery("Personas.searchByNroDocuAndPertype");
            jsql.setParameter("nrodocumento", docNumber);
            jsql.setParameter("pertype", "CLI");
            List<Clientes>lista = jsql.getResultList();
           switch(lista.size()){
               case 0:xml+="Cliente no encontrado";
               break;
               case 1:{
                   StringBuilder xmlLoop = new StringBuilder(32);
                   for (Clientes clientes : lista) {
                       xmlLoop.append("<item>\n").append("<id>").append(clientes.getIdPersona()).append("</id>\n" + "<apellido>")
                               .append(clientes.getApellido()).append("</apellido>\n").append("<nombre>")
                               .append(clientes.getNombre()).append("</nombre>\n").append("<idtipodocu>")
                               .append(clientes.getTipodocumento().getId()).append("</idtipodocu>\n").append("<nrodocu>")
                               .append(clientes.getNrodocumento()).append("</nrodocu>\n");
                       xmlLoop.append("</item>\n");                       
                   }
                   
                xml+=xmlLoop;
               }
           }               
            return xml;        
    }   
    public long chequearEmail(String email,Integer nrodocu) {
        long retorno = -6;       
            if(!email.isEmpty()&&nrodocu>0){
                if(validateDataUser.validate(email,email_Pattern)){
                    Query sqlemail = em.createNamedQuery("Personas.searchByEmailAndNroDocu");
                                sqlemail.setParameter("email", email.toLowerCase());
                                sqlemail.setParameter("nrodocumento", nrodocu);
                        if(sqlemail.getResultList().size()==1) {
                            retorno = -5;
                    } else{
                            sqlemail = em.createNamedQuery("Personas.findByEmail");
                                sqlemail.setParameter("email", email.toLowerCase());
                                if(sqlemail.getResultList().size()==1) {
                                    retorno =-8;
                        }
                        }
                }else{
                    Logger.getLogger("Email no válido");
                    retorno =-11;
                }
            }                
            return retorno;        
    }
    @Override
    public String searchClientForNameAndLastName(String name,String lastname) {
        String xml = "<Lista>\n";        
            String sbname = name+"%";
            String sblastname = lastname+"%";
                    String sql = "SELECT p FROM Personas p WHERE p.nombre LIKE :nombre and p.apellido LIKE :apellido";
                            Query consulta = em.createQuery(sql);
                            consulta.setParameter("nombre", sbname.toUpperCase());
                            consulta.setParameter("apellido", sblastname.toUpperCase());
                            List<Personas>lista = consulta.getResultList();
                                StringBuilder xmlLoop = new StringBuilder(32);
                                for (Personas personas : lista) {
                                    xmlLoop.append("<item>\n").append("<id>")
                                            .append(personas.getIdPersona())
                                            .append("</id>\n").append("<apellido>")
                                            .append(personas.getApellido()).append("</apellido>\n").append("<nombre>")
                                            .append(personas.getNombre()).append("</nombre>\n").append("<idtipodocu>")
                                            .append(personas.getTipodocumento().getId()).append("</idtipodocu>\n").append("<nrodocu>")
                                            .append(personas.getNrodocumento()).append("</nrodocu>\n").append("</item>\n");
                                }
                                xml+=xmlLoop;        
            return xml+"</Lista>\n";        
    }    
    private ClienteDomicilioTelefono parsear_a_objetos(String xmlClienteDomicilioTelefono){
        ClienteDomicilioTelefono datoscliente;
        ProjectHelpers xmlParser = new ProjectHelpers();
             XStream  xstream = new XStream(new StaxDriver());
             
               xstream.alias("ClienteDomicilioTelefono",ClienteDomicilioTelefono.class);
                xstream.alias("item", DatosCliente.class);                      
                    if(xmlClienteDomicilioTelefono.contains("<Domicilio>")) {
                        xstream.alias("Domicilio", DatosDomicilios.class);
                    }
                    if(xmlClienteDomicilioTelefono.contains("<telefono>")){                   
                        xstream.alias("listaTelefonos", ListaTelefonos.class);
                        xstream.alias("telefono", DatosTelefonos.class);
                        xstream.addImplicitCollection(ListaTelefonos.class,"list");
                    }        
                    datoscliente = (ClienteDomicilioTelefono) 
                            xstream.fromXML(xmlParser.parsearCaracteresEspecialesXML1(xmlClienteDomicilioTelefono));                                          
            return datoscliente;        
    }   

    private String encontrarRelacion(long idDomicilio, Clientes cliente,int idUsuario) {
        String result = "";
          String consulta="SELECT p FROM PersonasDomicilios p "
                                               + "WHERE p.personasdomiciliosPK.idPersona = :idPersona and "
                                               + "p.personasdomiciliosPK.iddomicilio = :iddomicilio";
                                       Query sqlPD = em.createQuery(consulta);
                                       sqlPD.setParameter("idPersona", cliente.getIdPersona());
                                       sqlPD.setParameter("iddomicilio", idDomicilio);            
                                       switch(sqlPD.getResultList().size()){
                                           case 0:{
                                            result= ejbclidom.addRelacionClienteDomicilio(cliente.getIdPersona(), idDomicilio,idUsuario);
                                            break;
                                           }                                           
                                           default:{                                              
                                                       //"Relacion Encontrada PD";
                                                   ejbclidom.renovarDomicilio(cliente.getIdPersona(), idUsuario);
                                                   result= ejbclidom.addRelacionClienteDomicilio(cliente.getIdPersona(), idDomicilio,idUsuario);
                                               break;
                                           }
                                       }
                                       return result;
    }

    
}