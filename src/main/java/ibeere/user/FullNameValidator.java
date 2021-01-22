package ibeere.user;

import ibeere.aggregate.profile.micro.FullName;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class FullNameValidator {
    public static FullName toFullName(String firstName, String lastName) throws UserInputException {

        if (isBlank(firstName)) {
            throw new UserInputException("No first name given");
        }

        if (isBlank(lastName)) {
            throw new UserInputException("No last name given");
        }

        String lastNameTrimmed = lastName.trim();
        String firstNameTrimmed = firstName.trim();

        if (lastNameTrimmed.length() > 20 || firstNameTrimmed.length() > 20) {
            throw new UserInputException("Name is too long");
        }

        return FullName.of(firstNameTrimmed, lastNameTrimmed);
    }
}