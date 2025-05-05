package guru.qa.niffler.values;

public enum AuthorityType {
    READ("read"),
    WRITE("write");

    private final String value;

    AuthorityType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
