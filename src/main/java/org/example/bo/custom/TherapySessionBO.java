package org.example.bo.custom;

import org.example.bo.SuperBO;
import org.example.dto.TherapySessionDto;
import org.example.entity.TherapySessionId;

import java.util.List;

public interface TherapySessionBO extends SuperBO {
    boolean scheduleSession(TherapySessionDto therapySessionDTO, double paymentAmount);
    boolean isSessionConflict(int patientId, int therapistId, String date, String time);
    List<TherapySessionDto> getAllSessions();

    boolean ReScheduleSession(TherapySessionDto sessionDTO, double paymentAmount, TherapySessionId id);

    boolean isSessionConflictUpdate(int patientId, int therapistId, String date, String time, TherapySessionId id);
}
