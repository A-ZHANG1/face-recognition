/*
 * connection.java
 * 
 * 
 */
package frcopy;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import static java.lang.Math.sqrt;
import static java.lang.System.exit;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import static javax.swing.JOptionPane.showMessageDialog;


public class connection {
    
    
    
     private Connection con;//to connect to mysql database
    private Statement st;//to execute queries
    private ResultSet rs;//to hold returned results
    private PreparedStatement pstmt;
    Blob blob;
    
    public connection() throws SQLException{
    try{
    Class.forName("com.mysql.jdbc.driver");
    }
    catch(Exception e){
       //  System.out.println("abc");
        System.out.println(e);
        //exit(0);
       // System.out.println("abc");
        
    }
        try {
            //con= DriverManager.getConnection("jdbc:mysql://localhost:3306/face","root","mysql123");//"/hello"
        	con= DriverManager.getConnection("jdbc:mysql://localhost:3306/face","root","");//"/hello"
            st=con.createStatement();
            System.out.println(" done");
            
        } catch (SQLException ex) {
            Logger.getLogger(connection.class.getName()).log(Level.SEVERE, null, ex);
            showMessageDialog(null,"Please ensure your database connection first");
            exit(0);
        }
        avg();
    
    }

   public void putdata(String name, ArrayList<File> picfiles,int num) {

        
        try{
		   
		  st = con.createStatement();
		   
		  for(int i=0;i<=num;i++){
          File imgfile = picfiles.get(i);
		  FileInputStream fin = new FileInputStream(imgfile);
		  System.out.println("123");
		   PreparedStatement pre =
		   con.prepareStatement("insert into testset1(Facename,FaceImage) values(?,?)");
		 
		  // pre.setDouble(1,"");
		   pre.setString(1,name);
		   pre.setBinaryStream(2,(InputStream)fin,(int)imgfile.length());
		   pre.executeUpdate();
		   System.out.println("Successfully inserted the file into the database!");

		   pre.close();
        }
		   con.close(); 
		}catch (Exception e1){
			System.out.println(e1.getMessage());
                        
		} 
        
    }

