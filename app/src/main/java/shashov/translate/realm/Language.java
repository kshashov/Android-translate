package shashov.translate.realm;

/**
 * Created by envoy on 23.03.2017.
 */
public class Language {
    private String code;
    private String title;

    public Language() {
    }

    public Language(String code, String title) {
        this.code = code;
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
