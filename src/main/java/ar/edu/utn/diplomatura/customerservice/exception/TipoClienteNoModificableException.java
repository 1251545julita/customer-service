package ar.edu.utn.diplomatura.customerservice.exception;

public class TipoClienteNoModificableException extends RuntimeException {

    public TipoClienteNoModificableException(){
        super("No se puede cambiar el tipo de cliente (tipoDocumento) en una actualización");
    }
}