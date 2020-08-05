/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package banking.management;


import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author raman-pt2680
 */
public class XML implements BankPersistenceInterface{
    
    
     Banking Banking = new Banking();
     int ElementId = 0;
     
    static int getCustomerId(){
        Integer customerId = null ;
         try {
             DocumentBuilderFactory dbFactory =DocumentBuilderFactory.newInstance();
             DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
             Document doc = dBuilder.parse(XML.class.getResourceAsStream("/BankCustomers.xml"));

             Element rootElement = doc.getDocumentElement();
             customerId = Integer.parseInt(rootElement.getAttribute("Total_Customers"));
             
         } catch (ParserConfigurationException | SAXException | IOException | NumberFormatException ex) {
             Logger.getLogger(XML.class.getName()).log(Level.SEVERE, null, ex);
         }
         return customerId;
    }
          
     @Override
     public boolean addNewAccount(int customerId,String acc_num, String name, String pass, long balance){
            try {
                DocumentBuilderFactory dbFactory =DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(XML.class.getResource("/BankCustomers.xml").getFile());
                
                // root element //
                Element rootElement = doc.getDocumentElement();
                // System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
                
                // update Total_Customers attribute
                NamedNodeMap rootAttr = rootElement.getAttributes();
                Node nodeAttr = rootAttr.getNamedItem("Total_Customers");
                nodeAttr.setTextContent(String.valueOf(customerId));
                
                //rootnode acc
                Element newAccount = doc.createElement("Account");
                
                // setting attribute to element
                Attr attr = doc.createAttribute("customerId");
                attr.setValue(String.valueOf(customerId));
                newAccount.setAttributeNode(attr);
                
                //element name
                Element elementName = doc.createElement("Name");
                elementName.appendChild(doc.createTextNode(name));
                newAccount.appendChild(elementName);    
                
                //element AccNum
                Element elementAccount = doc.createElement("Account_Number");
                elementAccount.appendChild(doc.createTextNode(acc_num));
                newAccount.appendChild(elementAccount);    
                
                //element Password
                Element elementPassword = doc.createElement("Password");
                elementPassword.appendChild(doc.createTextNode(pass));
                newAccount.appendChild(elementPassword);  
                
                //element Balance
                Element elementBalance = doc.createElement("Balance");
                elementBalance.appendChild(doc.createTextNode(String.valueOf(balance)));
                newAccount.appendChild(elementBalance);  
                
                //appending NewNode to RootElement
                rootElement.appendChild(doc.createTextNode("\n"));
                rootElement.appendChild(newAccount);
                
                DOMSource source = new DOMSource(doc);

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                StreamResult result = new StreamResult(XML.class.getResource("/BankCustomers.xml").getFile());
                transformer.transform(source, result);
                
                /*OUTPUT
                StreamResult res = new StreamResult(System.out);
                transformer.transform(source, res);*/
                
                 //UPDATING TRANSACTION IN "HISTORY.xml" 
                 updateTransactionHistory(acc_num , "Initial Creation" , ""+balance , ""+balance );
                
            } catch (ParserConfigurationException | SAXException | IOException | DOMException | TransformerFactoryConfigurationError | IllegalArgumentException | TransformerException ex) {
            ex.printStackTrace();
            } 
            return true;
        }
     