    private void avg() throws SQLException {
        int i,j,gray;
DatabaseMetaData dbm = con.getMetaData();
// check if "employee" table is there
ResultSet tables = dbm.getTables(null, null, "average", null);
if (tables.next()) {
  // Table exists
    
    
           System.out.println("table exists ");
}
else {
    String query="create table if not exists average( averageimage longblob, number INT(6) )";
         PreparedStatement pstmt = con.prepareStatement(query);
            // set parameter;
            //pstmt.setInt(1, candidateId);
            boolean t= pstmt.execute();
            
             float[][] avg=new float[280][280];
     
        try {
 BufferedImage theImage = new BufferedImage(250, 250, BufferedImage.TYPE_BYTE_GRAY);
System.out.println("chiryo");
    for( i=0; i<250; i++) {
         
        for( j=0; j<250; j++) {
            gray=0;//System.out.println(avg[i][j]);
            avg[i][j] = ((gray<<16) |(gray<<8) | gray  );
            theImage.setRGB(i,j, (int) avg[i][j]);
                    }
    }
    File output = new File("GrayScale.jpg");
    ImageIO.write(theImage, "jpg", output);
    System.out.println("banaiyo");
    File imgfile = new File("GrayScale.jpg");
        //BufferedImage image = ImageIO.read(imgfile);
    FileInputStream fin = new FileInputStream(imgfile);
    String query1="insert into average (averageimage, number) values(?,?)";
          pstmt = con.prepareStatement(query1);
        
		   pstmt.setBinaryStream(1,(InputStream)fin,(int)imgfile.length());
                    pstmt.setInt(2,0);
		         pstmt.executeUpdate();
                       // System.out.println(a);
		   System.out.println("Successfully operated into the database!");
            // set parameter;
            //pstmt.setInt(1, candidateId);
          //  boolean t= pstmt.execute();
}

catch(Exception e) {}
     
  // Table does not exist
}
    
       
             

// throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    
 
    void putaverage(double[][] average) throws FileNotFoundException, SQLException {
           File theDir = new File("temporary");
if (!theDir.exists()) {
        boolean result = false;

    try{
        theDir.mkdir();
        result = true;
    } 
    catch(SecurityException se){
        //handle it
    }        
}
      try {
    PrintWriter writer = new PrintWriter(new File("temporary/average.txt"));

    for(int i=0; i<average.length; i++){
        for(int j=0; j<average[0].length; j++){

          //use this if your array has (int,double..)
              // writer.write(String.valueOf(a[i][j])+" "); //Here you parse the int from array to String.


           //use this if your array has String
             writer.write(average[i][j]+" "); //Its String so you dont have to use String.valueOf(something(int,double,...)
        }
       writer.println(); //leave one line 
    }

    writer.flush();  //flush the writer
    writer.close();  //close the writer      



   } catch (FileNotFoundException e) {      
     e.printStackTrace();
      
   }

      try{
        File imgfile = new File("temporary/average.txt");
		  FileInputStream fin = new FileInputStream(imgfile);
		 System.out.println("123");
		   PreparedStatement pre =
		   con.prepareStatement("insert into average (averageimage) values(?)");
		 
		  // pre.setDouble(1,"");
		  // pre.setString(2,name);
		   pre.setBinaryStream(1,(InputStream)fin,(int)imgfile.length());
		         pre.executeUpdate();
                       // System.out.println(a);
		   System.out.println("Successfully inserted the average file into the database!");
                   
                    pre =
		   con.prepareStatement("DELETE FROM `average` WHERE id NOT IN ( \n" +
                                        "  SELECT id \n" +
                                        "  FROM ( \n" +
                                        "    SELECT id \n" +
                                        "    FROM `average` \n" +
                                        "    ORDER BY id DESC \n" +
                                        "    LIMIT 1\n" +
                                        "  )  x\n" +
                                        ")");
		        pre.executeUpdate();
      }
      catch (Exception e1){
			System.out.println(e1.getMessage()+" unable");
                        
		} 
         deleteDirectory(theDir);
    
    
    
    
    
    }

    void putweight(double[][] weightvector,String[] name,int[] faceid) throws FileNotFoundException, SQLException {
         File theDir = new File("temporary");
if (!theDir.exists()) {
        boolean result = false;

    try{
        theDir.mkdir();
        result = true;
    } 
    catch(SecurityException se){
        //handle it
    }        
}
        PreparedStatement pre =con.prepareStatement("select * from weight where 1");
                 // System.out.println("ghanta jasto");
                  rs = pre.executeQuery();
                   
               int id=0;
            while (rs.next()) {
               id=rs.getInt(1);                           
            }
                  
            System.out.println(id);
        
        
        
        for(int k=0;k<weightvector[0].length;k++)
        {
              try {
    PrintWriter writer = new PrintWriter(new File("temporary/weightvector.txt"));

    for(int i=0; i<weightvector.length; i++){
        

          //use this if your array has (int,double..)
              // writer.write(String.valueOf(a[i][j])+" "); //Here you parse the int from array to String.


           //use this if your array has String
             writer.write(weightvector[i][k]+" "); //Its String so you dont have to use String.valueOf(something(int,double,...)
       
       //writer.println(); //leave one line 
    }

    writer.flush();  //flush the writer
    writer.close();  //close the writer      



   } catch (FileNotFoundException e) {      
     e.printStackTrace();
   }

              try{
                        
        File imgfile = new File("temporary/weightvector.txt");
		  FileInputStream fin = new FileInputStream(imgfile);
		
		   pre =
		   con.prepareStatement("insert into weight (name,faceid,weightvector) values(?,?,?)");
		 
		   pre.setDouble(2,faceid[k]);
		   pre.setString(1,name[k]);
		   pre.setBinaryStream(3,(InputStream)fin,(int)imgfile.length());
		         pre.executeUpdate();
                       // System.out.println(a);
		   System.out.println("Successfully inserted the weight file into the database!");
                   
                      pre =
		   con.prepareStatement("DELETE FROM `weight` WHERE id <= ?");
                      pre.setInt(1, id);
		        pre.executeUpdate();
                   
              }
              catch (Exception e1){
			System.out.println(e1.getMessage());
                        
		} 
         
    
    

        
        }
        
    boolean a=deleteDirectory(theDir);
    }

    void puteigen(double[][] eigTnormal) {
          File theDir = new File("temporary");
if (!theDir.exists()) {
        boolean result = false;

    try{
        theDir.mkdir();
        result = true;
    } 
    catch(SecurityException se){
        //handle it
    }        
} 
        
        
        
                    try {
    PrintWriter writer = new PrintWriter(new File("temporary/eigen.txt"));

    for(int i=0; i<eigTnormal.length; i++){
        for(int j=0; j<eigTnormal[i].length; j++){

          //use this if your array has (int,double..)
              // writer.write(String.valueOf(a[i][j])+" "); //Here you parse the int from array to String.


           //use this if your array has String
             writer.write(eigTnormal[i][j]+" "); //Its String so you dont have to use String.valueOf(something(int,double,...)
        }
       writer.println(); //leave one line 
    }

    writer.flush();  //flush the writer
    writer.close();  //close the writer      



   } catch (FileNotFoundException e) {      
   }       
      
                    
                    
                     try{
		   
		    st = con.createStatement();
		   System.out.println("here");
		  
                      File imgfile = new File("temporary/eigen.txt");
		  FileInputStream fin = new FileInputStream(imgfile);
		 
		   PreparedStatement pre =
		   con.prepareStatement("insert into eigenfaces(Eigenface) values(?)");
		 System.out.println("123");
		  // pre.setDouble(1,"");
		  // pre.setString(1,name);
		   pre.setBinaryStream(1,(InputStream)fin,(int)imgfile.length());
                   System.out.println("123");
		   pre.executeUpdate();
		   System.out.println("Successfully inserted the eigen file into the database!");

		   pre.close();
                    pre = con.prepareStatement("DELETE FROM `eigenfaces` WHERE id NOT IN ( \n" +
                                        "  SELECT id \n" +
                                        "  FROM ( \n" +
                                        "    SELECT id \n" +
                                        "    FROM `eigenfaces` \n" +
                                        "    ORDER BY id DESC \n" +
                                        "    LIMIT 1\n" +
                                        "  )  x\n" +
                                        ")");
		        pre.executeUpdate();
        
		  // con.close(); 
		}catch (Exception e1){
			System.out.println(e1.getMessage());
                        
		} 
         
    boolean a=deleteDirectory(theDir);
    }

    double[][] geteigenmatrix() {
        double[][] eig = null;
        int a=0;
         try
             {
        String selectSQL = "SELECT * FROM `testset1` WHERE 1";
              pstmt = con.prepareStatement(selectSQL);
            rs = pstmt.executeQuery();
            
            
            while (rs.next()) {
                a++;}
             }
         catch (Exception e){
          System.out.println("error in query execution i suppose");}
       
        
        
        
        
        
        
       try
        {
        String selectSQL = "SELECT * FROM `eigenfaces` WHERE 1";
        
                 pstmt = con.prepareStatement(selectSQL);
             
            rs = pstmt.executeQuery();
        
            while (rs.next()) {
              
                blob = rs.getBlob(2);
               
            }
           
            byte[] bdata = blob.getBytes(1, (int) blob.length());
            
            String s = new String(bdata);
         
            String[] words = s.split(" ");
             
        double[] no = new double[words.length-1];
for (int i = 0; i < (words.length-1); i++) {
      no[i]=Double.parseDouble(words[i]);
}
          int j=  (words.length-1)/a;
         
      eig=new double[a][j];
   
for(int i=0;i<a;i++)
{
    for(int k=0;k<j;k++)
    {   
        eig[i][k]=no[j*i+k];
    }
}
      
    }
          
          catch (Exception e){
          System.out.println("error in query execution i suppose");}
       
   
        return eig;
                      //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    double[][] getweightmatrix() {
double weightvector[][]=null;
        try
             {
        String selectSQL = "SELECT * FROM `weight` WHERE 1";
              pstmt = con.prepareStatement(selectSQL);
            rs = pstmt.executeQuery();
            
            int j=0;int a=0;
            while (rs.next()) {
                a++;}
            
            System.out.println("tang tung dhis");
            System.out.println(a);
         // a counts the number of weightvectors present 
            
            rs.beforeFirst();
            int eigenvecno;
            if((1*a)>70)
         {eigenvecno=70;
             
         }
         else
         {
         eigenvecno=a;}
            weightvector = new double[eigenvecno][a];
            while (rs.next()) {
              
                blob = rs.getBlob(4);
              
                byte[] bdata = blob.getBytes(1, (int) blob.length());
            
                String s = new String(bdata);
                String[] words = s.split(" ");
                
                
                for (int i = 0; i < (words.length); i++) { 
       weightvector[i][j]=Double.parseDouble(words[i]);   // j is the image number remember that
                    }
               
            j++;
            }
   
    
    }
          
          catch (SQLException | NumberFormatException e){
          System.out.println("error in query execution i suppose");}
        return weightvector;       
// throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    double[][] getaveragematrix() {
       
        
        double[][] avg = null;
        try
        {
        String selectSQL = "SELECT * FROM `average` WHERE 1";
            pstmt = con.prepareStatement(selectSQL);
            rs = pstmt.executeQuery();
                while (rs.next()) {

                blob = rs.getBlob(1);
              
            }
            
            byte[] bdata = blob.getBytes(1, (int) blob.length());
            String s = new String(bdata);
       
            String[] words = s.split(" ");
        double[] no = new double[words.length-1];
for (int i = 0; i < (words.length-1); i++) {
   // no[i]=0;
    no[i]=Double.parseDouble(words[i]);
}
int j=  (int) sqrt(words.length-1);
      avg=new double[j][j];
for(int i=0;i<j;i++)
{
    for(int k=0;k<j;k++)
    {   
        avg[i][k]=no[280*i+k];
    }
}
    
        }
          
          catch (Exception e){
          System.out.println("error in query execution i suppose");}
        return avg;
// throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    String[] getname() {
        String name[] = null;
        try
             {
        String selectSQL = "SELECT * FROM `weight` WHERE 1";
              pstmt = con.prepareStatement(selectSQL);
            rs = pstmt.executeQuery();
            
            int j=0;int a=0;
            while (rs.next()) {
                a++;}
            
         // a counts the number of weightvectors present 
            name=new String [a];
            rs.beforeFirst();
            
            while (rs.next()) {
                name[j]=rs.getString(2);
                j++;
              }
   
    
    }
          
          catch (SQLException | NumberFormatException e){
          System.out.println("error in query execution i suppose");}
        
        
        
        return name;//throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
     
    
    
    
    
    
    
    
    
    public static boolean deleteDirectory(File directory) {
    if(directory.exists()){
        File[] files = directory.listFiles();
        if(null!=files){
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                }
                else {
                    files[i].delete();
                }
            }
        }
    }
    return(directory.delete());
}

