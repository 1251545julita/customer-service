package ar.edu.utn.diplomatura.customerservice.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import ar.edu.utn.diplomatura.customerservice.Constants;
import ar.edu.utn.diplomatura.customerservice.entity.PersonaFisica;
import ar.edu.utn.diplomatura.customerservice.entity.PersonaJuridica;
import ar.edu.utn.diplomatura.customerservice.entity.Preferencial;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import ar.edu.utn.diplomatura.customerservice.entity.Cliente;
import ar.edu.utn.diplomatura.customerservice.repository.ClienteRepository;

/**
 * Carga clientes de ejemplo al arrancar.
 *
 * Hace falta porque la base H2 vive en memoria: al apagar la aplicacion se
 * pierde todo, asi que cada arranque empieza con estos cinco.
 */
@Component
@Order(2)
public class CargaDatosIniciales implements CommandLineRunner {

    private final ClienteRepository clienteRepository;

    public CargaDatosIniciales(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public void run(String... args) {
        if (clienteRepository.count() > 0) {
            return;
        }

        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
        LocalDate fechaAhora = LocalDate.now();

        Cliente cliente1 = new PersonaFisica(
                "Juan",
                "30123456",
                "Calle 123, CABA",
                "11-5555-4444",
                "juan.perez@email.com",
                fechaAhora,
                true,
                "Sucursal 045 - Belgrano",
                "Perez",
                LocalDate.of(1990, 6, 15),
                "Ingeniero en Sistemas",
                new BigDecimal("750000.5024"),
                "Soltero",
                "Argentina",
                "Relación de Dependencia"
        );

        Cliente cliente2 = new PersonaJuridica(
                "DG Solutions SA",
                "30-12345678-9",
                "Av. Corrientes 456, CABA",
                "11-4444-5555",
                "contacto@dgsolutions.com",
                fechaAhora,
                true,
                "Sucursal 110 - Centro Corporativo",
                "DG Solutions Sociedad Anónima",
                "Desarrollo de Software",
                "Marta González",
                "Responsable Inscripto",
                LocalDate.of(2017, 4, 20),
                new BigDecimal("45000000.008")
        );

        List<String> beneficios = Arrays.asList("Acceso a VIP en aeropuertos", "Atención 24/7", "Cero comisiones de mantenimiento");
        Cliente cliente3 = new Preferencial(
                "Laura",
                "dni",
                "36852753",
                "Av. del Libertador 474, CABA",
                "11-3333-2222",
                "laura.vip@email.com",
                fechaAhora,
                true,
                "Sucursal 001 - Casa Central VIP",
                new BigDecimal("5000000.0064"),
                98765L,
                beneficios,
                LocalDate.of(1997, 1, 22),
                true
        );

        Cliente cliente4 = new PersonaFisica(
                "Federico",
                "20334455",
                "Calle 456, CABA",
                "11-7777-0011",
                "federico.luna@mail.com",
                fechaAhora,
                true,
                "Sucursal 047 - Belgrano",
                "Luna",
                LocalDate.of(1990, 6, 15),
                "Ingeniero en Sistemas",
                new BigDecimal("750000.5000"),
                "Soltero",
                "Argentina",
                "Relación de Dependencia"
        );

        // saveAll hace un INSERT por cada elemento de la lista.
        clienteRepository.saveAll(List.of( cliente1, cliente2, cliente3));

        cliente4.setActivo(false);
        clienteRepository.save(cliente4);

        System.out.println("=== " + clienteRepository.count() + " clientes cargados en MySQL ===");
    }
}
