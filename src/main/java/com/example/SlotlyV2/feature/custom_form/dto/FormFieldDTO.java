package com.example.SlotlyV2.feature.custom_form.dto;

import java.util.UUID;

import com.example.SlotlyV2.feature.custom_form.FormField;
import com.example.SlotlyV2.feature.custom_form.enums.FieldType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormFieldDTO {
    private UUID id;

    @NotBlank(message = "label is required")
    private String label;

    @NotNull(message = "field type is required")
    private FieldType fieldType;

    @Builder.Default
    private boolean required = false;

    @Builder.Default
    private Integer displayOrder = 0;

    public FormFieldDTO(FormField formField) {
        this.id = formField.getId();
        this.label = formField.getLabel();
        this.fieldType = formField.getFieldType();
        this.required = formField.isRequired();
        this.displayOrder = formField.getDisplayOrder();
    }
}
