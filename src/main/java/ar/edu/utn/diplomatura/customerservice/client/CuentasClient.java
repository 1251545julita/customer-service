package ar.edu.utn.diplomatura.customerservice.client;

import java.math.BigDecimal;
import java.util.List;

import ar.edu.utn.diplomatura.customerservice.dto.CuentaDTO;
import ar.edu.utn.diplomatura.customerservice.dto.CuentaRequest;
import ar.edu.utn.diplomatura.customerservice.dto.CuentaResponse;
import ar.edu.utn.diplomatura.customerservice.dto.CuentaUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


/**
 * Cuentas Feign del servicio de cuentas (product-service).
 *
 * Es solo una interfaz: la implementacion (abrir la conexion, mandar el pedido,
 * leer la respuesta, pasar el JSON a objetos) la escribe Feign al arrancar.
 *
 * name: identificador del servicio al que le hablamos.
 * url:  donde esta.
 *
 * Las anotaciones son las mismas que en un controller, pero leidas al reves:
 * en un controller dicen "atiendo este pedido"; aca dicen "mando este pedido".
 */
@FeignClient(name = "servicio-productos", url = "http://localhost:8082")
public interface CuentasClient {

    /** GET /cuentas
     * Lista todas las cuentas o filtra por número de cuenta.
     */
    @GetMapping("/cuentas")
    List<CuentaDTO> listar(@RequestParam(required = false) String numeroCuenta);

    /** GET /cuentas/clientes/{id}
     * Lista todas las cuentas de un cliente o filtra por número de cuenta.
     */
    @GetMapping("/cuentas/clientes/{id}")
    List<CuentaDTO> listarTodos(@PathVariable Long id,
                                @RequestParam(required = false) String numeroCuenta);

    /** GET /cuentas/clientes/{id}/{cuentaId}
     * Obtiene una cuenta específica de un cliente.
     */
    @GetMapping("/cuentas/clientes/{id}/{cuentaId}")
    CuentaDTO buscarPorId(@PathVariable("id") Long id,
                          @PathVariable("cuentaId") Long cuentaId);

    /** POST /cuentas
     * Crea una cuenta.
     */
    @PostMapping("/cuentas")
    CuentaResponse crear(@RequestBody CuentaRequest request);

    /** PUT /cuentas/clientes/{id}/{cuentaId}
     * Actualiza una cuenta existente.
     */
    @PutMapping("/cuentas/clientes/{id}/{cuentaId}")
    CuentaResponse actualizar(@PathVariable("id") Long id,
                              @PathVariable("cuentaId") Long cuentaId,
                              @RequestBody CuentaUpdateRequest datosNuevos);

//    // GET http://localhost:8082/cuentas
//    @GetMapping("/cuentas")
//    List<CuentaDTO> listar(
//            @RequestParam(value = "clienteId", required = false) Long clienteId,
//            @RequestParam(value = "numeroCuenta", required = false) String numeroCuenta
//    );
//
////    // POST http://localhost:8082/cuentas/1/acreditaciones?importe=15000
////    @PostMapping("/cuentas/{id}/acreditaciones")
////    CuentaDTO acreditar(@PathVariable Long id, @RequestParam("importe") double importe);
//
//    // GET http://localhost:8082/cuentas?numeroCuenta=000000000042
//    @GetMapping("/cuentas")
//    List<CuentaDTO> buscarPorNumeroCuenta(@RequestParam("numeroCuenta") String numeroCuenta);
//
//    // GET http://localhost:8082/cuentas/1
//    @GetMapping("/cuentas/{id}")
//    CuentaDTO obtener(@PathVariable Long id);
//
//    // GET /cuentas/{id}/saldo
//    @GetMapping("/cuentas/{id}/saldo")
//    BigDecimal consultarSaldo(@PathVariable Long id);
//
//    // POST http://localhost:8082/cuentas/1/acreditaciones?importe=15000
//    @PostMapping("/cuentas/{id}/acreditaciones")
//    CuentaDTO acreditar( @PathVariable Long id, @RequestBody OperacionCuentaRequest request);
//
//    // POST /cuentas/{id}/retiros
//    @PostMapping("/cuentas/{id}/retiros")
//    CuentaDTO retirar( @PathVariable Long id, @RequestBody OperacionCuentaRequest request);
}
