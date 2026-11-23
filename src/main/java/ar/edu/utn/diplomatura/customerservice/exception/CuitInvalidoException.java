package ar.edu.utn.diplomatura.customerservice.exception;

public class CuitInvalidoException extends RuntimeException{

    public CuitInvalidoException(String cuit){
        super("El cuit " + cuit + " es inválido, revisar que contenga 11 dígitos.");
    }
}
