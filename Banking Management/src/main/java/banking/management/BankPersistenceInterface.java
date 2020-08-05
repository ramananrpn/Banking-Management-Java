/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package banking.management;

/**
 *
 * @author raman-pt2680
 */
public interface BankPersistenceInterface {
    boolean addNewAccount(int customer_id,String acc_num, String name, String pass, long balance);
    int loginAuthentication(String l_acc);
    void deposit(String l_AccNum);
    void withdraw(String l_AccNum);
    void transfer(String l_AccNum);
    void history(String l_AccNum);
    void changePassword(String l_AccNum );
}

