package com.example.SlotlyV2.feature.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.SlotlyV2.feature.user.User;

@Repository
public interface ScheduleRepository extends JpaRepository<User, UserSchedule> {

}
