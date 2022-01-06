package com.wissen.training.springrestbasics.aws;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.AmazonRDSClientBuilder;
import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.DescribeDBInstancesResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.*;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RdsConnect {

    private static final Logger logger = LoggerFactory.getLogger(RdsConnect.class);
    /**
     * http://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-started.html
     */
    private static void awsRdsInstance(){
       // ProfileCredentialsProvider p
        Region region = Region.AP_SOUTH_1;
        AmazonRDS awsRDS = AmazonRDSClientBuilder.standard().withRegion(Regions.AP_SOUTH_1).build();


        DescribeDBInstancesResult dbInstResult = awsRDS.describeDBInstances();

        List<DBInstance> dbInstances= dbInstResult.getDBInstances();

        System.out.println("size of dbInstances: "+dbInstances.size());

        for(DBInstance dbInst:dbInstances)
        {
            System.out.println("DB Instance:: "+ dbInst.getDBName());
        }


    }

    public static void createDatabaseInstance(RdsClient rdsClient,
                                              String dbInstanceIdentifier,
                                              String dbName,
                                              String masterUsername,
                                              String masterUserPassword) {

        try {
            CreateDbInstanceRequest instanceRequest = CreateDbInstanceRequest.builder()
                    .dbInstanceIdentifier(dbInstanceIdentifier)
                    .allocatedStorage(20)
                    .dbName(dbName)
                    .engine("mysql")
                    .dbInstanceClass("db.t2.micro")
                    .engineVersion("8.0.15")
                    .storageType("standard")
                    .masterUsername(masterUsername)
                    .masterUserPassword(masterUserPassword)
                    .build();

            CreateDbInstanceResponse response = rdsClient.createDBInstance(instanceRequest);
            waitForInstanceToGetReady(rdsClient,dbInstanceIdentifier);
            System.out.print("The status is " + response.dbInstance().dbInstanceStatus());

        } catch (RdsException | InterruptedException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }

    }


    private static void connectToRdsInstance(){
        String dbName = "dbnameusingjavasdknew";
        String userName = "administrator";
        String password = "administrator";
        String hostname = "db-using-java-sdk-new.c3rtlok5p7fq.us-west-2.rds.amazonaws.com";
        String port = "3306";
        String jdbcUrl =  "jdbc:mysql://" + hostname + ":" +
                port + "/" + dbName + "?user=" + userName + "&password=" + password;
        try {
            System.out.println("Loading driver...");
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Driver loaded!");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot find the driver in the classpath!", e);
        }

        Connection conn = null;
        Statement setupStatement = null;
        Statement readStatement = null;
        ResultSet resultSet = null;
        String results = "";
        int numresults = 0;
        String statement = null;

        try {
            // Create connection to RDS DB instance
            conn = DriverManager.getConnection(jdbcUrl);

            // Create a table and write two rows
            setupStatement = conn.createStatement();
            String createTable = "CREATE TABLE Beanstalk (Resource char(50));";
            String insertRow1 = "INSERT INTO Beanstalk (Resource) VALUES ('EC2 Instance');";
            String insertRow2 = "INSERT INTO Beanstalk (Resource) VALUES ('RDS Instance');";

            setupStatement.addBatch(createTable);
            setupStatement.addBatch(insertRow1);
            setupStatement.addBatch(insertRow2);
            setupStatement.executeBatch();
            setupStatement.close();

        } catch (SQLException ex) {
            // Handle any errors
            System.out.println("Not been able to connect: "+ex);
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } finally {
            System.out.println("Closing the connection.");
            if (conn != null) try { conn.close(); } catch (SQLException ignore) {}
        }

        try {
            conn = DriverManager.getConnection(jdbcUrl);

            readStatement = conn.createStatement();
            resultSet = readStatement.executeQuery("SELECT Resource FROM Beanstalk;");

            resultSet.first();
            results = resultSet.getString("Resource");
            resultSet.next();
            results += ", " + resultSet.getString("Resource");

            resultSet.close();
            readStatement.close();
            conn.close();

        } catch (SQLException ex) {
            // Handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } finally {
            System.out.println("Closing the connection.");
            if (conn != null) try { conn.close(); } catch (SQLException ignore) {}
        }

    }

    private static Connection getRemoteConnection() {

            try {
                Class.forName("com.mysql.jdbc.Driver");
                String dbName = "dbnameusingjavasdknew";
                String userName = "administrator";
                String password = "administrator";
                String hostname = "db-using-java-sdk-new.c3rtlok5p7fq.us-west-2.rds.amazonaws.com";
                String port = "3306";
                String jdbcUrl = "jdbc:mysql://" + hostname + ":" + port + "/" + dbName + "?user=" + userName + "&password=" + password;
                logger.trace("Getting remote connection with connection string from environment variables.");
                Connection con = DriverManager.getConnection(jdbcUrl);
                logger.info("Remote connection successful.");
                return con;
            }
            catch (ClassNotFoundException e) { logger.warn(e.toString());}
            catch (SQLException e) { logger.warn(e.toString());}

        return null;
    }

    public static void waitForInstanceToGetReady(RdsClient rdsClient, String dbInstanceIdentifier) throws InterruptedException {
        boolean isAvailable = false;
        DescribeDbInstancesRequest describeDbInstancesRequest = DescribeDbInstancesRequest.builder().dbInstanceIdentifier(dbInstanceIdentifier).build();
        System.out.println("Waiting for Instance to avaialable");
        int totalWaitTime = 10000;
        int currentWait = 0;
        while(!isAvailable){
            DescribeDbInstancesResponse describeDbInstancesResponse = rdsClient.describeDBInstances();
            List<software.amazon.awssdk.services.rds.model.DBInstance> dbInstances = describeDbInstancesResponse.dbInstances();
            for(software.amazon.awssdk.services.rds.model.DBInstance dbInstance: dbInstances){
                if(dbInstance.dbInstanceStatus().contains("available")){
                    isAvailable = true;
                }else{
                    Thread.sleep(1000);
                    currentWait = currentWait+1000;
                    if(currentWait>=totalWaitTime){
                        System.out.println("Taking More Time Then expected.. Try Again After Some Time");
                        break;
                    }
                }
            }

        }
    }

    public static void main(String[] args) {
//       Connection connection =  getRemoteConnection();
//        System.out.println(connection);
//       awsRdsInstance();
       connectToRdsInstance();
//        String dbInstanceIdentifier = "db-using-java-sdk-new";
//        String dbName = "dbnameusingjavasdknew";
//        String masterUsername = "administrator";
//        String masterUserPassword = "administrator";
//
//        Region region = Region.US_WEST_2;
//        RdsClient rdsClient = RdsClient.builder()
//                .region(region)
//                .build();
//
//        createDatabaseInstance(rdsClient, dbInstanceIdentifier, dbName, masterUsername, masterUserPassword) ;
    }


}
