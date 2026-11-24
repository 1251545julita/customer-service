package ar.edu.utn.diplomatura.customerservice.controller;

import java.util.List;

import ar.edu.utn.diplomatura.customerservice.client.CuentasClient;
import ar.edu.utn.diplomatura.customerservice.dto.CuentaDTO;
import ar.edu.utn.diplomatura.customerservice.dto.CuentaRequest;
import ar.edu.utn.diplomatura.customerservice.dto.CuentaResponse;
import ar.edu.utn.diplomatura.customerservice.dto.CuentaUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ar.edu.utn.diplomatura.customerservice.entity.Cliente;
import ar.edu.utn.diplomatura.customerservice.service.ClienteService;

/**
 * Puerta de entrada HTTP. Recibe la peticion, llama al service y devuelve el
 * resultado. No lleva reglas de negocio.
 *
 * @RestController convierte a JSON lo que devuelven los metodos.
 * @RequestMapping("/clientes") es el prefijo comun de todas las rutas.
 */
@RestController
@RequestMapping("/clientes")
@Tag(name = "Clientes", description = "Alta, baja, consulta y acreditaciones de clientes del banco")
public class ClienteController {

    private final ClienteService clienteService;
    private final CuentasClient cuentasClient;

    public ClienteController(ClienteService clienteService, CuentasClient cuentasClient) {
        this.clienteService = clienteService;
        this.cuentasClient = cuentasClient;
    }

    /**
     * GET /clientes              -> todos
     * GET /clientes?numeroDocumento=xxx -> filtra por numeroDocumento
     *
     * @RequestParam es lo que va despues del "?" en la URL.
     */
    @Operation(summary = "Lista todos los clientes",
            description = "Devuelve la lista completa. Con el parametro 'numeroDocumento' filtra por "
                    + "coincidencia parcial, sin distinguir mayusculas.")
    @ApiResponse(responseCode = "200", description = "Lista de clientes (puede venir vacia)")
    @GetMapping
    public List<Cliente> listar(@Parameter(description = "Parte del numeroDocumento a buscar", example = "gomez")
                                @RequestParam(required = false) String numeroDocumento) {
        if (numeroDocumento == null || numeroDocumento.isBlank()) {
            return clienteService.listarTodos();
        }
        return clienteService.buscarPorNumeroDocumento(numeroDocumento);
    }

