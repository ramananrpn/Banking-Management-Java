package banking.management;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ramanan R
 * 
 * deposit()
 * withwithdrawAmount()
 * transfer()
 * history()
 * changeAllpassword()   
 * updateTransactionHistory()
 */
public class MySQL implements BankPersistenceInterface{
    String query="";
    
    Connection conn;

    public MySQL() {
        try {
            this.conn = DataBaseConnection.getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(MySQL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public boolean addNewAccount(int customerId,String accountNum, String name, String pass, long balance)
    {
                 
            int check =  0;
            try {      
               //INSERT INTO DATABASE            
                String query = "INSERT INTO `customers`(`name`, `account_number`, `password`, `balance`) VALUES ('" +name+ "','" +accountNum+ "', '"+pass+"' , "+balance+")";

               
                    Statement stmt = conn.createStatement();
                    
                    if(stmt.executeUpdate(query)>0){
  
                        //INSERT in `History
                            query = "INSERT INTO `history`(`account_number`, `transaction_type`, `amount`, `balance`) VALUES ('"+accountNum+"', 'Initial Creation' , '"+balance+"' , '"+balance+"' )";
                            
                            if(stmt.executeUpdate(query)>0){
                                check = 1;
                            }
                    }
        } catch (Exception ex) {
            Logger.getLogger(MySQL.class.getName()).log(Level.SEVERE, null, ex);
        }
                 
                        //   
                  if(check == 1)
                      return true;
                  else
                      return false;
    }
    
    @Override
    public int loginAuthentication(String loginAccountNum) {
    int flag=0;   
        try {
 
            query = "SELECT * FROM customers WHERE `account_number` = '"+loginAccountNum+"'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while(rs.next()){
                //System.out.println(Customer_obj[i].accountNum);
                if (rs.getString("account_number").equals(loginAccountNum)) {
                    flag=1;
                    String loginPassword;
                    do { System.out.print("\tPlease Enter Account Password: ");
                    loginPassword = BankingUtilities.st.nextLine();
                    if (rs.getString("password").equals(Banking.encryptPassword(loginPassword))) {
                        System.out.println();
                        System.out.println("\t\t** Welcome " + rs.getString("name") + " ** ");
                    }
                    else
                    {
                        System.out.println();
                        System.out.println("\t\t** Invalid Password ** ");
                        System.out.println();
                    }
                    } while (!rs.getString("password").equals(Banking.encryptPassword(loginPassword)));
                    
                    return 1;
                }
            }
             } catch (SQLException ex) {
            Logger.getLogger(MySQL.class.getName()).log(Level.SEVERE, null, ex);
        }

    return 0;
  }
        
    @Override
    public void deposit(String loginAccountNum ) {
        try {
            
            System.out.print("\n\tPlease Enter Amount to be deposited :  ");
            int dep = BankingUtilities.in.nextInt();
            
            //DB
            query = "SELECT * FROM `customers` WHERE `account_number` = '"+loginAccountNum+"'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                query = "UPDATE `customers` SET `balance`="+(rs.getInt("balance")+dep)+" WHERE `account_number` = '"+loginAccountNum+"'";
            }
            if(stmt.executeUpdate(query)>0){
                query = "SELECT * FROM `customers` WHERE `account_number` = '"+loginAccountNum+"'";
                rs = stmt.executeQuery(query);
                while(rs.next()){
                System.out.println("\n\t** Amount deposited Successfully ** \n\t\t * Current Balance : " + rs.getInt("balance")+" *");
                
                //UPDATING TRANSACTION IN "HISTORY" TABLE
                updateTransactionHistory(loginAccountNum , "Deposit" , dep , rs.getInt("balance") , conn);
                
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(MySQL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    @Override
    public void withdraw(String loginAccountNum) {
        try {
           
            query = "SELECT * FROM `customers` WHERE `account_number` = '"+loginAccountNum+"'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while(rs.next()){
                if (rs.getInt("balance") <= 1000) {
                    System.out.print("\n\t** Cannot process Operation . Minimum Balance should be maintained **\n ");
                }
                else {
                    int withdrawAmount;
                    do {
                        System.out.print("\n\tPlease Enter Amount to be withdrawn : ");
                        withdrawAmount = BankingUtilities.in.nextInt();
                        
                        //ensure MIN balance
                        if (rs.getInt("balance") - withdrawAmount < 1000) {
                            System.out.print("\n\t** Cannot process Operation . Minimum Balance should be maintained **\n ");
                        }
                        
                    } while ( rs.getInt("balance")  - withdrawAmount < 1000);
                    
                    query = "UPDATE `customers` SET `balance`= "+ (rs.getInt("balance")-withdrawAmount) +" WHERE `account_number` = '"+loginAccountNum+"'";
                    if(stmt.executeUpdate(query)>0){
                        query = "SELECT * FROM `customers` WHERE `account_number` = '"+loginAccountNum+"'";
                        rs = stmt.executeQuery(query);
                        while(rs.next()){
                            System.out.println("\n\t** Please Collect Cash ** \n\t Current Balance : " + rs.getInt("balance"));
                             
                            //UPDATING TRANSACTION IN "HISTORY" TABLE
                            updateTransactionHistory(loginAccountNum , "Withdraw" , withdrawAmount , rs.getInt("balance") , conn);
                           
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MySQL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
  
    
    @Override
    public void transfer(String loginAccountNum) {
        try {
            
            String queryFrom = "SELECT * FROM `customers` WHERE `account_number` = '"+loginAccountNum+"'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(queryFrom);
            long balanceFrom = 0 ;
            while(rs.next()){
                balanceFrom = rs.getInt("balance");
            }
                if (balanceFrom <= 1000) {
                    System.out.print("\n\t** Cannot process Operation . Minimum Balance should be maintained **\n ");
                }
                else {
                    int flag = 0;
                    do {
                        System.out.print("\n\tPlease Enter Benefiaciary's Account Number : ");
                        String transferAccount = BankingUtilities.st.nextLine();
                        String queryTo = "SELECT * FROM `customers` WHERE `account_number` = '"+transferAccount+"'";                      
                        ResultSet rsTransfer = stmt.executeQuery(queryTo);
                        while(rsTransfer.next()) {
                            flag= 1;
                            if (rsTransfer.getString("account_number").equals(transferAccount)) {
                                int transferAmount;
                                do {
                                    System.out.print("\n\tPlease Enter Amount to be Transfered : ");
                                    transferAmount = BankingUtilities.in.nextInt();
       
                                        if (balanceFrom  - transferAmount < 1000) {
                                            System.out.print("\n\t\t** Cannot process Operation . Minimum Balance should be maintained **\n ");
                                        }
                                    
                                } while (balanceFrom - transferAmount < 1000);
                                
                                queryFrom = "UPDATE `customers` SET `balance`= "+ (balanceFrom-transferAmount) +" WHERE `account_number` = '"+loginAccountNum+"'";
                                queryTo = "UPDATE `customers` SET `balance`= "+ (rsTransfer.getInt("balance")+transferAmount) +" WHERE `account_number` = '"+transferAccount+"'";
                                
                                if(stmt.executeUpdate(queryFrom)>0 && stmt.executeUpdate(queryTo)>0){
                                    
                                    //updating self account history
                                    queryFrom = "SELECT * FROM `customers` WHERE `account_number` = '"+loginAccountNum+"'";
                                    ResultSet rsUpdate = stmt.executeQuery(queryFrom);
                                    while(rsUpdate.next()){
                                        System.out.print("\n\t** Amount Transfered Successfully to Account : " + transferAccount + " ** \n\t\t ** Current Balance : " + rsUpdate.getInt("balance")+" **");
                                        //UPDATING TRANSACTION IN "HISTORY" TABLE
                                        updateTransactionHistory(loginAccountNum , "Transfer to " + transferAccount , transferAmount , rsUpdate.getInt("balance") , conn);
                                    }
                                    
                                    //updating beneficiary account history
                                    queryTo = "SELECT * FROM `customers` WHERE `account_number` = '"+transferAccount+"'";                      
                                    rsUpdate = stmt.executeQuery(queryTo);
                                    while(rsUpdate.next()){
                                        //UPDATING TRANSACTION IN "HISTORY" TABLE
                                        updateTransactionHistory(transferAccount , "Credit from " + loginAccountNum , transferAmount , rsUpdate.getInt("balance") , conn);
                                    }                                   
                                }
                                break;
                            }
                        }
                        if (flag == 0) {
                            System.out.println();
                            System.out.println("\t\t** Invalid Account Number  ** ");
                        }
                    } while (flag==0);
                   
                }
            
        } catch (SQLException ex) {
            Logger.getLogger(MySQL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
  
    
    @Override
    public void history(String loginAccountNum) {
        try {
        
            query = "SELECT * FROM `customers` WHERE `account_number` = '"+loginAccountNum+"'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                System.out.print("\n\t\t\t\t** Transaction History ** \n\t\t\tAccount Number : " + rs.getString("account_number") + "\tName : " + rs.getString("name")  + "\n\n");
                
            }
            
            query = "SELECT * FROM `history` WHERE `account_number` = '"+loginAccountNum+"'";
            rs = stmt.executeQuery(query);
            
            System.out.print("\n\t-Operation- \t\t\t\t   -Amount- \t\t -Balance- \n");
            while(rs.next()) {
               // if (h.account.equals(BankingUtilities.Banking.Customer_obj[id].accountNum)) {
                    if(rs.getString("transaction_type").equals("Deposit")||rs.getString("transaction_type").equals("Withdraw"))
                        System.out.print("\n\t" + rs.getString("transaction_type") + "\t\t\t\t\t\t" + rs.getInt("amount") + "\t\t" + rs.getInt("balance"));
                    else  if(rs.getString("transaction_type").equals("Initial Creation"))
                        System.out.print("\n\t" + rs.getString("transaction_type") + "\t\t\t\t" + rs.getInt("amount") + "\t\t" + rs.getInt("balance"));
                    else
                        System.out.print("\n\t" + rs.getString("transaction_type") + "\t\t\t" + rs.getInt("amount") + "\t\t" +rs.getInt("balance"));
               // }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MySQL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
  
    
    @Override
    public void changePassword(String loginAccountNum )
    {
        try {
            Banking Banking = new Banking();
            System.out.print("\n\t ## Please Change Your Account Password for Secutity Reasons ## \n");
            String c_pass = Banking.getPassword();
            
            query = "UPDATE `customers` SET `password`= '"+ Banking.encryptPassword(c_pass) +"' WHERE `account_number` = '"+loginAccountNum+"'";
            Statement stmt = conn.createStatement();
            if(stmt.executeUpdate(query)>0){
                System.out.print("\t\t ** Password Changed Successfully ** ");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MySQL.class.getName()).log(Level.SEVERE, null, ex);
        }
     
    }
    
    public void updateTransactionHistory(String accountNum , String type , int amount , long balance,Connection conn){
        try {
            //INSERT in `History
            query = "INSERT INTO `history`(`account_number`, `transaction_type`, `amount`, `balance`) VALUES ('"+accountNum+"', '"+type+"' , '"+amount+"' , '"+balance+"' )";
            Statement stmt = conn.createStatement();                
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
           Logger.getLogger(MySQL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
       //PRINTING CUSTOMER TABLE FROM DB
       public void viewAllCustomers(){
             query = "SELECT * FROM customers";
        
             try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("\n\t\t-Name-\t\t\t-Account Number-\t\t-Balance-\t\t-Password-");
            while(rs.next()){				
				String name = rs.getString("name");
				String accountNum = rs.getString("account_number");
				String password = rs.getString("password");
				int balance = rs.getInt("balance");
				System.out.println("\t\t"+name+ "\t\t\t" +accountNum+ "\t\t\t" +balance+ "\t\t\t" +password);
			}
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
}