    double[][] getdiffarrange(double[][] average) throws SQLException, IOException {
        
//long startTime = System.currentTimeMillis();
    double diffarrange[][] = null;
//=new double[280][280];//average image
    
    
        
        String selectSQL = "SELECT * FROM testset1 WHERE 1";
        // System.out.println("conn done");
        
        try  {
         
                 pstmt = con.prepareStatement(selectSQL);
           
            rs = pstmt.executeQuery();
            
            String filename;

            int a=0;
            while (rs.next()) {
                //System.out.println(a);
                a++;}
            
         
            rs.beforeFirst();
          //  String name[]=new String[a];
           // int faceid[]=new int[a];
            double[][][]images=new double[a][280][280];//images
            double[][][]diff=new double[a][280][280];//image-average
            diffarrange=new double[280*280][a];//diff arranged as column 
            
            int aa=0;
         
            
            while (rs.next()) {
                
               //  File fw = new File("F:\\face.jpg");
                // System.out.println("haha");
               Blob bloba = rs.getBlob(3);
           //    name[aa]=rs.getString(2);
             //  faceid[aa]=rs.getInt(1);
               
              // System.out.println("haha");
                InputStream stream = bloba.getBinaryStream();
                //System.out.println("haha");
                BufferedImage image = ImageIO.read(stream);
                
                
    for (int x = 0; x < 280; ++x)
    {
    for (int y = 0; y < 280; ++y)
    {
        int rgb = image.getRGB(x, y);
        
        int b = (rgb & 0xFF);
        
        
        images [aa][x][y]=b;
      
    }
    }     
               
              //  ImageIO.write(image, "jpg", fw);
                aa++;
            }//System.out.println(a+" ding bhayo");
     
    for (int z = 0; z < a; ++z){
    for (int x = 0; x < 280; ++x){
    for (int y = 0; y < 280; ++y)
    {
        
        diff[z][x][y]=images[z][x][y]-average[x][y];
    }
    } 
    }
           
           
           
        int k=0;
            while(k<a)
            {//fun(k,d,a);
                int l=0;
            for(int i=0;i<280;i++)
        {
            for(int j=0;j<280;j++)
            { diffarrange[l][k]=diff[k][i][j];
            diffarrange[l][k]=(double)Math.round(diffarrange[l][k] * 100000d) / 100000d;
            l++;
            }
         }  
            k++;}
    
        }
        catch (SQLException | NumberFormatException e){
          System.out.println("error in query execution i suppose");}
        
        
   return diffarrange;
//throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
}
/*Row size too large (> 8126). 
Changing some columns to TEXT or BLOB 
or using ROW_FORMAT=DYNAMIC or 
ROW_FORMAT=COMPRESSED may help.
In current row format, BLOB prefix of 768 bytes is stored inline.
  */
  
  /*
 *FRcopy.java
 */
package frcopy;

import java.io.IOException;
import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JFrame;

/**
 *
 * @author Kush
 */
public class FRcopy {
double[][] average=new double[280][280];//average image
    
    private Connection con;//to connect to mysql database
    private Statement st;//to execute queries
    private ResultSet rs;//to hold returned 
    
    private PreparedStatement pstmt;
    int eigenvecno;
    /**
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     * @throws java.sql.SQLException
     */
    public static void main(String[] args) throws InterruptedException, SQLException, IOException {
        
        connection db=new connection();  

       
       // double[][] eigen=db.geteigenmatrix();
        double[][] weight = db.getweightmatrix();
        //double[][] average = db.getaveragematrix();
        
       /* if(eigen== null || weight==null || average==null)
        {  register f = new register();
              
              
		f.setVisible(true);
              f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }*/
        if(weight==null)
        {  register f = new register();
              
              
		f.setVisible(true);
              f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }
             else
        {
              recognize f = new recognize();
              // inserttry in=new inserttry();
              
		f.setVisible(true);
              f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }
      //  inserttry in=new inserttry();
      //train a=new train();
		
        // TODO code application logic here
        
        /*register f = new register();
		f.setVisible(true);
              f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);*/
    }
    
}
/*
 * insert.java
 */
package frcopy;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JOptionPane;


public class insert {
    private Connection con;//to connect to mysql database
    private Statement st;//to execute queries
    private ResultSet rs;//to hold returned results
    int num;
    insert(String name) throws SQLException 
    {   
         File file = new File("temp\\");
       
        if (file.isDirectory()) { //checking if temp exists which saves our faces temporarily
           
            String[] files = file.list();

            if (files.length > 0) {
                num=files.length;
               // System.out.println("The " + file.getPath() + " is not empty!");
          
       if(      name == null)
       {   
            name= JOptionPane.showInputDialog("Enter the face name ");
       } else {
        File f = new File("C:\\TestFolder\\");
        ArrayList<File> picfiles = new ArrayList<>(Arrays.asList(f.listFiles()));
        connection put=new connection();
         put.putdata(name,picfiles,num);
         boolean a=deleteDirectory(f);
      
       
       }
       
         }
        }
        
    }
    public static boolean deleteDirectory(File directory) {
    if(directory.exists()){
        File[] files = directory.listFiles();
        if(null!=files){
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                }
                else {
                    files[i].delete();
                }
            }
        }
    }
    return(directory.delete());
}

   public void putdata(String name, ArrayList<File> picfiles,int num) {

        
        try{
		   
		    st = con.createStatement();
		   
		  for(int i=0;i<=num;i++){
                      File imgfile = picfiles.get(i);
		  FileInputStream fin = new FileInputStream(imgfile);
		 System.out.println("123");
		   PreparedStatement pre =
		   con.prepareStatement("insert into testset1 'Facename' 'FaceImage' values(?,?)");
		 
		  // pre.setDouble(1,"");
		   pre.setString(2,name);
		   pre.setBinaryStream(3,(InputStream)fin,(int)imgfile.length());
		         pre.executeUpdate();
                       // System.out.println(a);
		   System.out.println("Successfully inserted the file into the database!");
                   
             

		   pre.close();
        }
		   con.close(); 
		}catch (Exception e1){
			System.out.println(e1.getMessage());
                        
		} 
               
    }
    
}
package frcopy;


import Jama.Matrix;
import static frcopy.recognize.eigen;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import static java.lang.Math.round;
import static java.lang.Math.sqrt;
import static java.lang.System.exit;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import static javax.swing.JOptionPane.showMessageDialog;

/*
 * inserttry.java
 */

public class inserttry {
   static ImageIcon icon;
   private Connection con;//to connect to mysql database
    private Statement st;//to execute queries
    private ResultSet rs;//to hold returned results
    private PreparedStatement pstmt;
    Blob blob;
static double eigen[][];
static double realeigen[][];
static double diffarrange[][];
static double weight[][];
static double average[][];
static double currentfaceweight[][];
static String namelist[];
static String name;
static double threshold=7000;
connection conn;
//double currentimage[][];

  public static void matrixretrieval() throws SQLException, IOException
  {
       connection conn = new connection();
       eigen=conn.geteigenmatrix();
       weight=conn.getweightmatrix();
       average=conn.getaveragematrix();
       diffarrange=conn.getdiffarrange(average);
       namelist=conn.getname();
       
       Matrix M=new Matrix(diffarrange);
       Matrix eigenprimary=new Matrix(eigen);
       
            Matrix eigensec=M.times(eigenprimary);
            realeigen=eigensec.getArray();
            
             
             int k=weight.length;
             System.out.println(k);
             double normfact[]=new double[k];
            
            for(int i=0;i<k;i++)
          { 
              normfact[i]=0;
          }
          for(int i=0;i<k;i++)
          { 
            for(int j=0;j<(280*280);j++)
            {
               normfact[i]=normfact[i]+(realeigen[j][i]*realeigen[j][i]); 
            }
          }
          
//          System.out.println(normfact[7]);
         double problem=0;
          for(int i=0;i<(280*280);i++)
          { 
            for(int j=0;j<k;j++)
            {
               realeigen[i][j]=(realeigen[i][j])/sqrt(normfact[j]); 
               
               problem=problem+realeigen[i][j];
            }
          }
             
           eigensec=new Matrix(realeigen);
            Matrix eigensecT=eigensec.transpose(); 
            realeigen=eigensecT.getArray();
             
          //   inserttry ins=new inserttry();
            // ins.form(weight,realeigen,average);
                 
  }
    
