package com.example.SlotlyV2.feature.booking_form;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.SlotlyV2.common.exception.booking_form.AnswerAlreadyExistsException;
import com.example.SlotlyV2.common.exception.booking_form.InvalidFormResponseException;
import com.example.SlotlyV2.feature.booking_form.dto.FieldAnswerDTO;
import com.example.SlotlyV2.feature.slot.Slot;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookingFormValidator {

    public void validateAnswers(Slot slot, List<FormQuestion> fields, List<FieldAnswerDTO> answers) {
        validateRequiredQuestions(fields, answers);
        validateQuestionsExist(fields, answers);
        validateAnswersExist(slot);
    }

    private void validateRequiredQuestions(List<FormQuestion> fields, List<FieldAnswerDTO> answers) {
        for (FormQuestion field : fields) {
            if (field.isRequired()) {
                FieldAnswerDTO answer = findAnswerForField(answers, field.getId());
                if (answer == null || answer.getFieldResponse() == null || answer.getFieldResponse().trim().isEmpty()) {
                    throw new InvalidFormResponseException("Required field '" + field.getLabel() + "' is missing");
                }
            }
        }
    }

    private void validateQuestionsExist(List<FormQuestion> fields, List<FieldAnswerDTO> answers) {
        Map<UUID, FormQuestion> fieldMap = fields.stream()
                .collect(Collectors.toMap(FormQuestion::getId, f -> f));

        for (FieldAnswerDTO answer : answers) {
            if (!fieldMap.containsKey(answer.getFieldId())) {
                throw new InvalidFormResponseException("Invalid field ID: " + answer.getFieldId());
            }
        }
    }

    public void validateAnswersExist(Slot slot) {
        if (slot.getFormAnswers() != null) {
            throw new AnswerAlreadyExistsException("Cant add answers to already booked slot");
        }
    }

    private FieldAnswerDTO findAnswerForField(List<FieldAnswerDTO> answers, UUID fieldId) {
        return answers.stream()
                .filter(a -> a.getFieldId().equals(fieldId))
                .findFirst()
                .orElse(null);
    }
}
