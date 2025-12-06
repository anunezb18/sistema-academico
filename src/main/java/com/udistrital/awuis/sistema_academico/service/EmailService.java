package com.udistrital.awuis.sistema_academico.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.mail.from:sistema-academico@udistrital.edu.co}")
    private String fromEmail;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Método genérico para enviar correos electrónicos
     *
     * @param destinatario Correo electrónico del destinatario
     * @param asunto Asunto del correo
     * @param cuerpo Cuerpo del mensaje
     * @return true si se envió exitosamente, false si hubo error
     */
    public boolean enviarCorreo(String destinatario, String asunto, String cuerpo) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom(fromEmail);
            mensaje.setTo(destinatario);
            mensaje.setSubject(asunto);
            mensaje.setText(cuerpo);

            mailSender.send(mensaje);

            System.out.println("✓ Correo enviado exitosamente a: " + destinatario);
            return true;

        } catch (MailException e) {
            System.err.println("✗ Error al enviar correo a " + destinatario + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("✗ Error inesperado al enviar correo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Envia notificacion de entrevista programada al correo del responsable del aspirante
     *
     * @param nombreAspirante Nombre completo del aspirante
     * @param correoResponsable Email del responsable (del formulario)
     * @param fechaHoraEntrevista Fecha y hora programada para la entrevista
     * @return true si se envio exitosamente, false si hubo error
     */
    public boolean enviarNotificacionEntrevista(String nombreAspirante, String correoResponsable,
                                                 LocalDateTime fechaHoraEntrevista) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom(fromEmail);
            mensaje.setTo(correoResponsable);
            mensaje.setSubject("Entrevista Programada - Sistema Academico Infantil");
            mensaje.setText(generarCuerpoCorreo(nombreAspirante, fechaHoraEntrevista));

            mailSender.send(mensaje);

            System.out.println("✓ Correo enviado exitosamente a: " + correoResponsable);
            return true;

        } catch (MailException e) {
            System.err.println("✗ Error al enviar correo a " + correoResponsable + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("✗ Error inesperado al enviar correo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Genera el cuerpo del correo personalizado con los datos del aspirante
     */
    private String generarCuerpoCorreo(String nombreAspirante, LocalDateTime fechaHoraEntrevista) {
        String fechaFormateada = fechaHoraEntrevista.format(FORMATTER);

        return String.format(
            "Estimado/a responsable,\n\n" +
            "Nos complace informarle que el aspirante %s ha sido ACEPTADO " +
            "para continuar con el proceso de admision.\n\n" +
            "Se ha programado una entrevista para el dia %s.\n\n" +
            "Por favor, confirme su asistencia respondiendo a este correo o " +
            "comunicandose con la institucion.\n\n" +
            "Datos de la entrevista:\n" +
            "- Aspirante: %s\n" +
            "- Fecha y hora: %s\n\n" +
            "Atentamente,\n" +
            "Sistema Academico Infantil\n" +
            "Universidad Distrital Francisco Jose de Caldas",
            nombreAspirante, fechaFormateada, nombreAspirante, fechaFormateada
        );
    }

    /**
     * Envia notificacion de aceptacion final como estudiante
     */
    public boolean enviarNotificacionAceptacion(String nombreEstudiante, String correoResponsable) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom(fromEmail);
            mensaje.setTo(correoResponsable);
            mensaje.setSubject("¡Felicitaciones! Estudiante Aceptado");
            mensaje.setText(
                String.format(
                    "Estimado/a responsable,\n\n" +
                    "Nos complace informarle que %s ha sido aceptado/a oficialmente " +
                    "como estudiante de nuestra institucion.\n\n" +
                    "Pronto recibira informacion sobre el proceso de matricula y " +
                    "los siguientes pasos a seguir.\n\n" +
                    "Atentamente,\n" +
                    "Sistema Academico Infantil\n" +
                    "Universidad Distrital Francisco Jose de Caldas",
                    nombreEstudiante
                )
            );

            mailSender.send(mensaje);
            System.out.println("✓ Correo de aceptacion enviado exitosamente a: " + correoResponsable);
            return true;

        } catch (MailException e) {
            System.err.println("✗ Error al enviar correo de aceptacion: " + e.getMessage());
            return false;
        }
    }

    /**
     * Envia notificacion de rechazo al aspirante con la razon especificada
     *
     * @param nombreAspirante Nombre completo del aspirante
     * @param correoResponsable Email del responsable (del formulario)
     * @param razonRechazo Motivo por el cual fue rechazado
     * @return true si se envio exitosamente, false si hubo error
     */
    public boolean enviarNotificacionRechazo(String nombreAspirante, String correoResponsable,
                                              String razonRechazo) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom(fromEmail);
            mensaje.setTo(correoResponsable);
            mensaje.setSubject("Notificacion de Proceso de Admision - Sistema Academico Infantil");
            mensaje.setText(generarCuerpoCorreoRechazo(nombreAspirante, razonRechazo));

            mailSender.send(mensaje);

            System.out.println("✓ Correo de rechazo enviado exitosamente a: " + correoResponsable);
            return true;

        } catch (MailException e) {
            System.err.println("✗ Error al enviar correo de rechazo a " + correoResponsable + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("✗ Error inesperado al enviar correo de rechazo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Genera el cuerpo del correo de rechazo personalizado
     */
    private String generarCuerpoCorreoRechazo(String nombreAspirante, String razonRechazo) {
        return String.format(
            "Estimado/a responsable,\n\n" +
            "Le informamos que hemos finalizado el proceso de evaluacion del aspirante %s.\n\n" +
            "Lamentablemente, en esta ocasion no hemos podido continuar con el proceso de admision.\n\n" +
            "Razon:\n%s\n\n" +
            "Agradecemos su interes en nuestra institucion y le deseamos el mayor de los exitos " +
            "en su busqueda educativa.\n\n" +
            "Atentamente,\n" +
            "Sistema Academico Infantil\n" +
            "Universidad Distrital Francisco Jose de Caldas",
            nombreAspirante,
            razonRechazo != null && !razonRechazo.trim().isEmpty() ? razonRechazo : "No especificada"
        );
    }
}