    public inserttry() throws SQLException, IOException{
        matrixretrieval();
    try{
    Class.forName("com.mysql.jdbc.driver");
    }
    catch(Exception e){
       //  System.out.println("abc");
        System.out.println(e);
        //exit(0);
       // System.out.println("abc");
        
    }
        try {
            con= DriverManager.getConnection("jdbc:mysql://localhost:3306/face","root","");
            st=con.createStatement();
            System.out.println(" done");
            
        } catch (SQLException ex) {
            Logger.getLogger(connection.class.getName()).log(Level.SEVERE, null, ex);
            showMessageDialog(null,"Please ensure your database connection first");
            exit(0);
        }
        
        
         
double weightvector[][]=null;
        try
             {
        String selectSQL = "SELECT * FROM `weight` WHERE 1";
              pstmt = con.prepareStatement(selectSQL);
            rs = pstmt.executeQuery();
            
            int j=0;int a=0;
            while (rs.next()) {
                a++;}
            
         // a counts the number of weightvectors present 
            
            rs.beforeFirst();
            int eigenvecno;
            if((0.7*a)>50)
         {eigenvecno=50;
             
         }
         else
         {
         eigenvecno=a;}
            weightvector = new double[eigenvecno][a];
            while (rs.next()) {
              
                blob = rs.getBlob(4);
              
                byte[] bdata = blob.getBytes(1, (int) blob.length());
            
                String s = new String(bdata);
                String[] words = s.split(" ");
                
                
                for (int i = 0; i < (words.length); i++) { 
       weightvector[i][j]=Double.parseDouble(words[i]);   // j is the image number remember that
                    }
               
            j++;
            }
   
    
    }
          
          catch (SQLException | NumberFormatException e){
          System.out.println("error in query execution i suppose");}
      int a=0;
        for(int i=0;i<(weight[0].length-1);i++)
                   {    double diff=0;double distance=0;
                       for(int j=0;j<weight.length-1;j++)
                       {
                           diff=diff+Math.pow((weight[j][i]-weight[j][i+1]), 2);
                           
                          
                        
                       }
                       distance=sqrt(diff);
                         a++;
                       System.out.println(distance);
                          System.out.println(a);
                   }
        
        
        
     a=8;
    int eigenvecno=a;
        double[][] finalobt=new double[a][(280*280)];
          //System.out.println("1st "+eigTnormal[3][500]+" no turu "+weightvector[3][0]);
          //System.out.println("2nd "+eigTnormal[2][500]+" no turu "+weightvector[2][0]);
        for(int i=0;i<a;i++)
         {   
         for(int j=0;j<eigenvecno;j++)
         {
             for(int ku=0;ku<(280*280);ku++)
             {
                 finalobt[i][ku]=finalobt[i][ku]+realeigen[j][ku]*weight[j][i];
             }
         }
         }
         
           
          // Matrix thisone=new Matrix(img1);
         //  Matrix first=thisone.transpose();
          // double thisonne[][]=thisone.getArray();
           double img[][][]=new double[a][280][280];
          
           
           for(int z=0;z<a;z++)
           {
           for(int i=0;i<280;i++)
         {
             for(int j=0;j<280;j++)
             {
                img[z][i][j]=finalobt[z][(i*280+j)]+average[i][j];
             }
         }
           }
          // System.out.println("  ");
           //System.out.println(images[1][1][1]+" "+img[1][1]);
             //         System.out.println("  ");

           try {
 BufferedImage theImage = new BufferedImage(280, 280, BufferedImage.TYPE_BYTE_GRAY);
    for(int i=0; i<280; i++) {
         
        for(int j=0; j<280; j++) {
         double   grayn=round(img[0][i][j]);//System.out.println(avg[i][j]);
         int gray=(int)grayn;
            int grayt = ((gray<<16) |(gray<<8) | gray  );
            theImage.setRGB(i,j,grayt);
                    }
    }
    File output = new File("F://img12.jpg");
    ImageIO.write(theImage, "jpg", output);
    //System.out.println("banaiyo");
           } catch (Exception e){}
           
          // double d=((int)img[0][20][204] );
      //     System.out.println("hell "+weightvector[0][0]+" no "+weightvector[0][1]+" yes"+weightvector[0][2]);
           
            // yaha samma
            long stopTime = System.currentTimeMillis();
           // long elapsedTime = stopTime - startTime;
    //  System.out.println(elapsedTime);
       // System.out.println(a+" eti "+eigenvecno);
       //  System.out.println(eigvectuseful.length+" eti "+eigvectuseful[0].length);    
     

        
        
//    boolean a=deleteDirectory(f);
    
    //temp folder delete hanne 
        //connection conn=new connection();
        
        
        /*
        double[][] finalobt=new double[36][(280*280)];
          //System.out.println("1st "+eigTnormal[3][500]+" no turu "+weightvector[3][0]);
          //System.out.println("2nd "+eigTnormal[2][500]+" no turu "+weightvector[2][0]);
         for(int i=0;i<36;i++)
         {   
         for(int j=0;j<(280*280);j++)
         {
             
                 finalobt[i][j]=0;
             }
         }
        
        for(int i=0;i<36;i++)
         {   
         for(int j=0;j<36;j++)
         {
             for(int ku=0;ku<(280*280);ku++)
             {
                 finalobt[i][ku]=finalobt[i][ku]+eigen[j][ku]*weight[j][i];
             }
         }
         }
         
           
          // Matrix thisone=new Matrix(img1);
         //  Matrix first=thisone.transpose();
          // double thisonne[][]=thisone.getArray();
           double img[][][]=new double[36][280][280];
          
           
           for(int z=0;z<36;z++)
           {
           for(int i=0;i<280;i++)
         {
             for(int j=0;j<280;j++)
             {
                img[z][i][j]=finalobt[z][(i*280+j)]+average[i][j];
             }
         }
           }
          // System.out.println("  ");
           //System.out.println(images[1][1][1]+" "+img[1][1]);
             //         System.out.println("  ");

           try {
 BufferedImage theImage = new BufferedImage(280, 280, BufferedImage.TYPE_BYTE_GRAY);
    for(int i=0; i<280; i++) {
         
        for(int j=0; j<280; j++) {
         double   grayn=round(img[6][i][j])+50;//System.out.println(avg[i][j]);
         int gray=(int)grayn;
            int grayt = ((gray<<16) |(gray<<8) | gray  );
            theImage.setRGB(i,j,grayt);
                    }
    }
    File output = new File("F://img17.jpg");
    ImageIO.write(theImage, "jpg", output);
    //System.out.println("banaiyo");
           } catch (Exception e){}
           
           double d=((int)img[0][20][204] );
      //     System.out.println("hell "+weightvector[0][0]+" no "+weightvector[0][1]+" yes"+weightvector[0][2]);
           
        
        /*
        double no[][]=null;
        try
        {
        String selectSQL = "SELECT * FROM `weight` WHERE 1";
        System.out.println("here i amjfkd");
          
           // Connection conn = DriverManager.getConnection();
                 pstmt = con.prepareStatement(selectSQL);
                 
            // set parameter;
            //pstmt.setInt(1, candidateId);
            rs = pstmt.executeQuery();
            
            
            String name;
            int faceid;
            int j=0;int a=0;
            while (rs.next()) {
              
                a++;}
            
         // a counts the number of images in testset 
            
            rs.beforeFirst();
            
            while (rs.next()) {
                
               //  File fw = new File("F:\\face.jpg");
                // System.out.println("haha");
                blob = rs.getBlob(4);
               //name=rs.getString(2);
              // faceid=rs.getInt(2);
                byte[] bdata = blob.getBytes(1, (int) blob.length());
            
            String s = new String(bdata);
             String[] words = s.split(" ");
             no = new double[words.length][a];
             for (int i = 0; i < (words.length); i++) {
    
      
        
        
       no[i][j]=Double.parseDouble(words[i]);   // j is the image number remember that
      // 
       System.out.println("tori");
       
       
}
               
            j++;
            }
   
    
    }
          
          catch (Exception e){
          System.out.println("error in query execution i suppose");}
          
        */
    
    }

    
}

/*
 * Recognize.java
 */
package frcopy;

import Jama.Matrix;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.Math.sqrt;
import static java.lang.System.exit;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;


public class recognize extends javax.swing.JFrame {
static ImageIcon icon;
static double eigen[][];
static double realeigen[][];
static double diffarrange[][];
static double weight[][];
static double average[][];
static double currentfaceweight[][];
static String namelist[];
static String name;
static double threshold=5400;
connection conn;
//double currentimage[][];

