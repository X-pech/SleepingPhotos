package study.itmo.xpech.mdft.util;

public enum ExtraValues {
    EXTRA_CACHEPATH("study.itmo.xpech.mdft.extra.cachepath"),
    EXTRA_URL("study.itmo.xpech.mdft.extra.URL"),
    EXTRA_DESC("study.itmo.xpech.mdft.extra.DESC"),
    EXTRA_ID("study.itmo.xpech.mdft.extra.ID"),
    DATA_KEY_ARRAY("study.itmo.xpech.mdft.dataKeyArray"),
    DATA_KEY_DETAIL("study.itmo.xpech.mdft.dataKeyDetail");

    private String extraString;

    ExtraValues(String extraString) {
        this.extraString = extraString;
    }

    @Override
    public String toString() {
        return extraString;
    }

}
