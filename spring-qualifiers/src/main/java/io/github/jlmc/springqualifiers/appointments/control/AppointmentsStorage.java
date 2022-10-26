package io.github.jlmc.springqualifiers.appointments.control;

import io.github.jlmc.springqualifiers.appointments.entity.Appointment;
import io.github.jlmc.springqualifiers.reporting.boundary.Billing;
import io.github.jlmc.springqualifiers.reporting.boundary.Report;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.*;

@Component
public class AppointmentsStorage {

    private final Map<String, Appointment> map = new HashMap<>();

    @PostConstruct
    void initialize() {
        map.put("1",
                new Appointment(
                        "1",
                        "123",
                        "77",
                        LocalDate.parse("2022-11-01").atTime(LocalTime.MAX).toInstant(ZoneOffset.UTC)
                ));
    }

    @Report(operation = "read-appointments")
    public List<Appointment> list() {
        return map.values().stream().sorted(Comparator.comparing(Appointment::at)).toList();
    }

    @Billing(operation = "add-appointment")
    public Appointment add(CreateAppointmentCommand command) {
        long mostSignificantBits = UUID.randomUUID().getMostSignificantBits();

        var appointment = new Appointment("" + mostSignificantBits, command.departmentId(), command.patientId(), Instant.now());

        map.put(appointment.id(), appointment);

        return appointment;
    }

}
