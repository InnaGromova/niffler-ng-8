package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;

import static com.codeborne.selenide.Selenide.$$;

public class SpendTable {
    private final ElementsCollection tableRows = $$("#spendings tbody tr");

    public ElementsCollection getTableRows() {
        return tableRows;
    }
}
