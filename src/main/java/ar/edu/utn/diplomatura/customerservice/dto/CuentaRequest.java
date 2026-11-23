package ar.edu.utn.diplomatura.customerservice.dto;

import java.math.BigDecimal;

/** Es un DTO de entrada. Representa los datos que se envían para solicitar la creación de una cuenta.
 */
public record CuentaRequest(

        String tipo, // Tipo de cuenta, si es caja de ahorro o cuenta corriente.
        String moneda,
        Long clienteId,
        BigDecimal saldoInicial,
        BigDecimal porcentajeTasaInteresAnual,
        BigDecimal limiteDescubierto
) {}