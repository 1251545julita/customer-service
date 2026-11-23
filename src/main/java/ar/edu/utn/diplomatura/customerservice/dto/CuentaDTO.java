package ar.edu.utn.diplomatura.customerservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Lo que este servicio necesita saber de una cuenta.
 *
 * Tiene la forma del JSON que devuelve el servicio de productos.
 * Representar la información de una cuenta cuando se consulta una o varias cuentas.
 * No es una entidad (no lleva @Entity): este servicio no tiene base de datos.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CuentaDTO(

        Long id,
        String numeroCuenta,
        String tipoCuenta,
        BigDecimal saldo,
        String moneda,
        LocalDateTime fechaDeApertura,
        boolean habilitada,
        Long clienteId,
        BigDecimal limiteDescubierto, // Específico de cuenta corriente
        BigDecimal porcentajeTasaInteresAnual // Específico de caja de ahorro
) {
}
