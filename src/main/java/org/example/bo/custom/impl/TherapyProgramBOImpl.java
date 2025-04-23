package org.example.bo.custom.impl;

import org.example.bo.custom.TherapyProgramBO;
import org.example.dao.DAOFactory;
import org.example.dao.DAOTypes;
import org.example.dao.custom.RegistrationDAO;
import org.example.dao.custom.TherapyProgramDAO;
import org.example.dto.TherapistDto;
import org.example.dto.TherapyProgramDto;
import org.example.entity.Registration;
import org.example.entity.Therapist;
import org.example.entity.TherapyProgram;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TherapyProgramBOImpl implements TherapyProgramBO {
    TherapyProgramDAO therapyProgramDAO = DAOFactory.getInstance().getDAO(DAOTypes.PROGRAM);
    RegistrationDAO registrationDAO = DAOFactory.getInstance().getDAO(DAOTypes.REGISTRATION);
    @Override
    public boolean exists(String programId) throws SQLException {
        return therapyProgramDAO.exists(programId);
    }

    @Override
    public boolean save(TherapyProgramDto programDto) throws Exception {
        TherapyProgram program = new TherapyProgram();
        program.setProgramId(programDto.getProgramId());
        program.setProgramName(programDto.getProgramName());
        program.setDuration(programDto.getDuration());
        program.setProgramCost(programDto.getProgramCost());
        program.setProgramDescription(programDto.getProgramDescription());

        // Initialize empty collections for relationships
        program.setRegistrations(new ArrayList<>());
        program.setPayments(new ArrayList<>());
        program.setTherapyDetails(new ArrayList<>());

        return therapyProgramDAO.save(program);
    }

    @Override
    public List<TherapyProgramDto> getAll() throws Exception {
        List<TherapyProgram> programs = therapyProgramDAO.getAll();

        return programs.stream()
                .map(program -> new TherapyProgramDto(
                        program.getProgramId(),
                        program.getProgramName(),
                        program.getDuration(),
                        program.getProgramCost(),
                        program.getProgramDescription()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public boolean update(TherapyProgramDto programDto) throws SQLException {
        TherapyProgram program = new TherapyProgram();
        program.setProgramId(programDto.getProgramId());
        program.setProgramName(programDto.getProgramName());
        program.setDuration(programDto.getDuration());
        program.setProgramCost(programDto.getProgramCost());
        program.setProgramDescription(programDto.getProgramDescription());

        // We don't set collections here to preserve existing relationships

        return therapyProgramDAO.update(program);
    }

    @Override
    public boolean deleteByPK(String programId) throws SQLException {
        return therapyProgramDAO.deleteByPK(programId);
    }
    private String programId;
    private String programName;
    private String duration;
    private BigDecimal programCost;
    private String programDescription;

    @Override
    public TherapyProgramDto getProgram(String programId) throws SQLException {
        TherapyProgram program = therapyProgramDAO.getProgram(programId);
        if(program != null){
            return new TherapyProgramDto(program.getProgramId(), program.getProgramName(), program.getDuration(), program.getProgramCost(), program.getProgramDescription());
        }
        return null;
    }

    @Override
    public List<TherapyProgramDto> getAllProgramOptions(String patientId) {
        List<TherapyProgram> programsByPatientId = registrationDAO.getProgramsByPatientId(Integer.parseInt(patientId));

        // Convert to DTOs
        List<TherapyProgramDto> dtoList = new ArrayList<>();
        for (TherapyProgram p : programsByPatientId) {
            dtoList.add(new TherapyProgramDto(
                    p.getProgramId(),
                    p.getProgramName(),
                    p.getDuration(),
                    p.getProgramCost(),
                    p.getProgramDescription()

            ));
        }

        return dtoList;
    }
}


