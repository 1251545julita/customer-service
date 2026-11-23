package ar.edu.utn.diplomatura.customerservice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Es un DTO de salida. Representa los datos que se devuelve después de crear o actualizar una cuenta.
 */
public record CuentaResponse(

        Long id,
        String numeroCuenta,
        String tipo,
        String moneda,
        BigDecimal saldo,
        LocalDate fechaDeApertura,
        boolean habilitada
) {}