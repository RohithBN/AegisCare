package com.pm.appointmentservice.controller;

import com.pm.appointmentservice.dto.AppointmentRequestDTO;
import com.pm.appointmentservice.dto.AppointmentResponseDTO;
import com.pm.appointmentservice.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/create/{id}")
    public ResponseEntity<AppointmentResponseDTO> CreateAppointment(@PathVariable UUID id , @RequestBody AppointmentRequestDTO appointment){
        AppointmentResponseDTO appointmentResponseDTO = appointmentService.createAppointment(appointment);
        return ResponseEntity.ok().body(appointmentResponseDTO);
    }


}
