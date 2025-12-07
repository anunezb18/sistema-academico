package com.udistrital.awuis.sistema_academico.controller;

import com.udistrital.awuis.sistema_academico.model.Usuario;
import com.udistrital.awuis.sistema_academico.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/correo")
public class CorreoRestController {

    @Autowired
    private EmailService emailService;

    /**
     * Endpoint para enviar correos desde el frontend
     */
    @PostMapping("/enviar")
    public ResponseEntity<Map<String, Object>> enviarCorreo(
            @SessionAttribute(value = "usuario", required = false) Usuario usuario,
            @RequestBody Map<String, Object> payload) {

        Map<String, Object> response = new HashMap<>();

        if (usuario == null) {
            response.put("success", false);
            response.put("message", "Usuario no autenticado");
            return ResponseEntity.status(401).body(response);
        }

        try {
            // Extraer datos del payload
            String[] destinatarios = ((String) payload.get("destinatarios")).split(",");
            String asunto = (String) payload.get("asunto");
            String mensaje = (String) payload.get("mensaje");

            int enviados = 0;
            int fallidos = 0;

            // Enviar correo a cada destinatario
            for (String destinatario : destinatarios) {
                destinatario = destinatario.trim();
                if (!destinatario.isEmpty()) {
                    // Crear el cuerpo del correo
                    String cuerpoCompleto = String.format(
                            "Remitente: %s%n%n%s%n%n---%nEste correo fue enviado desde el Sistema Académico AWUIS",
                            usuario.getCorreo(),
                            mensaje
                    );

                    boolean exito = emailService.enviarCorreo(destinatario, asunto, cuerpoCompleto);
                    if (exito) {
                        enviados++;
                    } else {
                        fallidos++;
                    }
                }
            }

            response.put("success", true);
            response.put("enviados", enviados);
            response.put("fallidos", fallidos);
            response.put("message", String.format("Correo enviado exitosamente a %d destinatario(s)", enviados));

            if (fallidos > 0) {
                response.put("warning", String.format("No se pudo enviar a %d destinatario(s)", fallidos));
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al enviar el correo: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(response);
        }
    }
}

