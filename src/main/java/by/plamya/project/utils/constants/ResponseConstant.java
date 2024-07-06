package by.plamya.project.utils.constants;

public enum ResponseConstant {
    USER_EMAIL_EXISTS("User with this email already exists: "),
    USER_USERNAME_EXISTS("User with this username already exists: "),
    USER_NOT_SAVE("User could not be saved. Please try again.");

    private String message;

    private ResponseConstant(String string) {
        this.message = message;
    }

}
