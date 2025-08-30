package guru.qa.niffler.jupiter;

import guru.qa.niffler.service.impl.SpendApiClient;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.Currency;
import guru.qa.niffler.model.SpendJson;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Date;

public class CreateSpendingExtension implements BeforeEachCallback {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CreateSpendingExtension.class);
  private final SpendApiClient spendApiClient = new SpendApiClient();

  @Override
  public void beforeEach(ExtensionContext context) {
      AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
              .ifPresent(userAnno -> {
                  if (userAnno.spendings().length > 0) {

                      Spend spendingAnnotation = userAnno.spendings()[0];

                      SpendJson spendJson = new SpendJson(
                              null,
                              new Date(),
                              new CategoryJson(
                                      null,
                                      spendingAnnotation.category(),
                                      userAnno.username(),
                                      false
                              ),
                              Currency.RUB,
                              spendingAnnotation.amount(),
                              spendingAnnotation.description(),
                              userAnno.username()
                      );

                      SpendJson created = spendApiClient.createSpend(spendJson);
                      context.getStore(NAMESPACE).put(context.getUniqueId(), created);

                  }
        });
  }
}
