package eecs285.proj4.wanggy;

public class TransactionClass
{
  String category;
  String merchant;
  double amount;

  TransactionClass(String incat, String inmer, double inamount)
  {
    category = incat;
    merchant = inmer;
    amount = inamount;
  }

  TransactionClass()
  {
    category = null;
    merchant = null;
    amount = 0;
  }
}