    @Override
    public int loginAuthentication(String loginAccount) {
         try {
             int i = 0 ;          
                
                     //Travering xml 
                     DocumentBuilderFactory dbFactory =DocumentBuilderFactory.newInstance();
                     DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                     Document doc = dBuilder.parse("src/resources/BankCustomers.xml");
                               
                     NodeList nList = doc.getElementsByTagName("Account");
                     
                     for ( i = 0; i < nList.getLength(); i++ ) {
                           Node nNode = nList.item(i);
                           
                           if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                               Element element = (Element) nNode;
                               
                               Node node = element.getElementsByTagName("Account_Number").item(0);
                               String getAccount  = node.getTextContent();
                              
                              //System.out.println("XML ACC NUM : " + getAccount);
                               
                               if(getAccount.equals(loginAccount)){
                                   String loginPassword , getPassword;
                                     do { 
                                             System.out.print("\tPlease Enter Account Password: ");
                                             loginPassword = BankingUtilities.st.nextLine();
                                             
                                             node = element.getElementsByTagName("Password").item(0);
                                             getPassword = node.getTextContent();
                                             
                                             if (getPassword.equals(Banking.encryptPassword(loginPassword))) {
                                               node = element.getElementsByTagName("Name").item(0);
                                               String getName = node.getTextContent(); 
                                               System.out.println();
                                               System.out.println("\t\t** Welcome " + getName + " ** ");
                                             }
                                             else
                                             {
                                               System.out.println();
                                               System.out.println("\t\t** Invalid Password ** ");
                                               System.out.println();
                                             }
                                         } while (!getPassword.equals(Banking.encryptPassword(loginPassword)));
                                   ElementId = i;  
                                   return 1;
                               }
                           }                      
                        }
                     
                        
            } catch (ParserConfigurationException | SAXException | IOException | DOMException ex) {
             Logger.getLogger(XML.class.getName()).log(Level.SEVERE, null, ex);
                }
            return 0;
       }
    
    
     @Override
     public void deposit(String loginAccountNum) {
         try {
             DocumentBuilderFactory dbFactory =DocumentBuilderFactory.newInstance();
             DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
             Document doc = dBuilder.parse("src/resources/BankCustomers.xml");
             
             NodeList nList = doc.getElementsByTagName("Account");
             
             System.out.print("\n\tPlease Enter Amount to be deposited :  ");
             int depositAmount = BankingUtilities.in.nextInt();
             
             Node nNode = nList.item(ElementId);
             Node node = null;
             Element element = (Element) nNode;
             
             if (nNode.getNodeType() == Node.ELEMENT_NODE) {                
                 node = element.getElementsByTagName("Balance").item(0);
                 Integer availableBalance = Integer.parseInt(node.getTextContent());
                 int total = depositAmount+availableBalance;
                 node.setTextContent(""+total);
		   
             }
             node = element.getElementsByTagName("Balance").item(0);
             
             //writing updated value in xml 
                DOMSource source = new DOMSource(doc);
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                StreamResult result = new StreamResult("src/resources/BankCustomers.xml");
                transformer.transform(source, result);
                         
             System.out.println("\n\t** Amount deposited Successfully ** \n\t\t * Current Balance : " + node.getTextContent()+" *");
            
             //UPDATING TRANSACTION IN "HISTORY.xml" 
                updateTransactionHistory(loginAccountNum , "Deposit" , ""+depositAmount , node.getTextContent());
            
            
         } catch (ParserConfigurationException | SAXException | IOException | DOMException | TransformerFactoryConfigurationError | IllegalArgumentException | TransformerException ex) {
             Logger.getLogger(XML.class.getName()).log(Level.SEVERE, null, ex);
         }
    }
    
    @Override 
    public void withdraw(String loginAccountNum) {
         try {
             DocumentBuilderFactory dbFactory =DocumentBuilderFactory.newInstance();
             DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
             Document doc = dBuilder.parse("src/resources/BankCustomers.xml");
                  
             NodeList nList = doc.getElementsByTagName("Account");
             Node nNode = nList.item(ElementId);
            
             Element element = (Element) nNode;
             if (nNode.getNodeType() == Node.ELEMENT_NODE) {  
                 nNode = element.getElementsByTagName("Balance").item(0);
                 Integer availableBalance = Integer.parseInt(nNode.getTextContent());
                 if (availableBalance <= 1000) {
                     System.out.print("\n\t** Cannot process Operation . Minimum Balance should be maintained **\n ");
                 }
                 else{
                     int withdrawAmount;
                     do {
                     System.out.print("\n\tPlease Enter Amount to be withdrawn : ");
                     withdrawAmount = BankingUtilities.in.nextInt();
                     if (availableBalance-withdrawAmount < 1000) {
                     System.out.print("\n\t** Cannot process Operation . Minimum Balance should be maintained **\n ");
                     }
                 } while ( availableBalance - withdrawAmount < 1000);
                   
                   int total =  availableBalance - withdrawAmount;
                   nNode = element.getElementsByTagName("Balance").item(0);
                   
                   nNode.setTextContent(""+total);
                   
                   //writing updated value in xml 
                    DOMSource source = new DOMSource(doc);
                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                    StreamResult result = new StreamResult("src/resources/BankCustomers.xml");
                    transformer.transform(source, result);
                    
                    System.out.println("\n\t** Please Collect Cash ** \n\t Current Balance : " + nNode.getTextContent()+" *");
                    
                    //UPDATING TRANSACTION IN "HISTORY" TABLE
                       updateTransactionHistory(loginAccountNum , "Withdraw" , ""+withdrawAmount ,  nNode.getTextContent());
                     
                 }
                 
             }
             
         } catch (ParserConfigurationException | SAXException | IOException | DOMException | TransformerFactoryConfigurationError | IllegalArgumentException | TransformerException ex) {
             Logger.getLogger(XML.class.getName()).log(Level.SEVERE, null, ex);
         }
    }
  
    @Override
    public void transfer(String loginAccountNum) {
         try {
             DocumentBuilderFactory dbFactory =DocumentBuilderFactory.newInstance();
             DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
             Document doc = dBuilder.parse("src/resources/BankCustomers.xml");
                       
             NodeList nList = doc.getElementsByTagName("Account");
             Node nNode = nList.item(ElementId);
            
             Element element = (Element) nNode;
             
             if (nNode.getNodeType() == Node.ELEMENT_NODE) {  
                      nNode = element.getElementsByTagName("Balance").item(0);
                      Integer availableBalance = Integer.parseInt(nNode.getTextContent());
                      if (availableBalance <= 1000) {
                        System.out.print("\n\t** Cannot process Operation . Minimum Balance should be maintained **\n ");
                      }
                      else{
                          int bencificiaryId;
                          do{
                              System.out.print("\n\tPlease Enter Benefiaciary's Account Number : ");
                              String transferAccount = BankingUtilities.st.nextLine();
                              for ( bencificiaryId = 0; bencificiaryId < nList.getLength(); bencificiaryId++ ) {
                                    Node nodeBeneficiary = nList.item(bencificiaryId);

                                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                                        Element element_benificiary = (Element) nodeBeneficiary;                               
                                        Node node = element_benificiary.getElementsByTagName("Account_Number").item(0);
                                        String getAccount  = node.getTextContent();

                                        //System.out.println("XML ACC NUM : " + getAccount);
                                        if(getAccount.equals(transferAccount)){
                                            int transferAmount;
                                            do{
                                                System.out.print("\n\tPlease Enter Amount to be Transfered : ");
                                                transferAmount = BankingUtilities.in.nextInt();
                                                if(availableBalance - transferAmount < 1000){
                                                    System.out.print("\n\t\t** Cannot process Operation . Minimum Balance should be maintained **\n ");
                                                }
                                            }while(availableBalance - transferAmount < 1000);
                                            
                                            //updating beneficiary account
                                            node = element_benificiary.getElementsByTagName("Balance").item(0);
                                            Integer total  = Integer.parseInt(node.getTextContent()) + transferAmount;
                                            node.setTextContent(""+total);
                                            
                                            //updatating acticveuser account
                                            total = Integer.parseInt(nNode.getTextContent()) - transferAmount;
                                            nNode.setTextContent(""+total);
                                            //writing updated value in xml 
                                             DOMSource source = new DOMSource(doc);
                                             TransformerFactory transformerFactory = TransformerFactory.newInstance();
                                             Transformer transformer = transformerFactory.newTransformer();
                                             transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                                             transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                                             StreamResult result = new StreamResult("src/resources/BankCustomers.xml");
                                             transformer.transform(source, result);
                                             
                                             System.out.print("\n\t** Amount Transfered Successfully to Account : " + transferAccount + " ** \n\t\t ** Current Balance : " +nNode.getTextContent()+" **");
                                             
                                             //UPDATING TRANSACTION IN "HISTORY.xml"
                                                //active user
                                                updateTransactionHistory(loginAccountNum , "Transfer to " + transferAccount , ""+transferAmount , nNode.getTextContent());
                                                //Beneficiary
                                                updateTransactionHistory(transferAccount , "Credit from " + loginAccountNum , ""+transferAmount , node.getTextContent());                                               
                                                break;
                                        }
                                    }                              
                                }                              
                              if (bencificiaryId >= Banking.customerId) {
                                System.out.println();
                                System.out.println("\t\t** Invalid Account Number  ** ");
                              }
                          }while(bencificiaryId >= Banking.customerId);   
                      }                      
             }        
         } catch (ParserConfigurationException | SAXException | IOException | DOMException | TransformerFactoryConfigurationError | IllegalArgumentException | TransformerException ex) {
             Logger.getLogger(XML.class.getName()).log(Level.SEVERE, null, ex);
         }
    }
  
     @Override
     public void history(String loginAccountNum) {
         try {
             DocumentBuilderFactory dbFactory =DocumentBuilderFactory.newInstance();
             DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
             Document doc = dBuilder.parse("src/resources/BankCustomers.xml");
              
             //
             NodeList nList = doc.getElementsByTagName("Account");
             Node nNode = nList.item(ElementId);
             Element element = null ;
             if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                 element = (Element) nNode;
                 System.out.print("\n\t\t\t\t** Transaction History ** \n\t\t\tAccount Number : " + element.getElementsByTagName("Account_Number").item(0).getTextContent() + "\tName : " + element.getElementsByTagName("Name").item(0).getTextContent() + "\n\n");
                 System.out.print("\n\t-Operation- \t\t\t\t   -Amount- \t\t -Balance- \n");
             }

             //traversing History.xml
             dbFactory =DocumentBuilderFactory.newInstance();
             dBuilder = dbFactory.newDocumentBuilder();
             doc = dBuilder.parse("src/resources/History.xml");          
             nList = doc.getElementsByTagName("Transaction");
             int i;
             for(i=0 ; i < nList.getLength() ; i++){
                 
                  nNode = nList.item(i);
                 if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    element = (Element) nNode;                              
                    Node node = element.getElementsByTagName("Account_Number").item(0);                   
                    String getAccount  = node.getTextContent();
                    //System.out.println("XML ACC NUM : " + getAccount);
                    if(getAccount.equals(loginAccountNum)){
                         if(((element.getElementsByTagName("Transaction_Type").item(0).getTextContent()).equals("Deposit"))||((element.getElementsByTagName("Transaction_Type").item(0).getTextContent()).equals("Withdraw")))
                            System.out.print("\n\t" + element.getElementsByTagName("Transaction_Type").item(0).getTextContent()+ "\t\t\t\t\t\t" + element.getElementsByTagName("Amount").item(0).getTextContent() + "\t\t" + element.getElementsByTagName("Balance").item(0).getTextContent());
                         else  if((element.getElementsByTagName("Transaction_Type").item(0).getTextContent()).equals("Initial Creation"))
                            System.out.print("\n\t" + element.getElementsByTagName("Transaction_Type").item(0).getTextContent() + "\t\t\t\t" + element.getElementsByTagName("Amount").item(0).getTextContent() + "\t\t" + element.getElementsByTagName("Balance").item(0).getTextContent());
                         else
                            System.out.print("\n\t" + element.getElementsByTagName("Transaction_Type").item(0).getTextContent() + "\t\t\t" + element.getElementsByTagName("Amount").item(0).getTextContent()+ "\t\t" + element.getElementsByTagName("Balance").item(0).getTextContent());
                    }
                 }
             }
             
            }catch (ParserConfigurationException | SAXException | IOException | DOMException ex) {
             Logger.getLogger(XML.class.getName()).log(Level.SEVERE, null, ex);
         }
    }
    
    public void updateTransactionHistory(String acc_num , String type , String amount , String balance){
         try {
                 DocumentBuilderFactory dbFactory =DocumentBuilderFactory.newInstance();
                 DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                 Document doc = dBuilder.parse("src/resources/History.xml");
                
                 // root element //
                Element rootElement = doc.getDocumentElement();
                // System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

                //rootnode Transcation
                Element newTransaction = doc.createElement("Transaction");
                     
                //element Account_number
                Element elementAccount = doc.createElement("Account_Number");
                elementAccount.appendChild(doc.createTextNode(acc_num));
                newTransaction.appendChild(elementAccount);    
                
                //element Transaction_type
                Element elementType = doc.createElement("Transaction_Type");
                elementType.appendChild(doc.createTextNode(type));
                newTransaction.appendChild(elementType);    
                
                //element Amount
                Element elementAmount = doc.createElement("Amount");
                elementAmount.appendChild(doc.createTextNode(""+amount));
                newTransaction.appendChild(elementAmount);   
                
                //element Balance
                Element elementBalance = doc.createElement("Balance");
                elementBalance.appendChild(doc.createTextNode(balance));
                newTransaction.appendChild(elementBalance);  
                
                //appending NewNode to RootElement
                rootElement.appendChild(doc.createTextNode("\n"));
                rootElement.appendChild(newTransaction);
                
                DOMSource source = new DOMSource(doc);

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                StreamResult result = new StreamResult("src/resources/History.xml");
                transformer.transform(source, result);
                
                /*OUTPUT
                StreamResult res = new StreamResult(System.out);
                transformer.transform(source, res);*/
                
            } catch (ParserConfigurationException | SAXException | IOException | DOMException | TransformerFactoryConfigurationError | IllegalArgumentException | TransformerException ex) {
                ex.printStackTrace();
            } 
    }
    @Override
    public void changePassword(String loginAccountNum)
    {
         try {
             System.out.print("\n\t ## Please Change Your Account Password for Secutity Reasons ## \n");
             String changePassword;
             String confirmPassword;
             do {
                 System.out.print("\tPlease Enter Password :");
                 changePassword = BankingUtilities.st.nextLine();
                 System.out.print("\tPlease Re-Enter Password :");
                 confirmPassword = BankingUtilities.st.nextLine();
                 if (!changePassword.equals(confirmPassword)) {
                 System.out.println();
                 System.out.println("\t\t** Password Mismatch ** ");
                 System.out.println();
                 }
             } while (!changePassword.equals(confirmPassword));
             
             DocumentBuilderFactory dbFactory =DocumentBuilderFactory.newInstance();
             DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
             Document doc = dBuilder.parse("src/resources/BankCustomers.xml");
                       
             NodeList nList = doc.getElementsByTagName("Account");
             Node nNode = nList.item(ElementId);
             Element element = (Element) nNode;
             Node node = element.getElementsByTagName("Password").item(0);
             node.setTextContent(Banking.encryptPassword(changePassword));           
             System.out.print("\t\t ** Password Changed Successfully ** ");
             
             //writing updated value in xml 
                    DOMSource source = new DOMSource(doc);
                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                    StreamResult result = new StreamResult("src/resources/BankCustomers.xml");
                    transformer.transform(source, result);
         } catch (ParserConfigurationException | SAXException | IOException | DOMException | TransformerFactoryConfigurationError | IllegalArgumentException | TransformerException ex) {
             Logger.getLogger(XML.class.getName()).log(Level.SEVERE, null, ex);
         }
    }
}
