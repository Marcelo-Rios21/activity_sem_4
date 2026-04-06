package com.duoc.backend.invoice;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;

import com.duoc.backend.appointment.Appointment;
import com.duoc.backend.appointment.AppointmentRepository;
import com.duoc.backend.care.Care;
import com.duoc.backend.care.CareRepository;
import com.duoc.backend.medication.Medication;
import com.duoc.backend.medication.MedicationRepository;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final MedicationRepository medicationRepository;
    private final CareRepository careRepository;
    private final AppointmentRepository appointmentRepository;

    public InvoiceService(
            InvoiceRepository invoiceRepository,
            MedicationRepository medicationRepository,
            CareRepository careRepository,
            AppointmentRepository appointmentRepository) {
        this.invoiceRepository = invoiceRepository;
        this.medicationRepository = medicationRepository;
        this.careRepository = careRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public Iterable<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id).orElse(null);
    }

    public Invoice saveInvoice(Invoice invoice) {
        if (invoice.getAppointment() == null || invoice.getAppointment().getId() == null) {
            throw new IllegalArgumentException("La factura debe estar asociada a una visita válida.");
        }

        Appointment appointment = appointmentRepository.findById(invoice.getAppointment().getId())
                .orElseThrow(() -> new IllegalArgumentException("La visita no existe en la base de datos."));

        if (appointment.getPatient() == null) {
            throw new IllegalArgumentException("La visita no tiene un paciente asociado.");
        }

        List<Medication> requestedMedications = invoice.getMedications() != null ? invoice.getMedications() : List.of();
        List<Care> requestedCares = invoice.getCares() != null ? invoice.getCares() : List.of();

        List<Medication> validMedications = StreamSupport.stream(
                medicationRepository.findAllById(
                        requestedMedications.stream().map(Medication::getId).collect(Collectors.toList())
                ).spliterator(), false
        ).collect(Collectors.toList());

        if (validMedications.size() != requestedMedications.size()) {
            throw new IllegalArgumentException("Algunos medicamentos no existen en la base de datos.");
        }

        List<Care> validCares = StreamSupport.stream(
                careRepository.findAllById(
                        requestedCares.stream().map(Care::getId).collect(Collectors.toList())
                ).spliterator(), false
        ).collect(Collectors.toList());

        if (validCares.size() != requestedCares.size()) {
            throw new IllegalArgumentException("Algunos servicios no existen en la base de datos.");
        }

        double totalCareCost = validCares.stream()
                .mapToDouble(Care::getCost)
                .sum();

        double totalMedicationCost = validMedications.stream()
                .mapToDouble(Medication::getCost)
                .sum();

        double additionalCharges = invoice.getAdditionalCharges() != null ? invoice.getAdditionalCharges() : 0.0;

        invoice.setAppointment(appointment);
        invoice.setPatientName(appointment.getPatient().getName());
        invoice.setDate(appointment.getDate());
        invoice.setTime(appointment.getTime());
        invoice.setCares(validCares);
        invoice.setMedications(validMedications);
        invoice.setAdditionalCharges(additionalCharges);
        invoice.setTotalCost(totalCareCost + totalMedicationCost + additionalCharges);

        return invoiceRepository.save(invoice);
    }

    public void deleteInvoice(Long id) {
        invoiceRepository.deleteById(id);
    }
}