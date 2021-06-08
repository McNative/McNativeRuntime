package org.mcnative.runtime.common.player;

import org.mcnative.runtime.api.player.input.PlayerTextInputValidator;
import org.mcnative.runtime.api.text.components.MessageComponent;

import java.util.function.Predicate;

public class DefaultPlayerTextInputValidator implements PlayerTextInputValidator {

    private final Predicate<String> validator;
    private final MessageComponent<?> errorMessage;

    public DefaultPlayerTextInputValidator(Predicate<String> validator, MessageComponent<?> errorMessage) {
        this.validator = validator;
        this.errorMessage = errorMessage;
    }

    public DefaultPlayerTextInputValidator(Predicate<String> validator) {
        this(validator, null);
    }

    @Override
    public boolean isValid(String s) {
        return validator.test(s);
    }

    @Override
    public MessageComponent<?> getErrorMessage() {
        return this.errorMessage;
    }
}
