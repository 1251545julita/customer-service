package ar.edu.utn.diplomatura.customerservice.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import ar.edu.utn.diplomatura.customerservice.Constants;
import ar.edu.utn.diplomatura.customerservice.client.CuentasClient;
import ar.edu.utn.diplomatura.customerservice.componentes.ValidadorDeCuit;
import ar.edu.utn.diplomatura.customerservice.componentes.ValidadorDeDni;
import ar.edu.utn.diplomatura.customerservice.dto.CuentaDTO;
import ar.edu.utn.diplomatura.customerservice.dto.CuentaRequest;
import ar.edu.utn.diplomatura.customerservice.dto.CuentaResponse;
import ar.edu.utn.diplomatura.customerservice.dto.CuentaUpdateRequest;
import ar.edu.utn.diplomatura.customerservice.entity.PersonaFisica;
import ar.edu.utn.diplomatura.customerservice.entity.PersonaJuridica;
import ar.edu.utn.diplomatura.customerservice.exception.*;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import ar.edu.utn.diplomatura.customerservice.entity.Cliente;
import ar.edu.utn.diplomatura.customerservice.repository.ClienteRepository;

/**
 * Reglas del negocio. Esta entre el controlador y el repositorio.
 *
 * Cuando algo no se puede hacer (el cliente no existe, el importe es invalido)
 * estos metodos devuelven null.
 */