    /**
     * GET /clientes/1
     *
     * @PathVariable toma el "1" de la URL.
     */
    @Operation(summary = "Obtener un cliente por id")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado.")
    @ApiResponse(responseCode = "404", description = "El cliente no encontrado.")
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.obtenerOFallar(id));
    }

    /**
     * POST /clientes
     *
     * @RequestBody convierte el JSON del cuerpo en un objeto Cliente.
     */
    @Operation(summary = "Registrar un cliente")
    @ApiResponse(responseCode = "201", description = "Cliente creado.")
    @ApiResponse(responseCode = "400", description = "Datos inválidos al intentar crear un cliente, por ejemplo, dni invalido")
    @ApiResponse(responseCode = "409", description = "El email ya está registrado")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> crear(@RequestBody Cliente unCliente) {
        Cliente clienteCreado = clienteService.crear(unCliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteCreado);
    }

    /**
     * PUT /clientes/1
     *
     * @RequestBody convierte el JSON del cuerpo en un objeto Cliente con datos actualizados.
     */
    @Operation(summary = "Actualiza un cliente existente",
            description = "Actualiza nombre, apellido, email y cuentas asociadas. Devuelve null si el id no existe.")
    @ApiResponse(responseCode = "200", description = "Cuenta actualizada.")
    @ApiResponse(responseCode = "404", description = "Cuenta indicada no encontrada.")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Cliente datosNuevos) {
        return ResponseEntity.ok(clienteService.actualizar(id, datosNuevos));
    }

    /**
     * DELETE /clientes/6
     *
     * @PathVariable envia por path param el id del cliente existente.
     * @RequestParam envia por path param el modo de cómo eliminar ese cliente existente. Puede ser lógico o físico.
     */
    @Operation(summary = "Borra logica o fisicamente un cliente existente",
            description = "Deshabilita un cliente existente en modo logico. En modo fisico, borra fisicamente de la Base de Datos.")
    @ApiResponse(responseCode = "204", description = "Cliente deshabilitado.")
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado o ya se borró.")
    @ApiResponse(responseCode = "400", description = "El modo debe ser 'logico' o 'fisico' en los parametros.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, @RequestParam(defaultValue = "logico") String modo) {
        clienteService.eliminarCliente(id, modo);

        return ResponseEntity.noContent().build();
    }

    /** URLs para el manejo de las cuentas bancarias **/

    /**
     * GET /clientes/{clienteId}/cuentas
     *
     * Lista todas las cuentas asociadas a un cliente.
     *
     * @PathVariable obtiene el identificador del cliente.
     * @RequestParam permite filtrar las cuentas por número de cuenta.
     */
    @Operation(summary = "Lista las cuentas de un cliente",
            description = "Devuelve todas las cuentas asociadas al cliente indicado. "
                    + "Con el parámetro 'numeroCuenta' filtra por coincidencia parcial, sin distinguir mayúsculas.")
    @ApiResponse(responseCode = "200", description = "Lista de cuentas (puede venir vacía)")
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado.")
    @GetMapping("/{clienteId}/cuentas")
    public List<CuentaDTO> listarCuentas(@PathVariable Long clienteId, @RequestParam(required = false) String numeroCuenta) {
        if (numeroCuenta == null || numeroCuenta.isBlank()) {
            return clienteService.listarCuentas(clienteId);
        }

        return clienteService.buscarCuentas(clienteId, numeroCuenta);
    }

    /**
     * GET /clientes/{clienteId}/cuentas/{cuentaId}
     *
     * Obtiene una cuenta específica asociada a un cliente.
     *
     * @PathVariable obtiene el identificador del cliente.
     * @PathVariable obtiene el identificador de la cuenta.
     */
    @Operation(summary = "Obtiene una cuenta de un cliente",
            description = "Devuelve los datos de la cuenta que coincide con el ID indicado y pertenece al cliente especificado.")
    @ApiResponse(responseCode = "200", description = "Cuenta encontrada.")
    @ApiResponse(responseCode = "404", description = "Cliente o cuenta no encontrados.")
    @GetMapping("/{clienteId}/cuentas/{cuentaId}")
    public CuentaDTO obtenerCuenta(@PathVariable Long clienteId, @PathVariable Long cuentaId) {
        return clienteService.obtenerCuenta(clienteId, cuentaId);
    }

    /**
     * POST /clientes/cuentas
     *
     * Crea una cuenta para un cliente existente.
     *
     * @RequestBody convierte el JSON del cuerpo en un objeto CuentaRequest.
     * Este objeto contiene los datos necesarios para crear la cuenta.
     */
    @Operation(summary = "Crea una cuenta para un cliente",
            description = "Solicita a app-cuentas la creación de una Caja de Ahorro o una Cuenta Corriente para un cliente existente. "
                    + "El número de cuenta lo genera el sistema.")
    @ApiResponse(responseCode = "201", description = "Cuenta creada.")
    @ApiResponse(responseCode = "400", description = "Tipo de cuenta inválido o campos incorrectos.")
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado.")
    @PostMapping("/cuentas")
    public ResponseEntity<CuentaResponse> crearCuenta(@Valid @RequestBody CuentaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clienteService.crearCuenta(request));
    }

    /**
     * PUT /clientes/{clienteId}/cuentas/{cuentaId}
     *
     * Actualiza los datos específicos de una cuenta existente.
     *
     * @PathVariable obtiene el identificador del cliente.
     * @PathVariable obtiene el identificador de la cuenta.
     * @RequestBody convierte el JSON del cuerpo en un objeto CuentaUpdateRequest.
     * Este objeto contiene los campos que se desean actualizar.
     */
    @Operation(summary = "Actualiza una cuenta de un cliente",
            description = "Solicita a app-cuentas la actualización del porcentajeTasaInteresAnual de una Caja de Ahorro "
                    + "o del limiteDescubierto de una Cuenta Corriente.")
    @ApiResponse(responseCode = "200", description = "Cuenta actualizada.")
    @ApiResponse(responseCode = "404", description = "Cliente o cuenta no encontrados.")
    @ApiResponse(responseCode = "400", description = "Campos que no corresponden al tipo de cuenta.")
    @PutMapping("/{clienteId}/cuentas/{cuentaId}")
    public CuentaResponse actualizarCuenta(@PathVariable Long clienteId, @PathVariable Long cuentaId, @Valid @RequestBody CuentaUpdateRequest request) {
        return clienteService.actualizarCuenta(
                clienteId,
                cuentaId,
                request
        );
    }
}