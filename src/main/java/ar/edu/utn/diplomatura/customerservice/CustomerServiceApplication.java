package ar.edu.utn.diplomatura.customerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Punto de entrada. Ejecutar el main de esta clase levanta la aplicacion.
 *
 * @SpringBootApplication hace tres cosas:
 *   - marca esta clase como configuracion,
 *   - configura la aplicacion sola segun las librerias del pom.xml
 *     (por eso arranca Tomcat en el 8080 y se conecta a H2),
 *   - recorre este paquete y los de abajo buscando clases anotadas
 *     (@Component, @Service, @Repository, @RestController) para crearlas.
 *
 * Por eso toda clase que Spring deba gestionar tiene que estar dentro de
 * ar.edu.utn.diplomatura.customerservice o en un subpaquete.
 */
@SpringBootApplication
@EnableFeignClients
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);

        System.out.println();
        System.out.println("===========================================================");
        System.out.println("  Microservicio B - API REST Customers");
        System.out.println("  App:         http://localhost:8081/clientes");
        System.out.println("               JDBC URL: jdbc:mysql://localhost:3306/customer-service   user: root");
        System.out.println("===========================================================");
        System.out.println();

    }
}
