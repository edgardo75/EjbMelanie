package com.melani.ejb;
import javax.ejb.Remote;
@Remote
public interface EJBClientesRemote {
    String obtenerCliente(long idCliente);
    String obtenerClienteXTipoAndNumeroDocu(short idTipo, int nrodDocu);
    String getCustomerDocNumber(Integer docNumber);
    String searchClientForNameAndLastName(String name,String lastname);
//    String addClienteDatosPersonales(String datospersonalescliente);
}
