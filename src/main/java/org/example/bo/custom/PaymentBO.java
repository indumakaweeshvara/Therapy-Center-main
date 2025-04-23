package org.example.bo.custom;

import org.example.bo.SuperBO;
import org.example.dto.PaymentDto;
import org.example.view.tdm.PaymentTm;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface PaymentBO extends SuperBO {
    PaymentDto getPaymentByPatientId(int patientId);
    BigDecimal findPaymentAmount(int patientId, String date, String time);
    String getNextPaymentId() throws SQLException;

    List<PaymentTm> getAllPayments() throws SQLException;

    PaymentDto getPaymentById(int paymentId);

    boolean savePayment(PaymentDto paymentDto);

    boolean deletePayment(int paymentId);

    boolean updatePayment(PaymentDto paymentDto);
}
