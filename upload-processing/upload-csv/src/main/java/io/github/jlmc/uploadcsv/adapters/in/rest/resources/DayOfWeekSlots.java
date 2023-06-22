package io.github.jlmc.uploadcsv.adapters.in.rest.resources;

import java.time.DayOfWeek;
import java.util.Collection;

public record DayOfWeekSlots(DayOfWeek dayOfWeek, Collection<SlotResource> slots) {}
