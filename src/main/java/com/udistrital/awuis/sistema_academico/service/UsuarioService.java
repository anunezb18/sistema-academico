package com.udistrital.awuis.sistema_academico.service;

import com.udistrital.awuis.sistema_academico.model.Directivo;
import com.udistrital.awuis.sistema_academico.model.Profesor;
import com.udistrital.awuis.sistema_academico.model.Rol;
import com.udistrital.awuis.sistema_academico.model.TokenUsuario;
import com.udistrital.awuis.sistema_academico.model.Usuario;
import com.udistrital.awuis.sistema_academico.repositories.DirectivoMapper;
import com.udistrital.awuis.sistema_academico.repositories.ProfesorMapper;
import com.udistrital.awuis.sistema_academico.repositories.RolMapper;
import com.udistrital.awuis.sistema_academico.repositories.TokenUsuarioMapper;
import com.udistrital.awuis.sistema_academico.repositories.UsuarioMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private TokenUsuarioMapper tokenUsuarioMapper;

    @Autowired
    private RolMapper rolMapper;

    @Autowired
    private ProfesorMapper profesorMapper;

    @Autowired
    private DirectivoMapper directivoMapper;

    @Autowired
    private EmailService emailService;

    /**
     * Crea un nuevo usuario con rol específico.
     * Usa COMPOSICIÓN: Primero crea Usuario, luego crea Profesor/Directivo con referencia al Usuario.
     */
    @Transactional
    public Usuario crearUsuario(String correo, String contrasena, int idRol, String nombre) {
        System.out.println("=== Iniciando creación de usuario ===");
        System.out.println("Correo: " + correo + ", Rol: " + idRol + ", Nombre: " + nombre);

        // Verificar si el correo ya existe
        if (usuarioMapper.findByCorreo(correo).isPresent()) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }

        // Obtener el rol
        Rol rol = rolMapper.findById(idRol)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado con ID: " + idRol));

        // Crear token de sesión
        TokenUsuario token = new TokenUsuario();
        token.generarToken();
        token.setExpiracion(LocalDateTime.now().plusHours(24)); // Token válido por 24 horas
        token.setRol(rol); // Asignar el rol al token
        token = tokenUsuarioMapper.save(token); // Guardar y obtener con ID generado
        System.out.println("Token creado con ID: " + token.getIdToken());

        Usuario usuario;

        // Primero crear usuario base
        usuario = new Usuario();
        usuario.setCorreo(correo);
        usuario.setContrasena(contrasena);
        usuario.setToken(token);
        usuario = usuarioMapper.save(usuario);
        System.out.println("Usuario base creado con ID: " + usuario.getIdUsuario());

        // Crear entidad específica según el rol (COMPOSICIÓN)
        try {
            switch (idRol) {
                case 1: // Directivo (idRol en BD)
                case 3: // Directivo (alternativo)
                    System.out.println("Creando Directivo (COMPOSICIÓN con Usuario)...");
                    Directivo directivo = new Directivo();
                    directivo.setIdUsuario(usuario.getIdUsuario());
                    directivo.setNombre(nombre);
                    directivo = directivoMapper.save(directivo);
                    System.out.println("Directivo creado - idDirectivo: " + directivo.getIdDirectivo() + ", idUsuario: " + directivo.getIdUsuario());
                    break;

                case 2: // Profesor
                    System.out.println("Creando Profesor (COMPOSICIÓN con Usuario)...");
                    Profesor profesor = new Profesor();
                    profesor.setIdUsuario(usuario.getIdUsuario());
                    profesor.setNombre(nombre);
                    profesor = profesorMapper.save(profesor);
                    System.out.println("Profesor creado - idProfesor: " + profesor.getIdProfesor() + ", idUsuario: " + profesor.getIdUsuario());
                    break;


                case 4: // Estudiante (se crea cuando se acepta un aspirante)
                    System.out.println("Usuario genérico - Estudiante se creará desde aspirante");
                    break;

                default: // Otros roles (admin, etc.)
                    System.out.println("Usuario genérico creado");
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error al crear usuario: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al crear " + rol.getNombre() + ": " + e.getMessage(), e);
        }

        System.out.println("=== Usuario creado exitosamente ===");
        return usuario;
    }

    /**
     * Crea un usuario estudiante desde un aspirante aceptado
     */
    @Transactional
    public Usuario crearEstudianteDesdeAspirante(String correo, String nombreCompleto) {
        // Generar contraseña temporal aleatoria
        String contrasenaTemporal = generarContrasenaTemporal();

        // Crear usuario con rol de Estudiante (idRol = 4) - no requiere nombre adicional
        Usuario usuario = crearUsuario(correo, contrasenaTemporal, 4, nombreCompleto);

        // Enviar correo con credenciales
        try {
            String asunto = "Bienvenido a AWUIS - Credenciales de Acceso";
            String mensaje = String.format(
                "Hola %s,\n\n" +
                "¡Felicitaciones! Has sido aceptado como estudiante en AWUIS.\n\n" +
                "Tus credenciales de acceso son:\n" +
                "Correo: %s\n" +
                "Contraseña temporal: %s\n\n" +
                "Por favor, cambia tu contraseña al iniciar sesión por primera vez.\n\n" +
                "Saludos,\n" +
                "Sistema Académico AWUIS",
                nombreCompleto, correo, contrasenaTemporal
            );

            emailService.enviarCorreo(correo, asunto, mensaje);
        } catch (Exception e) {
            System.err.println("Error al enviar correo de bienvenida: " + e.getMessage());
        }

        return usuario;
    }

    /**
     * Valida credenciales de inicio de sesión
     */
    public Usuario validarCredenciales(String correo, String contrasena) {
        Usuario usuario = usuarioMapper.findByCorreo(correo).orElse(null);

        if (usuario == null) {
            return null;
        }

        // TODO: Comparar con hash de contraseña
        if (!usuario.getContrasena().equals(contrasena)) {
            return null;
        }

        // Verificar que el token sea válido
        if (usuario.getToken() != null && !usuario.getToken().esValido()) {
            // Regenerar token si expiró
            TokenUsuario nuevoToken = usuario.getToken();
            nuevoToken.generarToken();
            tokenUsuarioMapper.save(nuevoToken);
        }

        return usuario;
    }

    /**
     * Cierra sesión invalidando el token
     */
    @Transactional
    public void cerrarSesion(int idUsuario) {
        Usuario usuario = usuarioMapper.findById(idUsuario).orElse(null);
        if (usuario != null && usuario.getToken() != null) {
            tokenUsuarioMapper.invalidar(usuario.getToken().getIdToken());
        }
    }

    /**
     * Inhabilita una cuenta de usuario
     */
    @Transactional
    public void inhabilitarCuenta(int idUsuario) {
        Usuario usuario = usuarioMapper.findById(idUsuario).orElse(null);
        if (usuario != null && usuario.getToken() != null) {
            tokenUsuarioMapper.invalidar(usuario.getToken().getIdToken());
        }
    }

    /**
     * Genera una contraseña temporal aleatoria
     */
    private String generarContrasenaTemporal() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(12);

        for (int i = 0; i < 12; i++) {
            int index = random.nextInt(caracteres.length());
            sb.append(caracteres.charAt(index));
        }

        return sb.toString();
    }

    public Usuario obtenerPorCorreo(String correo) {
        return usuarioMapper.findByCorreo(correo).orElse(null);
    }

    public Usuario obtenerPorId(int idUsuario) {
        return usuarioMapper.findById(idUsuario).orElse(null);
    }
}