@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ValidadorDeCuit validadorDeCuit;
    private final ValidadorDeDni validadorDeDni;
    private final CuentasClient cuentasClient;

    // Inyeccion por constructor: Spring busca esos dos beans en el contenedor y los pasa como argumentos.
    public ClienteService(ClienteRepository clienteRepository, ValidadorDeDni validadorDeDni, ValidadorDeCuit validadorDeCuit, CuentasClient cuentasClient) {
        this.clienteRepository = clienteRepository;
        this.validadorDeDni = validadorDeDni;
        this.validadorDeCuit = validadorDeCuit;
        this.cuentasClient = cuentasClient;
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    /** Devuelve el cliente encontrado, o null si ese id no existe. */
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Cliente obtenerOFallar(Long id){
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNoEncontradoException(id));
    }

    public List<Cliente> buscarPorNumeroDocumento(String numeroDocumento) {
        return clienteRepository.findByNumeroDocumentoContainingIgnoreCase(numeroDocumento);
    }

    private void validarDocumento(Cliente unCliente) {

        if (unCliente instanceof PersonaFisica unaPersonaFisica) {
            unCliente.setTipoDocumento(Constants.TIPO_DOCUMENTO_DNI);
            if (!validadorDeDni.esValido(unCliente.getNumeroDocumento())) {
                throw new DniInvalidoException(unCliente.getNumeroDocumento());
            }
            unaPersonaFisica.setNumeroDocumento( validadorDeDni.normalizar( unaPersonaFisica.getNumeroDocumento()));

        } else if (unCliente instanceof PersonaJuridica unaPersonaJuridica) {
            unCliente.setTipoDocumento(Constants.TIPO_DOCUMENTO_CUIT);
            if (!validadorDeCuit.esValido(unaPersonaJuridica.getCuit())) {
                throw new CuitInvalidoException(unaPersonaJuridica.getCuit());
            }
            unaPersonaJuridica.setCuit( validadorDeCuit.normalizar(unaPersonaJuridica.getCuit()));
        }
    }

    /** Crea un cliente. El número de documento se guarda siempre sin puntos y sin guiones. */
    public Cliente crear(Cliente unCliente) {
        unCliente.setNombre(unCliente.getNombre());
        validarDocumento(unCliente);
        unCliente.setDomicilio(unCliente.getDomicilio());
        unCliente.setTelefono(unCliente.getTelefono());
        if (clienteRepository.existsByCorreoElectronico(unCliente.getCorreoElectronico())) {
            throw new EmailDuplicadoException(unCliente.getCorreoElectronico());
        }
        unCliente.setFechaDeAlta(LocalDate.now());
        unCliente.setActivo(true);
        unCliente.setSucursal(unCliente.getSucursal());
        return clienteRepository.save(unCliente);
    }


    /** Actualiza nombre, apellido, email y cuentas asociadas. Devuelve null si el id no existe. */
    public Cliente actualizar(Long id, Cliente datosNuevos) {
        Cliente clienteExistente = obtenerOFallar(id);
        if (!Hibernate.getClass(clienteExistente).equals(datosNuevos.getClass())) {
            throw new TipoClienteNoModificableException();
        }

        clienteExistente.setNombre(datosNuevos.getNombre());
        clienteExistente.setTipoDocumento(datosNuevos.getTipoDocumento());
        clienteExistente.setNumeroDocumento(datosNuevos.getNumeroDocumento());
        clienteExistente.setDomicilio(datosNuevos.getDomicilio());
        clienteExistente.setTelefono(datosNuevos.getTelefono());
        clienteExistente.setCorreoElectronico(datosNuevos.getCorreoElectronico());
        clienteExistente.setSucursal(datosNuevos.getSucursal());
        clienteExistente.setActivo(datosNuevos.getActivo());

        if (clienteExistente instanceof PersonaFisica unaPersonaFisica && datosNuevos instanceof PersonaFisica nuevos) {

            unaPersonaFisica.setApellido(nuevos.getApellido());
            unaPersonaFisica.setFechaDeNacimiento(nuevos.getFechaDeNacimiento());
            unaPersonaFisica.setProfesion(nuevos.getProfesion());
            unaPersonaFisica.setIngresosDeclarados(nuevos.getIngresosDeclarados());
            unaPersonaFisica.setEstadoCivil(nuevos.getEstadoCivil());
            unaPersonaFisica.setNacionalidad(nuevos.getNacionalidad());
            unaPersonaFisica.setSituacionLaboral(nuevos.getSituacionLaboral());
            unaPersonaFisica.setDni(nuevos.getDni());

        } else if (clienteExistente instanceof PersonaJuridica unaPersonaJuridica && datosNuevos instanceof PersonaJuridica nuevos) {

            unaPersonaJuridica.setRazonSocial(nuevos.getRazonSocial());
            unaPersonaJuridica.setRubro(nuevos.getRubro());
            unaPersonaJuridica.setRepresentanteLegal(nuevos.getRepresentanteLegal());
            unaPersonaJuridica.setCondicionIva(nuevos.getCondicionIva());
            unaPersonaJuridica.setFechaConstitucion(nuevos.getFechaConstitucion());
            unaPersonaJuridica.setFacturacionAnual(nuevos.getFacturacionAnual());
            unaPersonaJuridica.setCuit(nuevos.getCuit());
        }

        return clienteRepository.save(clienteExistente);
    }

    public void eliminarCliente(Long id, String modo) {

        if (!modo.equals("fisico") && !modo.equals("logico")) {
            throw new ModoEliminacionInvalidoException(modo);
        }

        boolean seDioDeBaja = modo.equals("logico")
                ? desactivar(id)
                : eliminar(id);

        if (!seDioDeBaja) {
            throw new ClienteNoEncontradoException(id);
        }
    }

    /** Elimina un cliente. Si el id no existe, no hace nada. */
    public boolean eliminar(Long id) {
        if (!clienteRepository.existsById(id)) {
            return false;
        }
        clienteRepository.deleteById((id));
        return true;
    }

    public boolean desactivar(Long id){
        Cliente unCliente = buscarPorId(id).orElse(null);
        if (unCliente == null) {
            return false;
        }
        unCliente.setActivo(false);
        clienteRepository.save(unCliente);
        return true;
    }

    /** Manejo de las cuentas bancarias **/

    /**
     * Lista todas las cuentas asociadas a un cliente.
     */
    public List<CuentaDTO> listarCuentas(Long clienteId) {
        obtenerOFallar(clienteId); // 404 si el cliente no existe
        return cuentasClient.listarTodos(clienteId, null);
    }

    /**
     * Busca las cuentas de un cliente por número de cuenta.
     */
    public List<CuentaDTO> buscarCuentas(Long clienteId, String numeroCuenta) {
        return cuentasClient.listarTodos(clienteId, numeroCuenta);
    }

    /**
     * Obtiene una cuenta específica de un cliente.
     */
    public CuentaDTO obtenerCuenta(Long clienteId, Long cuentaId) {
        return cuentasClient.buscarPorId(clienteId, cuentaId);
    }

    /**
     * Crea una cuenta para un cliente.
     */
    public CuentaResponse crearCuenta(CuentaRequest request) {
        return cuentasClient.crear(request);
    }

    /**
     * Actualiza una cuenta existente de un cliente.
     */
    public CuentaResponse actualizarCuenta(Long clienteId, Long cuentaId, CuentaUpdateRequest datosNuevos) {
        return cuentasClient.actualizar(
                clienteId,
                cuentaId,
                datosNuevos
        );
    }
}