  public static void matrixretrieval() throws SQLException, IOException
  {
       connection conn = new connection();
       eigen=conn.geteigenmatrix();
       weight=conn.getweightmatrix();
       average=conn.getaveragematrix();
       diffarrange=conn.getdiffarrange(average);
       namelist=conn.getname();
       
       Matrix M=new Matrix(diffarrange);
       
       
      // if(eigen!=null)
    	   Matrix eigenprimary=new Matrix(eigen);
       
            Matrix eigensec=M.times(eigenprimary);
            realeigen=eigensec.getArray();
            
             
             int k=weight.length;
             System.out.println(k);
             System.out.println("hello");
             double normfact[]=new double[k];
              
            for(int i=0;i<k;i++)
          { 
              normfact[i]=0;
          }
          for(int i=0;i<k;i++)
          { 
            for(int j=0;j<(280*280);j++)
            {
               normfact[i]=normfact[i]+(realeigen[j][i]*realeigen[j][i]); 
            }
          }
          
//          System.out.println(normfact[7]);
         double problem=0;
          for(int i=0;i<(280*280);i++)
          { 
            for(int j=0;j<k;j++)
            {
               realeigen[i][j]=(realeigen[i][j])/sqrt(normfact[j]); 
               
               problem=problem+realeigen[i][j];
            }
          }
             
           eigensec=new Matrix(realeigen);
            Matrix eigensecT=eigensec.transpose(); 
            realeigen=eigensecT.getArray();
             
             //inserttry ins=new inserttry();
            // ins.form(weight,realeigen,average);
             
             
             
   System.out.println("hello");    
  }

   
  
  
   double[][] imageacquire() {
    int width = 280;    //width of the image
    int height = 280;   //height of the image
    BufferedImage image = null;
    double[][] face=new double[width][height];
    File f = null;
    //File fw = null;
   
    //read image
    try{
      f = new File("face.jpg"); //image file path 
      //image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
      image = ImageIO.read(f);
      while(image==null)
      {image = ImageIO.read(f);}
       BufferedImage outputImage = new BufferedImage(280, 280, image.getType());
 
        // scales the input image to the output image
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(image.getScaledInstance(280, 280, Image.SCALE_SMOOTH), 0, 0, 280, 280, null);
        g2d.dispose();
      image=outputImage;
      face=makeGray(image);
     // System.out.println("Reading complete.");
//      ImageIO.write(image, "jpg", fw);
    }catch(IOException e){
      System.out.println("Error: "+e);
    }
    return face;
  }
  
  public static double[][] makeGray(BufferedImage img)
{       double face[][]=new double[280][280];
    for (int x = 0; x < img.getWidth(); ++x)
    {
    for (int y = 0; y < img.getHeight(); ++y)
    {
        int rgb = img.getRGB(x, y);
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = (rgb & 0xFF);

        int grayLevel = (int) (0.2126*r + 0.7152*g + 0.0722*b);
        face[x][y]=grayLevel;
        //int gray = (grayLevel << 16) + (grayLevel << 8) + grayLevel; 
        //img.setRGB(x, y, gray);
    }
    }
    /*ColorConvertOp colorConvert = 
        new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
    colorConvert.filter(img,img);*/
        return face;
}



