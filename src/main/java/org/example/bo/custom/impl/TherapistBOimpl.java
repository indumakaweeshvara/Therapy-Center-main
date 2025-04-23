package org.example.bo.custom.impl;

import org.example.bo.custom.TherapistBO;
import org.example.dao.DAOFactory;
import org.example.dao.DAOTypes;
import org.example.dao.custom.QueryDAO;
import org.example.dao.custom.TherapistDAO;
import org.example.dto.TherapistDto;
import org.example.entity.Therapist;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class TherapistBOimpl implements TherapistBO {
    TherapistDAO therapistDAO = DAOFactory.getInstance().getDAO(DAOTypes.THERAPIST);
    QueryDAO queryDAO = DAOFactory.getInstance().getDAO(DAOTypes.QUERY);


    @Override
    public boolean save(TherapistDto therapistDto) throws Exception {
        Therapist therapist = new Therapist();
        therapist.setName(therapistDto.getName());
        therapist.setPhone(therapistDto.getPhone());
        therapist.setEmail(therapistDto.getEmail());
        therapist.setSpecialization(therapistDto.getSpecialization());
        therapist.setTherapySessions(new ArrayList<>());
        therapist.setTherapyDetails(new ArrayList<>());
        return therapistDAO.save(therapist);
    }

    @Override
    public boolean update(TherapistDto therapistDto) throws SQLException {
        Therapist therapist = new Therapist();
        therapist.setId(therapistDto.getId());
        therapist.setName(therapistDto.getName());
        therapist.setPhone(therapistDto.getPhone());
        therapist.setEmail(therapistDto.getEmail());
        therapist.setSpecialization(therapistDto.getSpecialization());
        // don't set the lists here as they should be handled by Hibernate
        // to avoid overwriting existing related data
        return therapistDAO.update(therapist);
    }

    @Override
    public List<TherapistDto> getAll() throws Exception {
        List<Therapist> therapists = therapistDAO.getAll();
        return therapists.stream()
                .map(therapist -> new TherapistDto(
                        therapist.getId(),
                        therapist.getName(),
                        therapist.getEmail(),
                        therapist.getPhone(),
                        therapist.getSpecialization()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteByPK(String therapistId) throws SQLException {
        return therapistDAO.deleteByPK(therapistId);
    }

    @Override
    public List<TherapistDto> getAllTherapistOptions(String patientId) throws Exception {
        List<Therapist> therapistEntities = queryDAO.getTherapistsByPatientId(patientId);

        // Convert to DTOs
        List<TherapistDto> dtoList = new ArrayList<>();
        for (Therapist t : therapistEntities) {
            dtoList.add(new TherapistDto(
                    t.getId(),
                    t.getName(),
                    t.getEmail(),
                    t.getPhone(),
                    t.getSpecialization()
            ));
        }

        return dtoList;
    }


}
