package com.melani.ejb;
import com.melani.entity.EmpleadoParttime;
import com.melani.entity.Empleados;
import com.melani.entity.FullTimeEmpleado;
import com.melani.entity.Generos;
import com.melani.entity.Notadepedido;
import com.melani.entity.Tiposdocumento;
import com.melani.utils.DatosEmpleado;
import com.melani.utils.ProjectHelpers;
import com.melani.utils.ValidateClientandUserData;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import java.text.DateFormat;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.registry.infomodel.EmailAddress;
import org.apache.commons.lang3.StringEscapeUtils;
@Stateless(name="ejb/EJBEmpleados")
@WebService(serviceName="ServicesEmpleados",name="EmpleadosWs")
public class EJBEmpleados implements EJBEmpleadosRemote {     
    @PersistenceContext()
    private EntityManager em; 
    String nombreyApellidoPattern;
    String numberPattern_Dni;
    //String email_Pattern;
    String telefonoPattern;
    String prefijoPattern;
    String nameUser_pattern;
    String password_pattern;
    ValidateClientandUserData validarDatosEmpleadoUser;
    public EJBEmpleados(){
        this.validarDatosEmpleadoUser = new ValidateClientandUserData();
        this.nombreyApellidoPattern = "(?=^.{1,30}$)[[A-Z][a-z]\\p{IsLatin}]* ?[[a-zA-Z]\\p{IsLatin}]* ?[[a-zA-Z]\\p{IsLatin}]+$";
        this.numberPattern_Dni="(?=^.{1,10}$)\\d+$";
      //  this.email_Pattern="^[\\w\\-\\+\\*]+[\\w\\S]@(\\w+\\.)+[\\w]{2,4}$";
        this.telefonoPattern ="^(4|15)(\\d){6,}+$";
        this.prefijoPattern ="^(\\d){1,8}+$";   
        this.nameUser_pattern="(?=^.{1,20}$)^([\\w\\.^\\-.][\\s]?)([\\w\\-\\s]*)([\\w]+$?)+$"; 
        this.password_pattern="^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?!.*\\s).*$";
                //"^(?=.*[A-Z])(?=.*\\d)(?!.*(.)\\1\\1)[a-zA-Z0-9@]{4,20}$";
    }
    
     @Override
    public long addEmpleadoFullTime(String xmlEmpleado){
        long retorno;
                    //llamo a metodo interno para convertir a objeto los datos del empleado
                    DatosEmpleado datosEmpleado = datosEmpleadosObject(xmlEmpleado);            
                      retorno = procesarDatosEmpleadoAdd(datosEmpleado);
                return retorno;
    }
    private long numeroaRetornar(long retorno) {
         switch((int)retorno){
            case 1:{retorno=-2;break;}//nro de documento encotrado
            case 2:{retorno=-4;break;}//email detectado
            case 3:{retorno=-6;break;}//nombre usuario detectado                                           
            case -3:{retorno=-5;break;}//error en el metodo
         }         
         return retorno;
    
    }
    public long addEmpleadoParttime(String xmlEmpleado){
        long retorno;                  
             DatosEmpleado datosEmpleado = datosEmpleadosObject(xmlEmpleado);
                retorno = procesarDatosEmpleadoAdd(datosEmpleado);           
           return retorno;        
    }

