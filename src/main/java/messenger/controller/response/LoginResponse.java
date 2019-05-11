package messenger.controller.response;

@SuppressWarnings("unused")
public class LoginResponse {

    private Status status;
    private String authenticationToken;

    public LoginResponse(Status status, String authenticationToken) {
        this.status = status;
        this.authenticationToken = authenticationToken;
    }

    public enum Status {
        LOGIN_SUCCESSFUL,
        ACCOUNT_NOT_CONFIRMED,
        INVALID_LOGIN_OR_PASSWORD
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getAuthenticationToken() {
        return authenticationToken;
    }

    public void setAuthenticationToken(String authenticationToken) {
        this.authenticationToken = authenticationToken;
    }
}
