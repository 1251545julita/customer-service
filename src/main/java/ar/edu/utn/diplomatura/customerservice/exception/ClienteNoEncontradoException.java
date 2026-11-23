package ar.edu.utn.diplomatura.customerservice.exception;

public class ClienteNoEncontradoException extends RuntimeException {

    public ClienteNoEncontradoException(Long id){
        super("No exite un client con el id " + id);
    }
}
