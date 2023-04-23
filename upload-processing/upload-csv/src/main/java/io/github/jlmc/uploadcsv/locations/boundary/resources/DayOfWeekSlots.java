package io.github.jlmc.uploadcsv.locations.boundary.resources;

import java.time.DayOfWeek;
import java.util.Collection;

public record DayOfWeekSlots(DayOfWeek dayOfWeek, Collection<SlotResource> slots) {}
