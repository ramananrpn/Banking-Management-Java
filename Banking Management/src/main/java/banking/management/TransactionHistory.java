package banking.management;

/**
 *
 * @author Ramanan R
 */
public class TransactionHistory {
    String account;
    String type;
    int amt;
    long balance;
  
    TransactionHistory(String account, String type, int amt, long balance)
    {
        this.account = account;
        this.type = type;
        this.amt = amt;
        this.balance = balance;
    }
}
