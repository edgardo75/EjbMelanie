package com.melani.ejb;

import com.melani.entity.Notadepedido;
import javax.ejb.Remote;

@Remote
public interface EJBEntradasSalidaDiariasRemote {
    String searchAllEntradasySalidasForCurrentDate();
    byte insertarEntradaSalidaManual(String monto, byte valorEntradaSalidaBit,String detalle);
    String selectAllEntradasYSalidasPorTurno();    
    public long calculosPorNumerodeCupon(double anticipo,Notadepedido nota);
    long calculosPorAnticipoNotaPedido(double anticipo,Notadepedido notadePedido);    
}
