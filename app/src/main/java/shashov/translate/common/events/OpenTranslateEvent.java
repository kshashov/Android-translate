package shashov.translate.common.events;

import shashov.translate.dao.Translate;

public class OpenTranslateEvent {
    private Translate translate;

    public OpenTranslateEvent(Translate translate) {
        this.translate = translate;
    }

    public Translate getTranslate() {
        return translate;
    }

    public void setTranslate(Translate translate) {
        this.translate = translate;
    }
}
