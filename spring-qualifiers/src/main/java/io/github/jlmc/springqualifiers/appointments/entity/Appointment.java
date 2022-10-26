package io.github.jlmc.springqualifiers.appointments.entity;

import java.time.Instant;

//@formatter:off
public record Appointment(String id, String departmentId, String patientId, Instant at) {}
//@formatter:on