    private static BufferedImage ConvertMat2Image(Mat frame) {
	
		
		MatOfByte mem = new MatOfByte();
		
		Imgcodecs.imencode(".jpg", frame, mem);
		
		byte[] byteArray = mem.toArray();
		BufferedImage buff = null;
		try {
			InputStream in = new ByteArrayInputStream(byteArray);
			buff = ImageIO.read(in);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return buff;
	}
    
    public recognize() throws SQLException, IOException {
        matrixretrieval();
        initComponents(); 
       new Thread(new Runnable() {
    public void run() {
        try {
            dothis();
        } catch (SQLException | IOException ex) {
            Logger.getLogger(recognize.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}).start(); 
        
    }
    
    public void dothis() throws SQLException, IOException
    {
             System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
				CascadeClassifier cascadeFaceClassifier = new CascadeClassifier(
				"C:\\Users\\kush\\Downloads\\Compressed\\OpenCvObjectDetection-master\\haarcascades\\haarcascade_frontalface_default.xml");
		
		VideoCapture videoDevice = new VideoCapture();
		videoDevice.open(0);
		if (videoDevice.isOpened()) {
                                  	do {		
				Mat frameCapture = new Mat();
                                
                               
				videoDevice.read(frameCapture);
				Graphics g = jPanel1.getGraphics();
				MatOfRect faces = new MatOfRect();
				cascadeFaceClassifier.detectMultiScale(frameCapture, faces);								
				Rect rectCrop=null;
				for (Rect rect : faces.toArray()) {
					
				
                                        Imgproc.rectangle(frameCapture, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
							new Scalar(0, 100, 0),3);
                                        rectCrop = new Rect(rect.x, rect.y, rect.width, rect.height);
                                        Mat faceImage = frameCapture.submat(rect);
                                       Imgcodecs.imwrite("face.jpg", faceImage);
                                       double[][] currentface=new double[280][280];
                                       double[][] currentfacedifference=new double[280][280];
                                       double [][] currentfacediffarranged=new double[280*280][1];
                                       currentface=imageacquire();
                                        for (int x = 0; x < 280; ++x){
                                        for (int y = 0; y < 280; ++y)
                                        {

                                            currentfacedifference[x][y]=currentface[x][y]-average[x][y];
                                        }
                                        } 
                                        
                                        for (int x = 0; x < 280; ++x){
                                        for (int y = 0; y < 280; ++y)
                                        {

                                            currentfacediffarranged[(280*x)+y][0]=currentfacedifference[x][y];
                                        }
                                        } 
                                       // System.out.println();
                                         Matrix diffarrange=new Matrix(currentfacediffarranged);
                                        Matrix eigT=new Matrix(realeigen);
                                        if(eigT.getColumnDimension()== diffarrange.getRowDimension())
                                        {  
                                        Matrix weightofthis=eigT.times(diffarrange);
                                        currentfaceweight=weightofthis.getArray();
                                        
                                         name=findname();
                                        
                                        
                                        
                                       // know thisface=new know();
                                     //   String facename=thisface.facename();
                                        
                                        
                                        // a lot of calls has to be made here ISD perhaps :D
                                        
                                        
                                        }
                                        
                                        else{
                                        name="unknown face";
                                        matrixretrieval();
                                        }
                                        
                                        Imgproc.putText(frameCapture, name, new Point(rect.x,rect.y-5), 1, 2, new Scalar(0,0,255));								
					
			
                                }
                                
				
				Image img2=ConvertMat2Image(frameCapture);
                                
                                icon = new ImageIcon(img2); 
                                jLabel1.setIcon(icon);
                               

		
				//System.out.println(String.format("%s (FACES)detected.", faces.toArray().length));
      }while(true);
			}
		 else {
			System.out.println("Camera not working properly");
			return;
		}
       
      
    
    }
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 740, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE)
                .addContainerGap())
        );

        jButton1.setLabel("Register new face");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setLabel("Exit");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Refresh");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(51, 51, 51)
                .addComponent(jButton3)
                .addGap(41, 41, 41)
                .addComponent(jButton2)
                .addGap(257, 257, 257))
            .addGroup(layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton1)
                    .addComponent(jButton3))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
           JFrame frame = new register();
		frame.setVisible(true);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        exit(0);
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
    try {
        matrixretrieval();
    } catch (SQLException | IOException ex) {
        Logger.getLogger(recognize.class.getName()).log(Level.SEVERE, null, ex);
    }
    }//GEN-LAST:event_jButton3ActionPerformed

    
     //* @param args the command line arguments
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

       public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(recognize.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(recognize.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(recognize.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(recognize.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new recognize().setVisible(true);
                } catch (SQLException | IOException ex) {
                    Logger.getLogger(recognize.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private String findname() {
            String naam;
                
                   for(int i=0;i<weight[0].length;i++)
                   {    double diff=0;double distance=0;
                       for(int j=0;j<weight.length;j++)
                       {
                           diff=diff+Math.pow((currentfaceweight[j][0]-weight[j][i]), 2);
                       }
                           distance=sqrt(diff);
                           //System.out.println(distance);
                           naam=namelist[i];
                           if(distance<=threshold)
                           {  // System.out.println(distance);
                               return naam;
                           }
                       
                   }
                   naam="unknown face";
                
                return naam;
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

/*
 *register.java
 */
package frcopy;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.showMessageDialog;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;


public class register extends javax.swing.JFrame {
static ImageIcon icon;
static connection conn; 
    
    private static BufferedImage ConvertMat2Image(Mat frame) {
	
		
		MatOfByte mem = new MatOfByte();
		
		Imgcodecs.imencode(".jpg", frame, mem);
		
		byte[] byteArray = mem.toArray();
		BufferedImage buff = null;
		try {
			InputStream in = new ByteArrayInputStream(byteArray);
			buff = ImageIO.read(in);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return buff;
	}
    /**
     * Creates new form register
     */
    public register() {
        initComponents();
        new Thread(new Runnable() {
    public void run() {
       dothis();
    }
}).start(); 
    }
public void dothis()
    {
             System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
				CascadeClassifier cascadeFaceClassifier = new CascadeClassifier(
				//"C:\\Users\\kush\\Downloads\\Compressed\\OpenCvObjectDetection-master\\haarcascades\\haarcascade_frontalface_default.xml");
						"C:\\OpenCV-3.2.0\\opencv\\sources\\data\\haarcascades_cuda\\haarcascade_frontalface_default.xml");
		VideoCapture videoDevice = new VideoCapture();
		videoDevice.open(0);
		if (videoDevice.isOpened()) {
                                  	do {		
				Mat frameCapture = new Mat();
                                
                               
				videoDevice.read(frameCapture);
				Graphics g = jPanel1.getGraphics();
				MatOfRect faces = new MatOfRect();
				cascadeFaceClassifier.detectMultiScale(frameCapture, faces);								
				Rect rectCrop=null;
				for (Rect rect : faces.toArray()) {
					
					Imgproc.putText(frameCapture, "Face", new Point(rect.x,rect.y-5), 1, 2, new Scalar(0,0,255));								
					Imgproc.rectangle(frameCapture, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
							new Scalar(0, 100, 0),3);
                                        rectCrop = new Rect(rect.x, rect.y, rect.width, rect.height);
                                        Mat faceImage = frameCapture.submat(rect);
                                        Imgcodecs.imwrite("face.jpg", faceImage);
				}
				
				Image img2=ConvertMat2Image(frameCapture);
                                
                                icon = new ImageIcon(img2); 
                                jLabel1.setIcon(icon);
                               

		
				//System.out.println(String.format("%s (FACES)detected.", faces.toArray().length));
      }while(true);
			}
		 else {
			System.out.println("Camera not working properly");
			return;
		}
       
      
    
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE)
                .addContainerGap())
        );

        jButton1.setLabel("Click");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setLabel("Finish process");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setLabel("Exit");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel2.setText("Note: Click subject's photo when face is detected in varying conditions and press ''Finish process'' to end.");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(58, 58, 58)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 630, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(165, 165, 165)
                        .addComponent(jButton2)
                        .addGap(170, 170, 170)
                        .addComponent(jButton3)))
                .addContainerGap(134, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton3)
                    .addComponent(jButton2))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
     
        imageacquire();
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
         File index = new File("temp");
        boolean a=conn.deleteDirectory(index);
        dispose();
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
          String name= JOptionPane.showInputDialog("Enter the face name ");
    try {
        insert pics=new insert(name);        // TODO add your handling code here:
    } catch (SQLException ex) {
        Logger.getLogger(register.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    
    
    new Thread(new Runnable() {
    public void run() {
       showMessageDialog(null,"Please wait for a moment");
    }
}).start(); 
    
    
    
    
    
    
     new Thread(new Runnable() {
    public void run() {
        
       
     
        try {
        train imageset=new train();
    } catch (SQLException ex) {
        Logger.getLogger(register.class.getName()).log(Level.SEVERE, null, ex);
    }
          
           File index = new File("temp");
        boolean a=conn.deleteDirectory(index);
     
    }
}).start(); 
      dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    //reading the previously saved face
  public void imageacquire() {
    int width = 280;    //width of the image
    int height = 280;   //height of the image
    BufferedImage image = null;
    File f = null;
    File fw = null;
   

// if the directory does not exist, create it
    File theDir = new File("temp");
if (!theDir.exists()) {
        boolean result = false;

    try{
        theDir.mkdir();
        result = true;
    } 
    catch(SecurityException se){
        //handle it
    }        
}
String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
    //read image
    try{
      f = new File("face.jpg"); //image file path
      
      fw = new File("temp\\"+timeStamp+".jpg"); 
      
      //image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
      image = ImageIO.read(f);
      while(image==null)
      {image = ImageIO.read(f);}
       BufferedImage outputImage = new BufferedImage(280, 280, image.getType());
 
        // scales the input image to the output image
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(image.getScaledInstance(280, 280, Image.SCALE_SMOOTH), 0, 0, 280, 280, null);
        g2d.dispose();
      image=outputImage;
      image=makeGray(image);
     // image=equalize(image);
      //System.out.println("Reading complete.");
      ImageIO.write(image, "jpg", fw);
    }catch(IOException e){
      System.out.println("Error: "+e);
    }
  }
  
  public static BufferedImage makeGray(BufferedImage img)
{  int a=0, ba=255, c=0,d=180;
    for (int x = 0; x < img.getWidth(); ++x)
    {
    for (int y = 0; y < img.getHeight(); ++y)
    {
        int rgb = img.getRGB(x, y);
        int r = (rgb >> 16) & 0xFF;
        //r=(r-c)*(ba-a)/(c-d)+a;
        //r=(int) (2.2*r+50);
       // if(r>=255)
         //   r=255;
        int g = (rgb >> 8) & 0xFF;
        //g=(g-c)*(ba-a)/(c-d)+a;
        //g=(int) (2.2*g+50);
        //if(g>=255)
          //  g=255;
        int b = (rgb & 0xFF);
      //  b=(b-c)*(ba-a)/(c-d)+a;
        //b=(int) (2.2*b+50);
        //if(b>=255)
          //  b=255;
        int grayLevel = (int) (0.2126*r + 0.7152*g + 0.0722*b);
        // grayLevel=(grayLevel-c)*(ba-a)/(c-d)+a;
        int gray = (grayLevel << 16) + (grayLevel << 8) + grayLevel; 
        img.setRGB(x, y, gray);
    }
    }
    /*ColorConvertOp colorConvert = 
        new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
    colorConvert.filter(img,img);*/
        return img;
      //  return new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY) ;
}

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(register.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(register.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(register.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(register.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new register().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

    private BufferedImage equalize(BufferedImage bufImg) {
/*       int width =bi.getWidth();
            int height =bi.getHeight();
            int anzpixel= width*height;
            int[] histogram = new int[255];
            int[] iarray = new int[1];
           

            //read pixel intensities into histogram
            for (int x = 1; x < width; x++) {
                for (int y = 1; y < height; y++) {
                    int valueBefore=bi.getRaster().getPixel(x, y,iarray)[0];
                    histogram[valueBefore]++;
                }
            }

            int sum =0;
         // build a Lookup table LUT containing scale factor
            float[] lut = new float[anzpixel];
            for (int i=0; i < 255; ++i )
            {
                sum += histogram[i];
                lut[i] = sum * 255 / anzpixel;
            }

            // transform image using sum histogram as a Lookup table
            for (int x = 1; x < width; x++) {
                for (int y = 1; y < height; y++) {
                    int valueBefore=bi.getRaster().getPixel(x, y,iarray)[0];
                    int valueAfter= (int) lut[valueBefore];
                    iarray[0]=valueAfter;
                     bi.getRaster().setPixel(x, y, iarray); 
                }
            }  




*/

    //Getting information of each pixel;
    int[][] intensity = new int[bufImg.getWidth()][ bufImg.getHeight()];
    int[] counter = new int[256];
    for(int j=0; j < bufImg.getHeight();j++)
        for(int i=0; i < bufImg.getWidth();i++)
        {
            int values=bufImg.getRGB(i,j);              
            Color oldColor = new Color(values);
            intensity[i][j] = oldColor.getBlue();
            counter[intensity[i][j]]++;
        }

    //BEGIN OF Histogram Equalization

    //find out how many rows the table have
    int row=0;

    for(int i=0;i<256;i++)
        if(counter[i]!=0)
            row++;

    //Find out the v column of the table
    //table[row][0] = v column
    //table[row][1] = c column
    int temp=0;
    int[][] table = new int[row][2];


    for(int i=0;i<256;i++)
        if(counter[i]!=0)
        {
            table[temp][0] = i;
            temp++;
        }

    //Find out the c column of the table
    for(int i=0;i<row;i++)
        table[i][1] = counter[table[i][0]];

    //C-> CS

    int sum = 0;

    for(int i=0;i<row;i++)
    {
        sum += table[i][1];
        table[i][1] = sum;
    }

    //CS->NCS
    int min = table[0][1], max = table[row-1][1];

    for(int i=0;i<row;i++)
        table[i][1] = Math.round((table[i][1]-min)/(max-min));

    //Mapping
    for(int j=0;j<bufImg.getHeight();j++)
        for(int i=0;i<bufImg.getWidth();i++)
        {
            for(int k=0;k<row;k++)
                if(intensity[i][j]==table[k][0])
                    intensity[i][j] = table[k][1];

            Color newColor = new Color(intensity[i][j], intensity[i][j], intensity[i][j]);

            bufImg.setRGB(i, j, newColor.getRGB());
        }


    return bufImg;

//return bi;


//throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
/*
 * test.java
 */
package frcopy;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import static java.lang.System.exit;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import static javax.swing.JOptionPane.showMessageDialog;

public class test{
    
    
     private Connection con;//to connect to mysql database
    private Statement st;//to execute queries
    private ResultSet rs;//to hold returned results
    public test() throws SQLException{
        int i,j,gray;
    try{
    Class.forName("com.mysql.jdbc.driver");
    }
    catch(Exception e){
         System.out.println("abc");
        System.out.println(e);
        //exit(0);
        System.out.println("abc");
        
    }
        try {
            con= DriverManager.getConnection("jdbc:mysql://localhost:3306/face","root","");
            st=con.createStatement();
            
        } catch (SQLException ex) {
            Logger.getLogger(connection.class.getName()).log(Level.SEVERE, null, ex);
            showMessageDialog(null,"Please ensure your database connection first");
            exit(0);
        }
          
        
DatabaseMetaData dbm = con.getMetaData();
// check if "employee" table is there
ResultSet tables = dbm.getTables(null, null, "average", null);
if (tables.next()) {
  // Table exists
    
    
            System.out.println("hello baby");
}
else {
    String query="create table if not exists average( averageimage longblob, number INT(6) )";
         PreparedStatement pstmt = con.prepareStatement(query);
            // set parameter;
            //pstmt.setInt(1, candidateId);
            boolean t= pstmt.execute();
            
             float[][] avg=new float[250][250];
     
        try {
 BufferedImage theImage = new BufferedImage(250, 250, BufferedImage.TYPE_BYTE_GRAY);
    for( i=0; i<250; i++) {
         
        for( j=0; j<250; j++) {
            gray=0;//System.out.println(avg[i][j]);
            avg[i][j] = ((gray<<16) |(gray<<8) | gray  );
            theImage.setRGB(i,j, (int) avg[i][j]);
                    }
    }
    File output = new File("GrayScale.jpg");
    ImageIO.write(theImage, "jpg", output);
    System.out.println("banaiyo");
    File imgfile = new File("GrayScale.jpg");
        //BufferedImage image = ImageIO.read(imgfile);
    FileInputStream fin = new FileInputStream(imgfile);
    String query1="insert into average (averageimage, number) values(?,?)";
           pstmt = con.prepareStatement(query1);
		   pstmt.setBinaryStream(1,(InputStream)fin,(int)imgfile.length());
                    pstmt.setInt(2,0);
		         pstmt.executeUpdate();
                       // System.out.println(a);
		   System.out.println("Successfully operated into the database!");
            // set parameter;
            //pstmt.setInt(1, candidateId);
          //  boolean t= pstmt.execute();
}

catch(Exception e) {}
     
  // Table does not exist
}
   
    }
       
    }
    
	/*
 * testa.java
 */
package frcopy;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class testa {
    
    
    testa() throws IOException
    {
    int i,j,gray;
    float[][] avg=new float[280][280];
     BufferedImage theImage = new BufferedImage(250, 250, BufferedImage.TYPE_BYTE_GRAY);
         for( i=0; i<250; i++) {
         
        for( j=0; j<250; j++) {
            gray=512;
            avg[i][j] = ((gray<<16) |(gray<<8) | gray  );
            theImage.setRGB(i,j, (int) avg[i][j]);
                    }
    }
    File output = new File("GrayScale.jpg");
    ImageIO.write(theImage, "jpg", output);
    }
    
}

/*
 * train.java
 */
package frcopy;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import java.awt.image.BufferedImage;
import java.io.File;
//import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.Math.round;
import static java.lang.Math.sqrt;
import static java.lang.System.exit;
import java.sql.Blob;
import java.sql.Connection;                                                     
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import static javax.swing.JOptionPane.showMessageDialog;

public class train {
    //double size=200;
    long startTime = System.currentTimeMillis();
    double[][] average=new double[280][280];//average image
    
    private Connection con;//to connect to mysql database
    private Statement st;//to execute queries
    private ResultSet rs;//to hold returned 
    
    private PreparedStatement pstmt;
    int eigenvecno;
    train() throws SQLException
    {   
         for (int x = 0; x < 280; ++x)
    {
    for (int y = 0; y < 280; ++y)
    {
        average[x][y]=0;
    }
    }  
        
        String selectSQL = "SELECT * FROM testset1 WHERE 1";
        // System.out.println("conn done");
        try {
            con= DriverManager.getConnection("jdbc:mysql://localhost:3306/hello","root","");
            st=con.createStatement();
           // System.out.println("conn done");
            
        } catch (SQLException ex) {
            Logger.getLogger(connection.class.getName()).log(Level.SEVERE, null, ex);
            showMessageDialog(null,"Please ensure your database connection first");
            exit(0);
        }

        try  {
           // Connection conn = DriverManager.getConnection();
                 pstmt = con.prepareStatement(selectSQL);
            // set parameter;
            //pstmt.setInt(1, candidateId);
            rs = pstmt.executeQuery();
            
            String filename;
// Blob blob = resultSet.getBlob(index);
//InputStream stream = blob.getBinaryStream(0, blob.length());

        //BufferedImage image = ImageIO.read(stream);
            // write binary stream into file
           // File file = new File(filename);
           // FileOutputStream output = new FileOutputStream(file);
           // System.out.println("Writing to file " + file.getAbsolutePath());
            int a=0;
            while (rs.next()) { a++;// a counts the number of images in testset 
                }
               rs.beforeFirst();
            String name[]=new String[a];
            int faceid[]=new int[a];
            double[][][]images=new double[a][280][280];//images
            double[][][]diff=new double[a][280][280];//image-average
            double[][]diffarrange=new double[280*280][a];//diff arranged as column 
            
            int aa=0;           
            while (rs.next()) {              
               Blob blob = rs.getBlob(3);
               name[aa]=rs.getString(2);
               faceid[aa]=rs.getInt(1);            
               InputStream stream = blob.getBinaryStream();
               BufferedImage image = ImageIO.read(stream);                          
    for (int x = 0; x < 280; ++x)
    {
    	for (int y = 0; y < 280; ++y)
    	{
    		int rgb = image.getRGB(x, y);
    		int b = (rgb & 0xFF);
    		images [aa][x][y]=b;       
    		average[x][y]=average[x][y]+images[aa][x][y];
    	}
    }     
                aa++;
            }
            System.out.println(a+" ding bhayo");
            System.out.println(average[279][279]); 
    for (int x = 0; x < 280; ++x)
     {
        for (int y = 0; y < 280; ++y)
        {
            average[x][y]=average[x][y]/a;            
        }
     }
          System.out.println(average[279][279]);  
    for (int z = 0; z < a; ++z){
    for (int x = 0; x < 280; ++x){
    for (int y = 0; y < 280; ++y)
    {
        
        diff[z][x][y]=images[z][x][y]-average[x][y];
    }
    } 
    }
           
           
           
        int k=0;
            while(k<a)
            {//fun(k,d,a);
                int l=0;
            for(int i=0;i<280;i++)
        {
            for(int j=0;j<280;j++)
            { diffarrange[l][k]=diff[k][i][j];
            //diffarrange[l][k]=(double)Math.round(diffarrange[l][k] * 100000d) / 100000d;
            l++;
            }
         }  
            k++;}
            
            
            Matrix M=new Matrix(diffarrange);
            Matrix MT=M.transpose();
            Matrix C=MT.times(M);
             double cn[][]=C.getArray();
           /* System.out.println(" ");System.out.println(" ");
           
            for(int i=0;i<3;i++)
             { 
                 
                 
                 for(int j=0;j<3;j++)
                 {  //System.out.println(" "); 
                     //cn[i][j]=(double)Math.round(cn[i][j] * 1000000000d) / 1000000000d;   
                     //eigvectuseful[i][j]=eigvect[i][j];
                 }
             }
                  */ 
            Matrix Cv=new Matrix(cn);
            C=Cv;
           
           EigenvalueDecomposition eigval =new EigenvalueDecomposition(C);
           double[] eigvalue = eigval.getRealEigenvalues();
        //   double[] eigpos=new double[eigvalue.length];          
             Matrix eigvec=eigval.getV();
          double eigvect[][]=eigvec.getArray();
          
          
          
          
       //   int a[]={5,8,6,1,4,55,6,1,7,8,252,3,15,49,24,76};
          
        int index[]=new int[eigvalue.length];
        double tem;
        double temp[]=new double[eigvalue.length];
        System.arraycopy(eigvalue, 0, temp, 0, eigvalue.length);
        for(int i=0;i<eigvalue.length;i++)
        {
            for(int j=i+1;j<eigvalue.length;j++)
          {  
            if(temp[i]<temp[j])
            {
                tem=temp[i];
                temp[i]=temp[j];
                temp[j]=tem;
            }
          }
        }
        
        // System.out.println(a[i]);
        
         for(int i=0;i<eigvalue.length;i++)
        {   
            for(int j=0;j<eigvalue.length;j++)
          {  
            if(temp[i]==eigvalue[j])
            {
              index[i]=j;
             
            } 
            
          }
        }               
         System.out.println(eigvalue.length);
          
         if((1*eigvalue.length)>70)
         {eigenvecno=70;
             
         }
         else
         {
         eigenvecno=eigvalue.length;}
         //eigenvecno=80;
         double eigvectusefull[][]=new double[a][eigenvecno];
         double eigvectuseful[][];
         for(int i=0;i<a;i++)
             {
                 for(int j=0;j<eigenvecno;j++)
                 {
                     eigvectusefull[i][j]=eigvect [i][index[j]];
                 }
             }
   System.out.println(eigenvecno);      
         //double weight[][]=new double[a][eigenvecno];
          Matrix eigvectuseful50=new Matrix(eigvectusefull);
          Matrix reqeigvector=M.times(eigvectuseful50);
            eigvectuseful=reqeigvector.getArray();          
            System.out.println("ÿetro laamo time lago");
            //System.out.println(eigvectuseful[0][8]);
            double normfact[]=new double[k];
            
            for(int i=0;i<eigenvecno;i++)
          { 
              normfact[i]=0;
          }
          for(int i=0;i<eigenvecno;i++)
          { 
            for(int j=0;j<(280*280);j++)
            {
               normfact[i]=normfact[i]+(eigvectuseful[j][i]*eigvectuseful[j][i]); 
            }
          }
          
//          System.out.println(normfact[7]);
         double problem=0;
          for(int i=0;i<(280*280);i++)
          { 
            for(int j=0;j<eigenvecno;j++)
            {
               eigvectuseful[i][j]=(eigvectuseful[i][j])/sqrt(normfact[j]); 
               
               //problem=problem+eigvectuseful[i][j];
            }
          }
            System.out.println(problem+" problems found");
          //Matrix M=new Matrix(diffarrange);
           // Matrix MT=M.transpose();
         Matrix eigusefuljama=new Matrix(eigvectuseful);
         Matrix eigT=eigusefuljama.transpose();
          double eigTnormal[][]=eigT.getArray();  
         Matrix weight=eigT.times(M);
         double weightvector[][]=weight.getArray();
                    System.out.println(weightvector[0][0]);  
                                connection conn=new connection();     
     conn.putaverage(average);    
      conn.putweight(weightvector,name,faceid);
            conn.puteigen(eigvectusefull);                
        File folder = new File("temporary");
       boolean deletion=conn.deleteDirectory(folder);                           
                    
          /*          
         // yaha bata checking matra ho 
         double[][] finalobt=new double[a][(280*280)];
          //System.out.println("1st "+eigTnormal[3][500]+" no turu "+weightvector[3][0]);
          //System.out.println("2nd "+eigTnormal[2][500]+" no turu "+weightvector[2][0]);
        for(int i=0;i<a;i++)
         {   
         for(int j=0;j<eigenvecno;j++)
         {
             for(int ku=0;ku<(280*280);ku++)
             {
                 finalobt[i][ku]=finalobt[i][ku]+eigTnormal[j][ku]*weightvector[j][i];
             }
         }
         }
         
           
          // Matrix thisone=new Matrix(img1);
         //  Matrix first=thisone.transpose();
          // double thisonne[][]=thisone.getArray();
           double img[][][]=new double[a][280][280];
          
           
           for(int z=0;z<a;z++)
           {
           for(int i=0;i<280;i++)
         {
             for(int j=0;j<280;j++)
             {
                img[z][i][j]=finalobt[z][(i*280+j)]+average[i][j];
             }
         }
           }
          // System.out.println("  ");
           //System.out.println(images[1][1][1]+" "+img[1][1]);
                      System.out.println("twaa  ");
/*
           try {
 BufferedImage theImage = new BufferedImage(280, 280, BufferedImage.TYPE_BYTE_GRAY);
 for (int kush=0;kush<a;kush++) 
 {
 for(int i=0; i<280; i++) {
         
        for(int j=0; j<280; j++) {
         double   grayn=round(img[kush][i][j]);//System.out.println(avg[i][j]);
         int gray=(int)grayn;
            int grayt = ((gray<<16) |(gray<<8) | gray  );
            theImage.setRGB(i,j,grayt);
                    }
    }
   // File output = new File("F://"+kush+".jpg");
   // ImageIO.write(theImage, "jpg", output);
 }
    //System.out.println("banaiyo");
           } catch (Exception e){
           System.out.println(e);
           }
  */         
          // double d=((int)img[0][20][204] );
      //     System.out.println("hell "+weightvector[0][0]+" no "+weightvector[0][1]+" yes"+weightvector[0][2]);
           
            // yaha samma
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
      System.out.println(elapsedTime);
       // System.out.println(a+" eti "+eigenvecno);
       //  System.out.println(eigvectuseful.length+" eti "+eigvectuseful[0].length);    
            
        } catch (SQLException | IOException e) {
            System.out.println("this is it "+e.getMessage());
        } finally {
            try { 
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        
        
    }
        
    
    
}

