package com.clinicai.encounter.domain.port;
import com.clinicai.encounter.domain.model.Slot;
import java.util.List;
public interface SchedulePort {
  List<Slot> setSchedule(String clinicianId, List<Slot> slotsUtc);
  List<Slot> getSchedule(String clinicianId);
}
