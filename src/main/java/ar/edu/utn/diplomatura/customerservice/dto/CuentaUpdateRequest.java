package ar.edu.utn.diplomatura.customerservice.dto;

import java.math.BigDecimal;

public record CuentaUpdateRequest(

        BigDecimal porcentajeTasaInteresAnual,
        BigDecimal limiteDescubierto
) {}
