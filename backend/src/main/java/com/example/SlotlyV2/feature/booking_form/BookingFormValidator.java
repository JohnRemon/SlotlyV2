package com.example.SlotlyV2.feature.booking_form;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.SlotlyV2.common.exception.booking_form.InvalidFormResponseException;
import com.example.SlotlyV2.feature.booking_form.dto.BookingFormAnswerRequest;
import com.example.SlotlyV2.feature.booking_form.enums.FieldType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookingFormValidator {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9\\s\\-()]{7,20}$");

    public void validateAnswers(List<FormQuestion> fields, List<BookingFormAnswerRequest> answers) {
        Map<UUID, FormQuestion> fieldMap = fields.stream()
                .collect(Collectors.toMap(FormQuestion::getId, f -> f));

        validateNoUnknownFields(fieldMap, answers);
        validateRequiredFields(fields, answers);
        validateFieldFormats(fieldMap, answers);
    }

    // ── Private validators ────────────────────────────────────────────────────

    private void validateNoUnknownFields(Map<UUID, FormQuestion> fieldMap,
            List<BookingFormAnswerRequest> answers) {
        for (BookingFormAnswerRequest answer : answers) {
            if (!fieldMap.containsKey(answer.getFieldId())) {
                throw new InvalidFormResponseException("Unknown field id: " + answer.getFieldId());
            }
        }
    }

    private void validateRequiredFields(List<FormQuestion> fields, List<BookingFormAnswerRequest> answers) {
        Map<UUID, String> answerMap = answers.stream()
                .filter(a -> a.getFieldResponse() != null && !a.getFieldResponse().trim().isEmpty())
                .collect(Collectors.toMap(BookingFormAnswerRequest::getFieldId,
                        BookingFormAnswerRequest::getFieldResponse));

        for (FormQuestion field : fields) {
            if (field.isRequired() && !answerMap.containsKey(field.getId())) {
                throw new InvalidFormResponseException(
                        "Required field '" + field.getLabel() + "' is missing or empty");
            }
        }
    }

    private void validateFieldFormats(Map<UUID, FormQuestion> fieldMap,
            List<BookingFormAnswerRequest> answers) {
        for (BookingFormAnswerRequest answer : answers) {
            FormQuestion field = fieldMap.get(answer.getFieldId());
            String response = answer.getFieldResponse();

            if (field == null || response == null || response.trim().isEmpty())
                continue;

            if (field.getFieldType() == FieldType.PHONE
                    && !PHONE_PATTERN.matcher(response.trim()).matches()) {
                throw new InvalidFormResponseException(
                        "Invalid phone number format for field '" + field.getLabel() + "'");
            }
        }
    }
}
