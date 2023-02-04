package com.example.demo.connex;
import java.sql.*;

public class Connexion
{
    Connection con;
    public Statement stat;
    ResultSet res;
    protected static String DB = "railway";
    protected static String Username = "postgres";
    protected static String Password = "DQqp3epOn5BfOG5n4Onx";
    protected static String PORT = "8022";
    protected static String HOST = "containers-us-west-62.railway.app";
    protected static String url = "jdbc:postgresql://"+HOST+":"+PORT+"/"+DB;


    public Connexion(String req)
    {
        try
        {
            Class.forName("org.postgresql.Driver");
            this.con = DriverManager.getConnection(url, Username, Password);
            this.stat= this.con.createStatement();
//  			this.res=stat.executeQuery(req);
            stat.execute(req);
        }
        catch(SQLException sqle)
        {
            sqle.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    public Connexion()
    {
        try {
            Class.forName("org.postgresql.Driver");
            this.con = DriverManager.getConnection(url, Username, Password);
        } catch (Exception e) {
        } finally {
        }
    }

    public void Resolve() {
        try {
            if (this.con == null) {
                Class.forName("org.postgresql.Driver");
                this.con = DriverManager.getConnection(url, Username, Password);
            }
        }
        catch(Exception e) { e.printStackTrace(); }
        finally {}
    }

    public void Close() {
        try {
            if(this.res != null) {
                getResultset().close();
            }
            if(this.stat != null) {
                getStatement().close();
            }
            if(this.con != null) {
                getConnection().close();
            }
        }
        catch(SQLException e) { e.printStackTrace(); }
        finally {}
    }

    public void CloseSC() {
        try {
            if(this.stat != null) {
                getStatement().close();
            }
            if(this.con != null) {
                getConnection().close();
            }
        }
        catch(SQLException e) { e.printStackTrace(); }
        finally {}
    }

    public void CloseRC() {
        try {
            if(this.res != null) {
                getResultset().close();
            }
            if(this.con != null) {
                getConnection().close();
            }
        }
        catch(SQLException e) { e.printStackTrace(); }
        finally {}
    }


    public Connexion(String req,String ide)
    {
        try
        {
            Class.forName("org.postgresql.Driver");
            this.con = DriverManager.getConnection(url, Username, Password);
            this.stat= this.con.createStatement();
            this.res=stat.executeQuery(req);
            //	stat.execute(req);
        }
        catch(SQLException sqle)
        {
            sqle.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return this.con;
    }
    public Statement getStatement() {
        return this.stat;
    }
    public PreparedStatement prepareStatement(String query) throws SQLException{
        return this.con.prepareStatement(query);
    }
    public ResultSet getResultset()
    {
        return this.res;
    }
    public void getCommit() throws Exception
    {
        this.stat.executeQuery("commit");
    }
    public void getRollBack() throws Exception
    {
        this.stat.executeQuery("rollback");
    }
    public Statement getStat()
    {
        return this.stat;
    }
}

