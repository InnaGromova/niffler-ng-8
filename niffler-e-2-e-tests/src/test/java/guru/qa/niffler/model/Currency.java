package guru.qa.niffler.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Currency {
  RUB("₽"),
  USD("$"),
  EUR("€"),
  KZT("₸");

  public final String alias;
  }
