package patmal.workshops.student.impl.logic;

import org.apache.commons.validator.routines.EmailValidator;

// Shared by InstructorManager and StudentManager (section 4.2 / 5.2): a null or
// syntactically invalid e-mail is rejected the same way for both.
final class EmailValidation {

    private EmailValidation() {
    }

    static boolean isValid(String email) {
        return email != null && EmailValidator.getInstance().isValid(email);
    }
}
