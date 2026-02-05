package com.example.SlotlyV2.feature.booking_form;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.SlotlyV2.common.exception.booking_form.InvalidFormResponseException;
import com.example.SlotlyV2.feature.booking_form.dto.FieldAnswerDTO;
import com.example.SlotlyV2.feature.booking_form.enums.FieldType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookingFormValidator {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9\\s\\-()]{7,20}$");

    public void validateAnswers(List<FormQuestion> fields, List<FieldAnswerDTO> answers) {
        validateRequiredQuestions(fields, answers);
        validateQuestionsExist(fields, answers);
        validateFieldTypes(fields, answers);
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

    private void validateFieldTypes(List<FormQuestion> fields, List<FieldAnswerDTO> answers) {
        Map<UUID, FormQuestion> fieldMap = fields.stream()
                .collect(Collectors.toMap(FormQuestion::getId, f -> f));

        for (FieldAnswerDTO answer : answers) {
            FormQuestion field = fieldMap.get(answer.getFieldId());
            if (field == null)
                continue;

            String response = answer.getFieldResponse();
            if (response == null || response.trim().isEmpty())
                continue;

            if (field.getFieldType() == FieldType.PHONE) {
                if (!PHONE_PATTERN.matcher(response.trim()).matches()) {
                    throw new InvalidFormResponseException(
                            "Invalid phone number format for field '" + field.getLabel() + "'");
                }
            }
        }
    }

    private FieldAnswerDTO findAnswerForField(List<FieldAnswerDTO> answers, UUID fieldId) {
        return answers.stream()
                .filter(a -> a.getFieldId().equals(fieldId))
                .findFirst()
                .orElse(null);
    }
}
