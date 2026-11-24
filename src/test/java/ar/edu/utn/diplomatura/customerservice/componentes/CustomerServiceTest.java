package ar.edu.utn.diplomatura.customerservice.componentes;

import ar.edu.utn.diplomatura.customerservice.client.CuentasClient;
import ar.edu.utn.diplomatura.customerservice.entity.Cliente;
import ar.edu.utn.diplomatura.customerservice.entity.PersonaFisica;
import ar.edu.utn.diplomatura.customerservice.exception.EmailDuplicadoException;
import ar.edu.utn.diplomatura.customerservice.repository.ClienteRepository;
import ar.edu.utn.diplomatura.customerservice.service.ClienteService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    ClienteRepository clienteRepository;
    @Mock
    CuentasClient cuentasClient;
    @InjectMocks
    ClienteService clienteService;
    @Mock
    ValidadorDeDni validadorDeDni;

    private PersonaFisica personaFisica() {
        return new PersonaFisica("Irene", "39123456", "Av. Corrientes 987", "11-8589-5555",
                "irene23@gmail.com", null, null, "Centro", "Rodriguez", LocalDate.of(1990, 5, 10),
                "Contadora", new BigDecimal("1500000"), "Soltera", "Argentina", "Dependencia");
    }

    @Test
    void crear_completaAltaYGuarda() {
        when(validadorDeDni.esValido("39123456"))
                .thenReturn(true);

        when(clienteRepository.existsByCorreoElectronico("irene23@gmail.com"))
                .thenReturn(false);

        when(clienteRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        Cliente creado = clienteService.crear(personaFisica());

        assertTrue(creado.getActivo());
        assertEquals(LocalDate.now(), creado.getFechaDeAlta());

        verify(clienteRepository).save(any());
    }

    @Test
    void crear_conEmailDuplicado_lanzaExcepcionYNoGuarda() {
        when(validadorDeDni.esValido("39123456"))
                .thenReturn(true);

        when(clienteRepository.existsByCorreoElectronico("irene23@gmail.com"))
                .thenReturn(true);

        assertThrows(
                EmailDuplicadoException.class,
                () -> clienteService.crear(personaFisica())
        );

        verify(clienteRepository, never()).save(any());
    }
}