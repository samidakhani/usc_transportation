import java.util.*;
import java.sql.*;
import java.lang.*;


  class Hw2 {
     public static void main(String args[]) {

      Connection myConn=null;
      String query_type = args[0];

      try{
      
      Connector connect = new Connector();
      myConn=connect.ConnectDb();

      }catch(Exception e){
          System.out.println(e);
      }

      switch(query_type){
      
      case "window": 
                     if(args.length != 6 ){
                         System.out.println("Please specify the correct no of parameters");
                         return;
                     }
                     WindowQuery window = new WindowQuery();
                     window.GetnerateQuery(args,myConn);                   
                     break;

      case "within": 
		     
                     if(args.length != 3 ){
                         System.out.println("Please specify the correct no of parameters");
                         return;
                     }
                     WithinQuery within = new WithinQuery();
                     within.GetnerateQuery(args,myConn);
                     break;

      case "nearest-neighbor":      
                          if(args.length != 4 ){
                              System.out.println("Please specify the correct no of parameters");
                              return;
                              }
		              NearestQuery near = new NearestQuery();
                              near.GetnerateQuery(args,myConn);
                              break;

      case "fixed":     
                   if(args.length != 2 ){
                         System.out.println("Please specify the correct no of parameters");
                         return;
                     }
		   FixedQuery fixed = new FixedQuery();
                   fixed.GetnerateQuery(args,myConn);
                   break;
      default:
               System.out.println("Invalid query type. Please specify the correct query type.");
      
      }    

          
     }
    }


    class Connector {
      
      public  Connection ConnectDb(){
        
      Connection myConn=null; ;  
    
        try{
          Class.forName("oracle.jdbc.driver.OracleDriver");
        } 
        catch(Exception ex){
           System.out.println(ex);
        }  
   
        try{

         myConn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl","system","system");
        }
         catch(SQLException ex){
           System.out.println("Error Connecting to the Database: "+ ex);
        } 
   
         return myConn;

     }
   }


   class WindowQuery {
 
      public void GetnerateQuery(String args[],Connection myConn){
      
      String object_type=args[1];
      int lower_x = Integer.parseInt(args[2]);
      int lower_y = Integer.parseInt(args[3]);
      int upper_x = Integer.parseInt(args[4]);
      int upper_y = Integer.parseInt(args[5]);
      String query="";


      switch(object_type) {
 
      case "student" :
                      query="SELECT S.STUDENT_ID " +
                             "FROM STUDENT S " +
                             "WHERE SDO_INSIDE(S.STUDENT_LOC, " +
                             "SDO_GEOMETRY(2003,NULL,NULL, " +
                             "SDO_ELEM_INFO_ARRAY(1,1003,3), " +
                             "SDO_ORDINATE_ARRAY("+lower_x+","+lower_y+","+upper_x+","+upper_y+")))='TRUE' ";
                      break;

      case "building":
                      query="SELECT B.BUILDING_ID " +
                             "FROM BUILDING B " +
                             "WHERE SDO_INSIDE(B.BUILDING_LOC, " +
                             "SDO_GEOMETRY(2003,NULL,NULL, " +
                             "SDO_ELEM_INFO_ARRAY(1,1003,3), " +
                             "SDO_ORDINATE_ARRAY("+lower_x+","+lower_y+","+upper_x+","+upper_y+")))='TRUE' ";
                      break;

      case "tramstops":
                       query="SELECT T.TRAMSTOP_ID " +
                             "FROM TRAMSTOP T " +
                             "WHERE SDO_INSIDE(T.TRAMSTOP_LOC, " +
                             "SDO_GEOMETRY(2003,NULL,NULL, " +
                             "SDO_ELEM_INFO_ARRAY(1,1003,3), " +
                             "SDO_ORDINATE_ARRAY("+lower_x+","+lower_y+","+upper_x+","+upper_y+")))='TRUE' ";
                       break;
      default:
               System.out.println("Invalid object type.Please specify a valid object type");

       }
     


      try{
     
        Statement myStmnt = myConn.createStatement();

        ResultSet myRs= myStmnt.executeQuery(query);
         
        switch(object_type) {
         
           case "student" :
                           System.out.println(" Students");
        	           System.out.println("------------");
                            while (myRs.next()){
                            System.out.println(myRs.getString("STUDENT_ID"));         
                            } 
                           break;
          case "building":
                          System.out.println(" Buildings");
        		  System.out.println("------------");
                           while (myRs.next()){
                            System.out.println(myRs.getString("BUILDING_ID"));         
                            }
                           break;

          case "tramstops":
                           System.out.println(" Tramstops");
                           System.out.println("------------");
                            while (myRs.next()){
                            System.out.println(myRs.getString("TRAMSTOP_ID"));         
                            }
                           break;

         } 

        
       

      }catch(Exception ex){
         System.out.println(ex);
       }   

      }
               
    }


   class WithinQuery {
 
      public void GetnerateQuery(String args[],Connection myConn){
         
        String student_id=args[1];
        int distance = Integer.parseInt(args[2]);  
        String query1="";
        String query2="";

        try{
        Statement myStmnt = myConn.createStatement();

        query1 ="SELECT B.BUILDING_ID "+
               "FROM STUDENT S,BUILDING B "+
               "WHERE S.STUDENT_ID='"+student_id+"' "+
               "AND SDO_WITHIN_DISTANCE(B.BUILDING_LOC,S.STUDENT_LOC,'distance="+distance+"')='TRUE' ";


        

        ResultSet myRs1= myStmnt.executeQuery(query1);

        System.out.println(" Buildings");
        System.out.println("------------");
        while (myRs1.next()){
         System.out.println(myRs1.getString("BUILDING_ID"));         
        }

       query2 ="SELECT T.TRAMSTOP_ID "+
               "FROM STUDENT S,TRAMSTOP T "+
               "WHERE S.STUDENT_ID='"+student_id+"' "+
               "AND SDO_WITHIN_DISTANCE(T.TRAMSTOP_LOC,S.STUDENT_LOC,'distance="+distance+"')='TRUE' ";
       
        ResultSet myRs2= myStmnt.executeQuery(query2);

        System.out.println("\n Tramstops");
        System.out.println("------------");
        while (myRs2.next()){
         System.out.println(myRs2.getString("TRAMSTOP_ID"));         
        }


      }catch(Exception ex){
         System.out.println(ex);
       }   
     

          
      }
    
    }

  class NearestQuery {
 
      public void GetnerateQuery(String args[],Connection myConn){

      String object_type=args[1];
      String building_id = args[2];
      int objects_no = Integer.parseInt(args[3]);
      String query="";

      switch(object_type){

       case "building" : 
                       objects_no=objects_no+1;
                       query="SELECT BUILDING_ID FROM " + 
                             "( SELECT B2.BUILDING_ID,SDO_NN_DISTANCE(1) AS DISTANCE " +
                             "FROM BUILDING B1,BUILDING B2 " +
                             "WHERE B1.BUILDING_ID='"+building_id+"'  " +
                             "AND SDO_NN(B2.BUILDING_LOC,B1.BUILDING_LOC,'sdo_num_res="+objects_no +"',1) ='TRUE'  AND B1.BUILDING_ID != B2.BUILDING_ID " +
                             "ORDER BY DISTANCE) " ;       
                       break;

       case "student":query="SELECT STUDENT_ID FROM "+
                            "(SELECT S.STUDENT_ID,SDO_NN_DISTANCE(1) AS DISTANCE "+
                            "FROM BUILDING B,STUDENT S "+
                            "WHERE B.BUILDING_ID='"+building_id+"'  "+
                            "AND SDO_NN(S.STUDENT_LOC,B.BUILDING_LOC,'sdo_num_res="+objects_no +"',1) ='TRUE' "+
                            "ORDER BY DISTANCE) ";
                       break;
 
       case "tramstops":query="SELECT TRAMSTOP_ID FROM "+
                            "(SELECT T.TRAMSTOP_ID,SDO_NN_DISTANCE(1) AS DISTANCE "+
                            "FROM BUILDING B,TRAMSTOP T "+
                            "WHERE B.BUILDING_ID='"+building_id+"'  "+
                            "AND SDO_NN(T.TRAMSTOP_LOC,B.BUILDING_LOC,'sdo_num_res="+objects_no +"',1) ='TRUE' "+
                            "ORDER BY DISTANCE) ";
                        break;       

      }

      try{
     
      Statement myStmnt = myConn.createStatement();

      ResultSet myRs= myStmnt.executeQuery(query);

      switch(object_type){

       case "building" : System.out.println(" Buildings");
        		  System.out.println("------------");
                           while (myRs.next()){
                            System.out.println(myRs.getString("BUILDING_ID"));         
                            }         
                       break;

       case "student": System.out.println(" Students");
        		  System.out.println("------------");
                           while (myRs.next()){
                            System.out.println(myRs.getString("STUDENT_ID"));         
                            } 
                       break;
 
       case "tramstops": System.out.println(" Tramstops");
        		  System.out.println("------------");
                           while (myRs.next()){
                            System.out.println(myRs.getString("TRAMSTOP_ID"));         
                            } 
                        break;       

       }
      }catch(Exception ex){
         System.out.println(ex);
       }

      }
    
    }

  class FixedQuery {
 
      public void GetnerateQuery(String args[],Connection myConn){

        int query_no = Integer.parseInt(args[1]);  
        String query="";
        String query2="";


        switch(query_no){

         case 1:query ="SELECT S.STUDENT_ID " +
                       "FROM STUDENT S, TRAMSTOP T " +
                       "WHERE (T.TRAMSTOP_ID='t2ohe' OR  T.TRAMSTOP_ID='t6ssl') " +
                       " AND SDO_INSIDE(S.STUDENT_LOC,T.TRAMSTOP_LOC)='TRUE' " ; 

                query2 ="SELECT B.BUILDING_ID " +
                       "FROM BUILDING B, TRAMSTOP T " +
                       "WHERE (T.TRAMSTOP_ID='t2ohe' OR  T.TRAMSTOP_ID='t6ssl') " +
                       " AND SDO_INSIDE(B.BUILDING_LOC,T.TRAMSTOP_LOC)='TRUE' " ;
                
                break;

         case 2:
		query ="SELECT S.STUDENT_ID,T.TRAMSTOP_ID "+
                       "FROM STUDENT S,TRAMSTOP T "+
                       "WHERE SDO_NN(T.TRAMSTOP_LOC,S.STUDENT_LOC,'sdo_num_res=2') ='TRUE' ";
                break;

         case 3:
		query ="SELECT TRAMSTOP_ID "+
                       "FROM (SELECT T.TRAMSTOP_ID,COUNT(B.BUILDING_ID) AS NO_COVERED "+
 		       "FROM BUILDING B,TRAMSTOP T "+
                       "WHERE  SDO_WITHIN_DISTANCE(B.BUILDING_LOC,T.TRAMSTOP_LOC,'distance=250')='TRUE' "+
                       "GROUP BY T.TRAMSTOP_ID "+
                       "ORDER BY NO_COVERED DESC) "+
                       "WHERE ROWNUM<=1 ";
                break;

         case 4:
		query ="SELECT * FROM ( SELECT S.STUDENT_ID , COUNT(B.BUILDING_ID) NN_COUNT " +
                       "FROM STUDENT S,BUILDING B " +
                       "WHERE SDO_NN(S.STUDENT_LOC,B.BUILDING_LOC,'sdo_num_res=1') ='TRUE' " +
                       "GROUP BY S.STUDENT_ID " +
                       "ORDER BY NN_COUNT DESC ) " +
                       "WHERE ROWNUM <= 5";
                break;

         case 5:
		query ="SELECT T.X,T.Y " +
                       "FROM  ( SELECT SDO_AGGR_MBR( BUILDING_LOC )  AS MBR FROM " +
                       "( SELECT B.BUILDING_ID,B.BUILDING_LOC " +
                       "FROM BUILDING B " +
                       "WHERE BUILDING_NAME LIKE 'SS%' ) ) B, " +
                       "TABLE(SDO_UTIL.GETVERTICES( B.MBR ) ) T " ;
                break;

         default:
                 System.out.println("Invalid Query Number.Query Range 1-5");

        }

        try{
        Statement myStmnt = myConn.createStatement();
        ResultSet myRs= myStmnt.executeQuery(query);

         switch(query_no){

         case 1:
                 

                  System.out.println(" Students");
                  System.out.println("------------");

                  while (myRs.next()){
                     System.out.println(myRs.getString("STUDENT_ID"));         
                  }

                  ResultSet myRs2= myStmnt.executeQuery(query2); 

                  System.out.println("\n Buildings");
                  System.out.println("------------");

                  while (myRs2.next()){
                     System.out.println(myRs2.getString("BUILDING_ID"));         
                  }

                break;
         case 2:
                 System.out.println("Student   Tramstop");
                 System.out.println("------------------------");
                 while (myRs.next()){
                 System.out.print(myRs.getString("STUDENT_ID")+"          ");  
                 System.out.println(myRs.getString("TRAMSTOP_ID"));        
                 }
                break;
         case 3:
                while (myRs.next()){
                 System.out.println(myRs.getString("TRAMSTOP_ID"));        
                 }
                break;
         case 4:
                 System.out.println("STUDENT   NN_COUNT");
                 System.out.println("-------------------");
                while (myRs.next()){
                 System.out.print(myRs.getString("STUDENT_ID")+"          "); 
                 System.out.println(myRs.getString("NN_COUNT"));       
                 }
                break;
         case 5:
                 System.out.println("X          Y");
                 System.out.println("--------------");
                while (myRs.next()){
                 System.out.print(myRs.getString("X")+"       "); 
                 System.out.println(myRs.getString("Y"));       
                 }

                break;
         default:
                 System.out.println("Invalid Query Number.Query Range 1-5");

         }

        }catch(Exception ex){
         System.out.println(ex);
       } 
      }
    
    }












