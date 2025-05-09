package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.models.*;
import com.cafeteria.cafeteria_plugin.repositories.CatalogEntryRepository;
import com.cafeteria.cafeteria_plugin.repositories.CatalogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.Class;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CatalogService {
    @Autowired
    private CatalogRepository catalogRepository;

    @Autowired
    private CatalogEntryRepository catalogEntryRepository;

    @Autowired
    private ClassService classService;

    public Catalog createCatalogForClass(com.cafeteria.cafeteria_plugin.models.Class studentClass) {
        Catalog catalog = new Catalog();
        catalog.setStudentClass(studentClass);
        return catalogRepository.save(catalog);
    }

    public List<CatalogEntry> getCatalogEntriesForClass(Long classId) {
        Optional<Catalog> catalog = catalogRepository.findByStudentClass_Id(classId);
        if (catalog.isPresent()) {
            return catalogEntryRepository.findByCatalog_Id(catalog.get().getId());
        }
        return new ArrayList<>();
    }

    public List<CatalogEntry> getStudentEntries(Long studentId) {
        return catalogEntryRepository.findByStudent_Id(studentId);
    }

    public CatalogEntry addGradeEntry(Student student, String subject, Double value) {
        // Găsim catalogul pentru clasa studentului
        Optional<Catalog> catalogOpt = catalogRepository.findByStudentClass_Id(student.getStudentClass().getId());
        if (!catalogOpt.isPresent()) {
            throw new RuntimeException("Catalogul nu a fost găsit pentru clasa: " + student.getStudentClass().getName());
        }

        CatalogEntry entry = new CatalogEntry();
        entry.setCatalog(catalogOpt.get());
        entry.setStudent(student);
        entry.setSubject(subject);
        entry.setType(EntryType.GRADE);
        entry.setGradeValue(value);
        entry.setDate(LocalDateTime.now());

        return catalogEntryRepository.save(entry);
    }

    public CatalogEntry addAbsenceEntry(Student student, String subject, Boolean justified, Long absenceId) {
        Optional<Catalog> catalogOpt = catalogRepository.findByStudentClass_Id(student.getStudentClass().getId());
        if (!catalogOpt.isPresent()) {
            throw new RuntimeException("Catalogul nu a fost găsit pentru clasa: " + student.getStudentClass().getName());
        }

        CatalogEntry entry = new CatalogEntry();
        entry.setCatalog(catalogOpt.get());
        entry.setStudent(student);
        entry.setSubject(subject);
        entry.setType(EntryType.ABSENCE);
        entry.setJustified(justified);
        entry.setDate(LocalDateTime.now());
        entry.setAbsenceId(absenceId);

        return catalogEntryRepository.save(entry);
    }

    public CatalogEntry updateAbsenceJustification(Long absenceId, Boolean justified) {
        Optional<CatalogEntry> entryOpt = catalogEntryRepository.findByAbsenceId(absenceId);

        if (entryOpt.isPresent()) {
            CatalogEntry entry = entryOpt.get();
            entry.setJustified(justified);
            return catalogEntryRepository.save(entry);
        } else {
            throw new RuntimeException("Nu s-a găsit o intrare de catalog pentru absența cu ID-ul: " + absenceId);
        }
    }
}