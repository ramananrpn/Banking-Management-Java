package banking.management;


/**
 *
 * @author Ramanan R
 * 
 * create()
 * print()
 * 
 */
public class Customers {
    
        int customerId;
        String accountNumber;
        String name;
        long balance = 1000;
        String password ;
        int transaction_num = 0; 
      
        public long generateAccountNumber(){
        return  System.currentTimeMillis();
        }

        public Customers() {
        }
        
        void create(int customerId ,String name , String pass ){          
            this.customerId = customerId;
            this.name = name;
            this.accountNumber = "AC"+generateAccountNumber();
            this.password = pass;

            if(Banking.BankPersistenceInterface.addNewAccount(customerId,accountNumber,name,pass,balance)){
                                System.out.println("\n\t\t** Account created Successfully ** ");

                                //printing
                                System.out.print("\n\t\tName\t\tAccount Number\t\tBalance\t\tPassword");
                                print();                          
             }
        }

        void print() {
         System.out.print( "\n\t\t"+ name + "\t\t" + accountNumber + "\t\t" + balance + "\t\t" + password);
        }

}
