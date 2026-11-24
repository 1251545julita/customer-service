package ar.edu.utn.diplomatura.customerservice.exception;

import java.math.BigDecimal;

public class SaldoInvalidoException extends RuntimeException {

    public SaldoInvalidoException(BigDecimal saldo){
        super("El saldo " + saldo + " es inválido. Debe ser mayor a cero.");
    }
}
