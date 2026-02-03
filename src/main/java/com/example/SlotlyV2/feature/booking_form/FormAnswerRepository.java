package com.example.SlotlyV2.feature.booking_form;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormAnswerRepository extends JpaRepository<FormAnswer, UUID> {
    List<FormAnswer> findBySlotId(Long slotId);
}
