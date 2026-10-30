package com.itscoding.minibank.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class AccountNumberValidator implements ConstraintValidator<AccountNumber, String> {

    private static final Pattern FORMAT = Pattern.compile("^110-\\d{4}-\\d{4}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;          // null 검사는 @NotBlank에 맡긴다
        return FORMAT.matcher(value).matches();
    }
}
