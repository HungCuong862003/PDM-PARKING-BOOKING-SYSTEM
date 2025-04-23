package main.java.com.parkeasy.service;

import main.java.com.parkeasy.model.Payment;
import main.java.com.parkeasy.repository.PaymentRepository;

import java.util.List;

public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    // insert a new payment
    public void insertPayment(Payment payment) {
        paymentRepository.insertPayment(payment);
    }

    // get a payment by its ID
    public Payment getPaymentById(int paymentID) {
        return paymentRepository.getPaymentById(paymentID);
    }

    // get all payments by reservation ID
    public List<Payment> getPaymentsByReservationId(int reservationID) {
        return paymentRepository.getPaymentsByReservationId(reservationID);
    }

    // get all payments
    public List<Payment> getAllPayments() {
        return paymentRepository.getAllPayments();
    }

    // update a payment
    public void updatePayment(Payment payment) {
        paymentRepository.updatePayment(payment);
    }

    // delete a payment
    public void deletePayment(int paymentID) {
        paymentRepository.deletePaymentById(paymentID);
    }

    // delete all payments by reservation ID
    public void deletePaymentsByReservationId(int reservationID) {
        paymentRepository.deletePaymentsByReservationId(reservationID);
    }
}