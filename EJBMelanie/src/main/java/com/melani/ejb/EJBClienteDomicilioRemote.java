package com.melani.ejb;
import javax.ejb.Remote;

@Remote
public interface EJBClienteDomicilioRemote {
    public String addRelacionClienteDomicilio(long idCliente, long idDomicilio,int idUsuario);       
}