    @Override
    public String selectAllEmpleados() {
       String xml="<?xml version='1.0' encoding='utf-8'?>\n"+"<Lista>\n";        
       ProjectHelpers passTry = new ProjectHelpers();
            Query consulta = em.createQuery("SELECT e FROM Empleados e order by e.idPersona desc");            
            List<Empleados>lista = consulta.getResultList();
            if(lista.size()>0){
                    StringBuilder xmlLoop = new StringBuilder(32);                 
                    
                    for (Empleados empleados : lista) {
                      xmlLoop.append("<item>\n")
                        .append("<id>").append(empleados.getIdPersona()).append("</id>\n")
                        .append("<nombre>").append(empleados.getNombre()).append("</nombre>\n")
                        .append("<apellido>").append(empleados.getApellido()).append("</apellido>\n")
                        .append("<genero>").append(empleados.getGeneros().getIdGenero()).append("</genero>\n")
                        .append("<tipodocu>").append(empleados.getTipodocumento().getId()).append("</tipodocu>\n")
                        .append("<documento>").append(empleados.getNrodocumento()).append("</documento>\n")
                        .append("<emptype>").append(empleados.getEmptype()).append("</emptype>\n")                              
                        .append("<observaciones>").append(empleados.getObservaciones()).append("</observaciones>\n")
                        .append("<email>").append(empleados.getEmail()).append("</email>\n")
                        .append("<nameuser>").append(empleados.getNameuser()).append("</nameuser>\n")                              
                        //.append("<clave>").append(StringEscapeUtils.escapeXml11(empleados.getPassword())).append("</clave>")
                        .append("<clave>").append(StringEscapeUtils.escapeXml11(passTry.decrypt(empleados.getKeyPassword(),empleados.getPassword()))).append("</clave>")
                        .append(obtenerEmpleado(empleados))
                        .append("</item>\n");
                }                   
                xml+=xmlLoop;
            }else {
                xml+="<result>Lista Vacia</result>\n";
            }        
            xml+="</Lista>\n";            
            return xml;        
    }
    protected String obtenerEmpleado(Empleados emp){
        String xml=null;
        if(emp.getEmptype().equals("FULLTIME")){
                        Query sqlFullTimeEmp = em.createQuery("Select f From FullTimeEmpleado f Where f.idPersona = :idpersona");
                        sqlFullTimeEmp.setParameter("idpersona", emp.getIdPersona());
                        List<FullTimeEmpleado>list=sqlFullTimeEmp.getResultList();
            for (FullTimeEmpleado fullTimeEmpleado : list) {
                xml=fullTimeEmpleado.toXML();
            }
        }else{
                        Query sqlParTTimeEmp = em.createQuery("Select e From EmpleadoParttime e Where e.idPersona = :idpersona");
                        sqlParTTimeEmp.setParameter("idpersona", emp.getIdPersona());
                        List<EmpleadoParttime>list=sqlParTTimeEmp.getResultList();
            for (EmpleadoParttime empleadoParttime : list) {
                xml =empleadoParttime.toXML();
            }

        }
        return xml;
    }
     @Override
    public int deshabilitarEmpleado(int idEmpleado, int idEmpleadoDesabilito) {
        long retorno;                    
            StringBuilder sb =new StringBuilder(32);
                Empleados empleadoDesabilitado = em.find(Empleados.class, idEmpleado);
                Empleados empleadoDesabilito = em.find(Empleados.class, idEmpleadoDesabilito);
                empleadoDesabilitado.setEstado((short)0);
                    sb.append(empleadoDesabilito.getNombre());
                    sb.append(" ");
                    sb.append(empleadoDesabilito.getApellido());                    
             retorno=empleadoDesabilitado.getIdPersona();        
            return (int) retorno;        
    }
    private long buscarEmpleadoEmailAndNameUser(int numerodocu,String email,String nameuser) {
        long retorno =0;       
            if(numerodocu>0){
                Query consulta = em.createQuery("SELECT p FROM Personas p WHERE p.nrodocumento = :nrodocumento");
                    consulta.setParameter("nrodocumento", numerodocu);
                    if(consulta.getResultList().size()==1){
                        retorno=1;
                        return retorno;
                    }
            }
            if(email.contains("@")){
                Query sqlemail = em.createQuery("SELECT p FROM Personas p WHERE p.email = :email");
                    sqlemail.setParameter("email", email.toLowerCase());
                    if(sqlemail.getResultList().size()==1) {
                        retorno=2;
                }
            }
            if(nameuser.length()>0){
                Query sqlusername=em.createQuery("SELECT e FROM Empleados e WHERE LOWER(e.nameuser) = LOWER(:nameuser)");
                    sqlusername.setParameter("nameuser", nameuser.toLowerCase());
                    if(sqlusername.getResultList().size()==1) {
                        retorno=3;
                }
            }       
           return retorno;        
    }
     @Override
    public int habilitarEmpleado(int idEmpleado, int idEmpleadohabilito) {
            int retorno;        
            GregorianCalendar gc = new GregorianCalendar();            
                Empleados empleadoHabilitado = em.find(Empleados.class, idEmpleado);
                Empleados empleadohabilito = em.find(Empleados.class, idEmpleadohabilito);
                empleadoHabilitado.setEstado((short)1);            
             retorno=Integer.valueOf(String.valueOf(empleadohabilito.getIdPersona()));
             Logger.getLogger(new StringBuilder("USUARIO HABILITÓ ").append(empleadoHabilitado.getNombre()).
                     append(" A USUARIO NUEVO ").append(empleadohabilito.getNombre()).append(" EL DÍA ").append(DateFormat.getInstance().format(gc.getTime())).toString());                
            return retorno;
        
    }
    public long actualizarEmpleado(String xmlEmpleado){   
        long retorno;         
            DatosEmpleado empleado = datosEmpleadosObject(xmlEmpleado);//convierto a objeto           
            retorno=validarDatosEmpleado(empleado);//retorno el resultado de validar datos nombre y apellido, password           
           if(retorno==0){      
                        retorno = valorRetornadoAlBuscarEmailyNombreUsuario(retorno, empleado.getNumeroDocumento(),
                                empleado.getEmail(),empleado.getNombreUsuario());                        
                            if(retorno>0){   
                                           retorno = processEmployee(empleado,xmlEmpleado);
                            }
           }
           return retorno;      
    }
    private void actualizarReferenciasConNotasdePedido(long nuevoEmployee, long idEmpleado) {       
            Query sql = em.createNamedQuery("Notadepedido.findAll", Notadepedido.class);
            List<Notadepedido>lista = sql.getResultList();
            for(Notadepedido n:lista){
                if(n.getIdUsuarioExpidioNota()==idEmpleado) {
                    n.setIdUsuarioExpidioNota(Long.valueOf(String.valueOf(nuevoEmployee)));
                }
                     if(n.getIdusuarioAnulado()==idEmpleado) {
                         n.setIdusuarioAnulado(Long.valueOf(String.valueOf(nuevoEmployee)));
                }
                         if(n.getIdusuarioEntregado()==idEmpleado) {
                             n.setIdusuarioEntregado(Long.valueOf(String.valueOf(nuevoEmployee)));
                }
                             if(n.getIdusuariocancelo()==idEmpleado) {
                                 n.setIdusuariocancelo(Long.valueOf(String.valueOf(nuevoEmployee)));
                }
            }
            em.flush();  
    }
    private long procesarDatosEmpleadoAdd(DatosEmpleado empleado) {        
        long retorno;       
              retorno=validarDatosEmpleado(empleado);//retorno el resultado de validar ambas cosas nombre, apellido, password
                if(retorno==0){
                        retorno =valorRetornadoAlBuscarEmailyNombreUsuario(retorno,empleado.getNumeroDocumento(),empleado.getEmail(),empleado.getNombreUsuario());                     
                                           if(retorno==0){
                                                    if(empleado.getTipoEmpleado().equals("FULLTIME")) {
                                                        retorno=addFullTimeEmpleado(empleado);
                                                    } else {
                                                        retorno=addPartTimeEmpleado(empleado);
                                                    }
                                            }else {
                                                retorno=numeroaRetornar(retorno);//retorno de buscarPersonaEmailNameUSer
                        }
                }
                return retorno;
    }
    private long addFullTimeEmpleado(DatosEmpleado empleado) {
        long retorno;            
            GregorianCalendar gc = new GregorianCalendar(Locale.getDefault());
            ProjectHelpers passwordTry = new ProjectHelpers();
                 FullTimeEmpleado empfulltime = new FullTimeEmpleado();
                                         empfulltime.setApellido(empleado.getApellido().toUpperCase());
                                         if(!empleado.getEmail().isEmpty())
                                            empfulltime.setEmail(empleado.getEmail().toLowerCase());
                                         empfulltime.setNombre(empleado.getNombre().toUpperCase());
                                         empfulltime.setNameuser(empleado.getNombreUsuario());
                                       
                                           String fraseEncriptada = passwordTry.encrypt(empleado.getPassword());
                                           if(!fraseEncriptada.equals("No Encrypted")){
                                                empfulltime.setPassword(empleado.getPassword());
                                                empfulltime.setKeyPassword(passwordTry.encryptionKey);
                                           }else{
                                               return -11;
                                           }                                       
                                         empfulltime.setGeneros(em.find(Generos.class, empleado.getIdGenero()));
                                         empfulltime.setEstado((short)1);
                                         empfulltime.setFechacarga(gc.getTime());
                                         empfulltime.setEmptype("FULLTIME");
                                         empfulltime.setObservaciones(empleado.getObservaciones());
                                         empfulltime.setTipodocumento(em.find(Tiposdocumento.class, empleado.getIdTipoDocumento()));
                                         empfulltime.setNrodocumento(empleado.getNumeroDocumento());
                                         if(empleado.getSalario().length()>0){
                                            empfulltime.setSalario(Double.valueOf(empleado.getSalario()));
                                         }else{
                                            empfulltime.setSalario(0); 
                                         }
                                         em.persist(empfulltime);                                         
                                       retorno = empfulltime.getIdPersona();    
                                       return retorno;        
    }
    private long addPartTimeEmpleado(DatosEmpleado empleado) {
        long retorno;       
            GregorianCalendar gc = new GregorianCalendar(Locale.getDefault());
            ProjectHelpers passTry = new ProjectHelpers();
              EmpleadoParttime empparttime = new EmpleadoParttime();
                                                    empparttime.setApellido(empleado.getApellido().toUpperCase());
                                                    empparttime.setNameuser(empleado.getNombreUsuario());                                      
                                                    empparttime.setNombre(empleado.getNombre().toUpperCase());
                                                    empparttime.setFechacarga(gc.getTime());                                                                                                           
                                                        if(!passTry.encrypt(empleado.getPassword()).equals("No Encrypted"))  {  
                                                            empparttime.setPassword(empleado.getPassword());
                                                            empparttime.setKeyPassword(passTry.encryptionKey);
                                                        }else{
                                                            return -11;
                                                        }
                                                    empparttime.setEmptype("PARTTIME");
                                                    empparttime.setTipodocumento(em.find(Tiposdocumento.class, empleado.getIdTipoDocumento()));
                                                    empparttime.setNrodocumento(empleado.getNumeroDocumento());
                                                    empparttime.setGeneros(em.find(Generos.class, empleado.getIdGenero()));
                                                    empparttime.setEstado((short)1);
                                                    if(!empleado.getEmail().isEmpty())
                                                         empparttime.setEmail(empleado.getEmail());
                                                    empparttime.setSalarioporhora(Double.valueOf(empleado.getSalarioxhora()));
                                                    em.persist(empparttime);                                                    
                                                    retorno = empparttime.getIdPersona();  
        
           return retorno;        
    }     
    private long validarDatosEmpleado(DatosEmpleado empleado) {
        long retorno=0;                  
         String numeroDocumento = String.valueOf(empleado.getNumeroDocumento()); 
            
            if(!validarDatosEmpleadoUser.validate(numeroDocumento,numberPattern_Dni)){
                retorno =-16;
            }else {            
                if(!empleado.getNombre().isEmpty()&&validarDatosEmpleadoUser.validate(empleado.getNombre(),nombreyApellidoPattern)){
                    if(!empleado.getApellido().isEmpty()&&validarDatosEmpleadoUser.validate(empleado.getApellido(),nombreyApellidoPattern)){
                        if(empleado.getPassword().equals(empleado.getPasswordre())){
                            if(!validarDatosEmpleadoUser.validate(empleado.getNombreUsuario(),nameUser_pattern)) {
                                retorno=-12;
                            }
                            
                            if(!validarDatosEmpleadoUser.validate(empleado.getPassword(),password_pattern) && (empleado.getPassword().equals(empleado.getPasswordre()))) {
                                retorno=-11;
                            
                            }else{                                
                                    if((!empleado.getEmail().isEmpty())&&validarDatosEmpleadoUser.isValidEmailAddress(empleado.getEmail())){
                                    //if((!empleado.getEmail().isEmpty())&&validarDatosEmpleadoUser.validate(empleado.getEmail(),email_Pattern)){
                                        retorno=-18;
                                    
                                    }
                                    else
                                    {                                
                                             
                                             
                                             if(!validarDatosEmpleadoUser.validate(empleado.getPassword(),password_pattern)){
                                                 retorno = -17;
                                             }
                                    }             
                            }              
                        }else{
                            retorno =-13;
                        }
                    }else {
                        retorno =-15;
                    }
                }else {
                    retorno = -14;
                }
            }
            return retorno;        
    }
    private DatosEmpleado datosEmpleadosObject(String xmlEmpleado) {
        DatosEmpleado datosEmpleado;     
             XStream xstream = new XStream(new StaxDriver());
            xstream.alias("Empleado", DatosEmpleado.class);  
                datosEmpleado =(DatosEmpleado) xstream.fromXML(xmlEmpleado);
             return datosEmpleado; 
    }
    private long valorRetornadoAlBuscarEmailyNombreUsuario(long indice,Integer numeroDocumento, String email, String nombreUsuario) {
        long retorno = 0;      
             switch((int)indice){
            case -11:break;//no valida el password
            case -12:break;    // no valida el nombre de usuario
            case -13:break;    
            case -14:break;//no valida el nombre del empleado
            case -15:break; // no valida el apellido
            case -16:break;// mo valida el numero de documento    
            default:{
                retorno = buscarEmpleadoEmailAndNameUser(numeroDocumento,email,nombreUsuario);     
            }
        }       
            return retorno;        
    }
    @Override
    public String checkPassEmployee(long idEmpleado,String pass) {
        String passKey = "";
        String result;        
            Query consulta = em.createNamedQuery("Empleados.chkpass");
            ProjectHelpers passTry = new ProjectHelpers();
            consulta.setParameter("1", idEmpleado);
            List<Empleados>empleado = consulta.getResultList();
            
            if(!empleado.isEmpty()){
                for (Empleados empleados : empleado) {
                     passKey = empleados.getPassword();
                        passTry.decrypt(empleados.getKeyPassword(),empleados.getPassword());
                    
                }
                result = String.valueOf(passKey.equals(pass));
            }else{
                result = "Empleado no encontrado";
            }      
            return  result;
    }
    private long processEmployee(DatosEmpleado empleado, String xmlEmpleado) {
         int retEmpleadoEmptype;
        int retEmployee;
        long retorno = 0;
        //selecciono la persona con el tipo de empleado a buscar
                        ProjectHelpers passTry = new ProjectHelpers();
                                      Query sqlEmpleadoEmptype =em.createQuery("Select e From Empleados e "
                                                       + "Where e.idPersona = :idpersona and e.emptype like :emptype");
                                                   sqlEmpleadoEmptype.setParameter("idpersona",(long) empleado.getId());
                                                   sqlEmpleadoEmptype.setParameter("emptype", empleado.getTipoEmpleado());
                                                   //el resultado de la consulta de empleado
                                                   retEmpleadoEmptype=sqlEmpleadoEmptype.getResultList().size();
                                                   Query slqEmployee=em.createQuery("Select e From Empleados e Where e.idPersona = :idpersona");
                                                           slqEmployee.setParameter("idpersona",(long) empleado.getId());
                                                   retEmployee=slqEmployee.getResultList().size();
                                                   Query sqlParttimeEmployee=em.createQuery("Select e From EmpleadoParttime e Where "
                                                               + "e.idPersona = :idpersona");
                                                                       sqlParttimeEmployee.setParameter("idpersona",(long) empleado.getId());

                                                       Query sqlFullTimeEmployee=em.createQuery("Select f From FullTimeEmpleado f Where "
                                                               + "f.idPersona = :idpersona");
                                                                       sqlFullTimeEmployee.setParameter("idpersona", (long)empleado.getId());


                     if(retEmployee==1){
                         
                                          Query sqlemail = em.createQuery("SELECT p FROM Personas p WHERE p.email = :email");
                                                 sqlemail.setParameter("email", empleado.getEmail());
                                                    Query sqlusername=em.createQuery("SELECT e FROM Empleados e WHERE e.nameuser = :nameuser");
                                                       sqlusername.setParameter("nameuser", empleado.getNombreUsuario());
                                                       if(empleado.getTipoEmpleado().equals("FULLTIME")&&retEmpleadoEmptype==0){                                                                    
                                                                       Query deletePartTimeEmployee=em.createNamedQuery("EmpleadoParttime.deleteById");
                                                                           deletePartTimeEmployee.setParameter("idPersona", (long)empleado.getId());
                                                                           deletePartTimeEmployee.executeUpdate();

                                                                       Query deleteEntityEmployee=em.createNamedQuery("Empleados.deleteById");
                                                                       deleteEntityEmployee.setParameter("idPersona",(long) empleado.getId());
                                                                           deleteEntityEmployee.executeUpdate();

                                                                       Query deleteEntityPersona=em.createNamedQuery("Personas.deleteById");
                                                                       deleteEntityPersona.setParameter("idPersona", (long)empleado.getId());
                                                                           deleteEntityPersona.executeUpdate();
                                                                       em.flush();         //                         
                                                                       long nuevoEmployee = addEmpleadoFullTime(xmlEmpleado);
                                                                       actualizarReferenciasConNotasdePedido(nuevoEmployee,empleado.getId());
                                                                       retorno=nuevoEmployee;
                                                               }else{
                                                                   if(empleado.getTipoEmpleado().equals("PARTTIME")&&retEmpleadoEmptype==0){   
                                                                       Query deletePartTimeEmployee=em.createNamedQuery("EmpleadoParttime.deleteById");
                                                                               deletePartTimeEmployee.setParameter("idPersona", (long)empleado.getId());
                                                                               deletePartTimeEmployee.executeUpdate();
                                                                               Query deleteEntityEmployee=em.createNamedQuery("Empleados.deleteById");
                                                                               deleteEntityEmployee.setParameter("idPersona",(long) empleado.getId());
                                                                               deleteEntityEmployee.executeUpdate();
                                                                               Query deleteEntityPersona=em.createNamedQuery("Personas.deleteById");
                                                                               deleteEntityPersona.setParameter("idPersona",(long) empleado.getId());
                                                                               deleteEntityPersona.executeUpdate();
                                                                               em.flush();
                                                                       long nuevoEmployee = addEmpleadoParttime(xmlEmpleado);
                                                                       actualizarReferenciasConNotasdePedido(nuevoEmployee, empleado.getId());
                                                                       retorno = nuevoEmployee;
                                                                   }else{  
                                                                           if(empleado.getTipoEmpleado().equals("FULLTIME")){
                                                                                       FullTimeEmpleado fulltimeEmploy=em.find(FullTimeEmpleado.class,empleado.getId());
                                                                                                       fulltimeEmploy.setApellido(empleado.getApellido());
                                                                                                       if(sqlemail.getResultList().isEmpty()&&empleado.getEmail().contains("@")) {
                                                                                                           fulltimeEmploy.setEmail(empleado.getEmail());          
                                                                                                       }
                                                                                                       fulltimeEmploy.setGeneros(em.find(Generos.class, empleado.getIdGenero()));
                                                                                                       if(sqlusername.getResultList().isEmpty()) {
                                                                                                           fulltimeEmploy.setNameuser(empleado.getNombreUsuario());
                                                                                                       }
                                                                                                       fulltimeEmploy.setNombre(empleado.getNombre());
                                                                                                       fulltimeEmploy.setSalario(Double.valueOf(empleado.getSalario()));
                                                                                                       fulltimeEmploy.setObservaciones(empleado.getObservaciones());
                                                                                                       fulltimeEmploy.setPassword(passTry.encrypt(empleado.getPassword()));
                                                                                                       fulltimeEmploy.setKeyPassword(passTry.encryptionKey);
                                                                                                       fulltimeEmploy.setTipodocumento(em.find(Tiposdocumento.class, empleado.getIdTipoDocumento()));
                                                                                                       em.persist(fulltimeEmploy);                                                                                                     
                                                                                                       retorno = fulltimeEmploy.getIdPersona();
                                                                           }else{
                                                                               EmpleadoParttime  empleadoPartime=em.find(EmpleadoParttime.class, empleado.getId());
                                                                                               empleadoPartime.setApellido(empleado.getApellido());                                                                                       
                                                                                                       if(sqlemail.getResultList().isEmpty()&&empleado.getEmail().contains("@")) {
                                                                                                           empleadoPartime.setEmail(empleado.getEmail());
                                                                                                       }
                                                                                                       empleadoPartime.setGeneros(em.find(Generos.class, empleado.getIdGenero()));
                                                                                                       if(sqlusername.getResultList().isEmpty()) {
                                                                                                           empleadoPartime.setNameuser(empleado.getNombreUsuario());
                                                                                                       }
                                                                                                       empleadoPartime.setNombre(empleado.getNombre());
                                                                                                       empleadoPartime.setSalarioporhora(Double.valueOf(empleado.getSalarioxhora()));
                                                                                                       empleadoPartime.setObservaciones(empleado.getObservaciones());
                                                                                                       empleadoPartime.setPassword(passTry.encrypt(empleado.getPassword())); 
                                                                                                       empleadoPartime.setKeyPassword(passTry.encryptionKey); 
                                                                                                       empleadoPartime.setTipodocumento(em.find(Tiposdocumento.class, empleado.getIdTipoDocumento()));
                                                                                                       em.persist(empleadoPartime);
                                                                                                       retorno = empleadoPartime.getIdPersona();
                                                                           }
                                                                   }
                                                               }
                                                       em.flush();
                                                }  //end if
                     
                        return retorno;                                               
        }
        
}