import java.sql.*;
import java.util.*;

public class TimelisteDb {
    private Connection connection;

    public TimelisteDb(Connection connection) {
        this.connection = connection;
    }

    public void printTimelister()throws SQLException {
      Statement state = connection.createStatement();
      String sporring = "SELECT timelistenr," + "status," + "beskrivelse " + "from tliste ";
      ResultSet rs = state.executeQuery(sporring);
      System.out.println("Timelistenr status beskrivelse");
      System.out.println();
      while(rs.next()){
        int i = rs.getInt("timelistenr");
        String s = rs.getString("status");
        String b = rs.getString("beskrivelse");
        System.out.format("%d,%s, %s\n", i, s, b);
    }
  }

    public void printTimelistelinjer(int timelisteNr) throws SQLException {
      Statement state = connection.createStatement();
      String sporring = "SELECT linjenr, timeantall, beskrivelse, kumulativt_timeantall from tlistelinje where timelistenr = " + timelisteNr;
      ResultSet rs = state.executeQuery(sporring);
      System.out.println("");

      while(rs.next()){
        int linje = rs.getInt("linjenr");
        int time = rs.getInt("timeantall");
        String bes = rs.getString("beskrivelse");
        int kumulativt = rs.getInt("kumulativt_timeantall");
        System.out.format("%d, %d, %s, %d\n", linje, time, bes, kumulativt);
      }
    }

    public double medianTimeantall(int timelisteNr) throws SQLException {
        Statement state = connection.createStatement();
        String sporring = "SELECT timeantall from tlistelinje where timelistenr = " + timelisteNr + "ORDER BY timeantall";
        ResultSet rs = state.executeQuery(sporring);
        ArrayList<Integer> liste = new ArrayList<Integer>();

        while(rs.next()){
          int i = rs.getInt("timeantall");
          liste.add(i);
        }
        return median(liste);
    }

    public void settInnTimelistelinje(int timelisteNr, int antallTimer, String beskrivelse) throws SQLException{
      Statement test = connection.createStatement();
      String spor = "select MAX(linjenr) as linjenr from tlistelinje where timelistenr = " + timelisteNr;
      ResultSet r = test.executeQuery(spor);

      int maxlinje = 0;
      while(r.next()){
        maxlinje = r.getInt("linjenr");
        maxlinje++;
        //System.out.println(maxlinje);
      String sql =  "INSERT INTO tlistelinje(timelistenr,linjenr, timeantall, beskrivelse) VALUES (?, ?, ?, ?)";
      PreparedStatement pst = connection.prepareStatement(sql);
      pst.setInt(1, timelisteNr);
      pst.setInt(2, maxlinje);
      pst.setInt(3, antallTimer);
      pst.setString(4, beskrivelse);
      pst.executeUpdate();
    }
  }

    public void regnUtKumulativtTimeantall(int timelisteNr) throws SQLException {
      Statement test = connection.createStatement();
      String sporring = "select timelistenr, linjenr, timeantall from tlistelinje where timelistenr = " + timelisteNr;
      ResultSet r = test.executeQuery(sporring);

      int sum = 0;
      while(r.next()){
        int s = r.getInt("linjenr");
        int i = r.getInt("timeantall");
        sum = sum + i;
        System.out.println(sum);
        String sql = "UPDATE tlistelinje SET kumulativt_timeantall = " + sum + "where timelistenr = " + timelisteNr + "AND linjenr = " + s ;
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.executeUpdate();
      }
    }

    /**
     * Hjelpemetode som regner ut medianverdien i en SORTERT liste. Kan slettes om du ikke har bruk for den.
     * @param list Tar inn en sortert liste av integers (f.eks. ArrayList, LinkedList osv)
     * @return medianverdien til denne listen
     */
    private double median(List<Integer> list) {
        int length = list.size();
        if (length % 2 == 0) {
            return (list.get(length / 2) + list.get(length / 2 - 1)) / 2.0;
        } else {
            return list.get((length - 1) / 2);
        }
    }
}
