package shashov.translate.dao;

import io.realm.RealmObject;

import java.io.Serializable;

public class Translate extends RealmObject implements Serializable {
    private String input;
    private String output;
    private String fromLang;
    private String toLang;
    private Long time;
    private long favTime;

    public Translate() {
        input = "";
        output = "";
        fromLang = "";
        toLang = "";
        favTime = 0;
    }

    public Translate(Translate translate) {
        input = translate.getInput();
        output = translate.getOutput();
        fromLang = translate.getFromLang();
        toLang = translate.getToLang();
        favTime = translate.getFavTime();
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

    public long getFavTime() {
        return favTime;
    }

    public void setFavTime(long favTime) {
        this.favTime = favTime;
    }

    public boolean isFavorite() {
        return favTime != 0;
    }
}