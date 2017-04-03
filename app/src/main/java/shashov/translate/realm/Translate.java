package shashov.translate.realm;

import io.realm.RealmObject;

import java.io.Serializable;

public class Translate extends RealmObject implements Serializable {
    private String input;
    private String output;
    private String fromLang;
    private String toLang;
    private Long time;

    public Translate() {
        input = "";
        output = "";
        fromLang = "";
        toLang = "";
    }

    public String getFromLang() {
        return fromLang;
    }

    public void setFromLang(String fromLang) {
        this.fromLang = fromLang;
    }

    public String getToLang() {
        return toLang;
    }

    public void setToLang(String toLang) {
        this.toLang = toLang;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}