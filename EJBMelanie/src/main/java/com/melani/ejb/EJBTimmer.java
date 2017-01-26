package com.melani.ejb;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
@Stateless
@WebService
public class EJBTimmer {
    @PersistenceContext()
    EntityManager em;    
    final String servidorSMTP = ResourceBundle.getBundle("email").getString("mail.smtp.host");                
    final String puertoEnvio = ResourceBundle.getBundle("email").getString("mail.smtp.port");
    Session ses = null;    
    @Schedule(persistent = false,timezone = "/*America/Argentina/San_Juan*/",second = "00",hour = "21",minute = "30")            
    private void ventasDiarias(){ 
         SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
          Query consulta = em.createQuery("SELECT SUM(n.montototalapagar) FROM Notadepedido n WHERE cast(n.fechadecompra as DATE) = CURRENT_DATE");
        
                    Properties props = new Properties();                    
                    props.put("mail.smtp.host", servidorSMTP);
                    props.put("mail.smtp.port", puertoEnvio);
                    props.put("mail.smtp.starttls.enable", "true");
                    props.put("mail.transport.protocol","smtp");
                    props.put("mail.smtp.auth", "true");                  
                    try {     
                        
                   Session session = Session.getInstance(props, new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication(){
                         return new PasswordAuthentication("fernan2bal@hotmail.com", "30emma30");                                 
                        }
                    });
                    final MimeMessage message = new MimeMessage(session);//                                      }                          
                    message.setFrom(new InternetAddress("fernan2bal@hotmail.com"));
                    message.addRecipient(Message.RecipientType.TO,new InternetAddress("fernan2bal@hotmail.com"));                    
                    Multipart multipart = new MimeMultipart("alternative");
                        MimeBodyPart textPart = new MimeBodyPart();
                        String textContext ;
                        String resultParser = consulta.getResultList().toString().replace("[", "").replace("]","");
                        if(resultParser.equals("null")){
                             textContext="Ventas del Día "+sdf.format(Calendar.getInstance().getTime())+" $ 0";
                        }else
                            textContext="Ventas del Día "+sdf.format(Calendar.getInstance().getTime())+" $ "+resultParser;                        
                        message.setSubject(textContext);                        
                        textPart.setText(textContext);
                        MimeBodyPart htmlPart = new MimeBodyPart();
                        String htmlContext = "<html>"+"<h1>Hola</h1>"+
                                "<p>"+textContext+"</p>"+"</html>";
                        htmlPart.setContent(htmlContext, "text/html");
                        multipart.addBodyPart(textPart);
                        multipart.addBodyPart(htmlPart);
                        message.setContent(multipart);                        
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {                                                                        
                                    Transport.send(message);  
                                    
                                } catch (MessagingException ex) {
                                    java.util.logging.Logger.getLogger(EJBTimmer.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }).start();
                    } catch (MessagingException ex) {
                       Logger.getLogger("Error en EJBTimmer "+ex.getMessage());
                    }
    }    
    public String obtenerIPAddress() {       
        return null;
    }
}
