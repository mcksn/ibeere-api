package ibeere.aggregate.question.answer;

public class AnswerProfileDto {
    private String credentials;
    private String name;
    private String imgUrl;
    private String path;
    private boolean verified;

    public AnswerProfileDto(AnswerProfile answerProfile) {
        this.credentials = answerProfile.getCredentials();
        this.name = answerProfile.getName();
        this.imgUrl = answerProfile.getImgUrl();
        this.path = answerProfile.getPath();
        this.verified = answerProfile.isVerified();
    }

    public String getCredentials() {
        return credentials;
    }

    public String getName() {
        return name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getPath() {
        return path;
    }

    public boolean isVerified() {
        return verified;
    }
}
