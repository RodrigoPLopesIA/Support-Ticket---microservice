package com.rodrigolopes.ms.support_ticket.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<ValidEnum, String> {

    private String[] acceptedValues;

    @Override
    public void initialize(ValidEnum annotation) {
        acceptedValues = java.util.Arrays.stream(annotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .toArray(String[]::new);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true; 
        for (String s : acceptedValues) {
            if (s.equals(value)) return true;
        }
        return false;
    }
}
