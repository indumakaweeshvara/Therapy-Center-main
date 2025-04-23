package org.example.bo.custom.impl;

import org.example.bo.custom.TherapyDetailBO;
import org.example.dao.DAOFactory;
import org.example.dao.DAOTypes;
import org.example.dao.custom.TherapistDAO;
import org.example.dao.custom.TherapyDetailDAO;
import org.example.dao.custom.TherapyProgramDAO;
import org.example.dto.TherapyDetailDto;
import org.example.entity.TherapyDetail;
import org.example.entity.TherapyDetailId;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;


public class TherapyDetailBOImpl implements TherapyDetailBO {
    TherapyDetailDAO therapyDetailDAO = DAOFactory.getInstance().getDAO(DAOTypes.THERAPY_DETAIL);
    TherapistDAO therapistDAO = DAOFactory.getInstance().getDAO(DAOTypes.THERAPIST);
    TherapyProgramDAO therapyProgramDAO = DAOFactory.getInstance().getDAO(DAOTypes.PROGRAM);

    @Override
    public boolean save(TherapyDetailDto therapyDetailDto) throws Exception {
        // Create the composite key
        TherapyDetailId detailId = new TherapyDetailId(
                therapyDetailDto.getTherapistId(),
                therapyDetailDto.getProgramId()
        );

        // Create the entity but don't set the references yet
        TherapyDetail therapyDetail = new TherapyDetail();
        therapyDetail.setTherapyDetailId(detailId);
        therapyDetail.setNote(therapyDetailDto.getNote());

        // Let the DAO handle the relationships
        return therapyDetailDAO.saveWithReferences(
                therapyDetail,
                therapyDetailDto.getTherapistId(),
                therapyDetailDto.getProgramId()
        );
    }

    @Override
    public List<TherapyDetailDto> getAll() throws Exception {
        List<TherapyDetail> details = therapyDetailDAO.getAll();
        return convertToDto(details);
    }

    @Override
    public boolean update(TherapyDetailDto therapyDetailDto) throws SQLException {
        TherapyDetailId detailId = new TherapyDetailId(
                therapyDetailDto.getTherapistId(),
                therapyDetailDto.getProgramId()
        );

        TherapyDetail therapyDetail = new TherapyDetail();
        therapyDetail.setTherapyDetailId(detailId);
        therapyDetail.setNote(therapyDetailDto.getNote());

        return therapyDetailDAO.update(therapyDetail);
    }

    @Override
    public boolean deleteByPK(int therapistId, String programId) throws SQLException {
        TherapyDetailId detailId = new TherapyDetailId(therapistId, programId);
        return therapyDetailDAO.deleteByPK(detailId);
    }

    private List<TherapyDetailDto> convertToDto(List<TherapyDetail> details) {
        return details.stream()
                .map(detail -> new TherapyDetailDto(
                        detail.getTherapist().getId(),
                        detail.getTherapist().getName(),
                        detail.getProgram().getProgramId(),
                        detail.getProgram().getProgramName(), // Assuming TherapyProgram has a name field
                        detail.getNote()
                ))
                .collect(Collectors.toList());
    }
}
