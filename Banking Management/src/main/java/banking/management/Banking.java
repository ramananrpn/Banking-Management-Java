package banking.management;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 *
 * @author Ramanan R
 * 
 * createXML()
 * password()
 * encrypt()
 * main()
 * 
 */

public class Banking {

    static BankPersistenceInterface BankPersistenceInterface ;
    
    int customerId = XML.getCustomerId() ;
    
    static Properties props = new Properties();
    //PASSWORD FUNCTION
    String getPassword(){
        String c_pass;
        String c_cpass;
        
         do
        {            
            boolean reg;
            do{

                System.out.print("\t\tPlease Enter Password : ");
                c_pass = BankingUtilities.st.nextLine();
                reg = Pattern.matches("^(?=.*\\d)(?=.*[a-zA-Z]).{4,8}$",c_pass);
                if(!reg){
                System.out.println("-- Password should contain : 4-8 chars , atleast 1 upper or lowercase , digit --");   
                }
            }while(!reg);
            System.out.print("\t\tPlease Re-Enter Password : ");
            c_cpass = BankingUtilities.st.nextLine();
            if (!c_pass.equals(c_cpass)) {
                System.out.println("\n\t\t** Password Mismatch ** \n");
            }
        } while (!c_pass.equals(c_cpass));
         
        return c_pass;
    }
    
    void createAccount(){
        Customers customer = new Customers();
        System.out.print("\n\t\tPlease Enter Customer Name : ");
        String customerName = BankingUtilities.st.nextLine();
     
        customer.create(++customerId , customerName, encryptPassword(getPassword()));

        
    }
    
    void accountLogin() {
    int flag=0;
    String loginAccount="";
    do
    {
   
            System.out.print("\t\t\t***\n\tPlease Enter Account Number : ");
            loginAccount = BankingUtilities.st.nextLine();
            
            flag= BankPersistenceInterface.loginAuthentication(loginAccount);
            
            if (flag!=1) {
                System.out.println();
                System.out.println("\t\t** Invalid Account Number  ** ");
            }    
                
    } while (flag==0);
    
    System.out.print("\n\t\t 1 --> Deposit \n\t\t 2 --> Withdraw \n\t\t 3 --> Money Transfer \n\t\t 4 --> Transaction History \n\t\t 5 --> Change Password \n\t\t\t\t** \n\t\t Please Enter Your Choice : \t");
    int option = BankingUtilities.in.nextInt();
    switch (option) {
    case 1: BankPersistenceInterface.deposit(loginAccount);
      break;
    case 2: BankPersistenceInterface.withdraw(loginAccount);
      break;
    case 3: BankPersistenceInterface.transfer(loginAccount);
      break;
    case 4: BankPersistenceInterface.history(loginAccount);
      break;
    case 5 : BankPersistenceInterface.changePassword(loginAccount);
    }  
  }
    
    static String encryptPassword(String password) { 
        int length = password.length();
        String encodedPassword = "";
        for (int i = 0; i < length; i++) { 
            char passwordCharacter;
            switch (password.charAt(i)) {
                case 'Z':
                    passwordCharacter = 'A';
                    break;
                case 'z':
                    passwordCharacter = 'a';
                    break;
                case '9':
                    passwordCharacter = '0';
                    break;
                default:
                    passwordCharacter = (char)(password.charAt(i) + 1 );
                    break;
            }
            encodedPassword = encodedPassword + passwordCharacter;
        }
        return encodedPassword;
    }

    //MAIN FUNCTION
    public static void main(String[] args) throws SQLException {
        MySQL MySQL = new MySQL();
        
        try (InputStream file = Banking.class
                .getClassLoader().getResourceAsStream("Banking.properties")) {
            // load the properties file            
            props.load(file);
            // assigning db parameters
            String decision = props.getProperty("implementation");
            if(decision.equals("xml")){
                BankPersistenceInterface = new XML();
            }
            else{
                BankPersistenceInterface = new MySQL();
                
            }
            
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        
            Banking banking = new Banking();
           
            //creating xml file
            //bank.create_xml();

            System.out.println("\t\t\t\t WELCOME TO BANKING MANAGEMENT SYSTEM ");
            
           // System.out.print(String.format("\n\t\t\t     -- Connected to database '%s' "+ "successfully --", conn.getCatalog()));
            
            int ch;
            do
            {
                System.out.print("\n\n\t\t 1 --> Add Customer \n\t\t 2 --> Login \n\t\t 3 --> View Customer DB \n\t\t 4 --> Exit \n\t\t Please Enter Your Choice : \t");
                ch = BankingUtilities.in.nextInt();
                switch (ch) {
                    case 1: 
                        banking.createAccount();
                        break;
                    case 2:  
                        banking.accountLogin();
                        break;
                    case 3: 
                        MySQL.viewAllCustomers();
                        break;                    
                    case 4:                      
                        System.out.println("\t\t\t\t\n** ! Thank You for using our service ! **");
                }
            } while (ch != 4);
       
    }
}
