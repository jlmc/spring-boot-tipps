package io.github.jlmc.springqualifiers.appointments.boundary;

import io.github.jlmc.springqualifiers.appointments.control.AppointmentsStorage;
import io.github.jlmc.springqualifiers.appointments.control.CreateAppointmentCommand;
import io.github.jlmc.springqualifiers.appointments.entity.Appointment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentsController {

    private final AppointmentsStorage appointmentsStorage;

    public AppointmentsController(AppointmentsStorage appointmentsStorage) {
        this.appointmentsStorage = appointmentsStorage;
    }

    @GetMapping
    public List<Appointment> getAppointments() {
        return appointmentsStorage.list();
    }

    @PostMapping
    public ResponseEntity<Appointment> create(@RequestBody Appointment appointment) {
        return ResponseEntity.ok(appointmentsStorage.add(new CreateAppointmentCommand(appointment.departmentId(), appointment.patientId())));
    }

}
