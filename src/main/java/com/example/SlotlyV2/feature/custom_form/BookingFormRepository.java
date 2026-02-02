package com.example.SlotlyV2.feature.custom_form;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingFormRepository extends JpaRepository<BookingForm, UUID> {

}
