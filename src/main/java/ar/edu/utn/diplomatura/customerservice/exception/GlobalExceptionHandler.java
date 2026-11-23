package ar.edu.utn.diplomatura.customerservice.exception;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Errores de microservicios externos (Product-Service)
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String, String>> manejarFeign(FeignException ex, HttpServletRequest request) {

        log.warn("{} {} -> Product-Service respondió {}", request.getMethod(), request.getRequestURI(), ex.status());

        return switch (ex.status()) {
            case 404 -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Cliente o cuenta no encontrada."));
            case 400 -> ResponseEntity.badRequest()
                    .body(Map.of("error", "Solicitud inválida rechazada por Product-Service."));
            case -1  -> ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)      // caída o timeout
                    .body(Map.of("error", "Product-Service no está disponible."));
            default  -> ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "Error inesperado en Product-Service."));
        };
    }

    // Excepciones propias del dominio
    @ExceptionHandler(ClienteNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> manejarClienteNoEncontrado( ClienteNoEncontradoException ex, HttpServletRequest request) {

        log.warn("{} {}", request.getMethod() + request.getRequestURI(), ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(CuitInvalidoException.class)
    public ResponseEntity<Map<String, String>> manejarCuitInvalido( CuitInvalidoException ex, HttpServletRequest request) {

        log.warn("{} - {}", request.getMethod() + request.getRequestURI(), ex.getMessage());

        return ResponseEntity
                .badRequest()
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(DniInvalidoException.class)
    public ResponseEntity<Map<String, String>> manejarDniInvalido( DniInvalidoException ex, HttpServletRequest request) {

        log.warn("{} - {}", request.getMethod() + request.getRequestURI(), ex.getMessage());

        return ResponseEntity
                .badRequest()
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(EmailDuplicadoException.class)
    public ResponseEntity<Map<String, String>> manejarEmailDuplicado( EmailDuplicadoException ex, HttpServletRequest request) {

        log.warn("{} - {}", request.getMethod() + request.getRequestURI(), ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> manejarErroresValidacion( MethodArgumentNotValidException ex) {

        Map<String, String> errores = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errores);
    }

    @ExceptionHandler(ModoEliminacionInvalidoException.class)
    public ResponseEntity<String> manejarModoInvalido( ModoEliminacionInvalidoException ex, HttpServletRequest request) {

        log.warn("{} - {}", request.getMethod() + request.getRequestURI(), ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
}
