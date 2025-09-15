package minggu2.models;

import java.util.List;

import minggu2.database.Database;

import java.util.ArrayList;
import java.sql.*;;

public class Jurusan {
    public static boolean insertJurusan(String kode, String nama) {
        String sql = "INSERT INTO jurusan (kodeMK, namaMK) VALUES(?, ?)";
        try (Connection conn = Database.connect();
            PreparedStatement state = conn.prepareStatement(sql)) {
            state.setString(1, kode);
            state.setString(2, nama);
            state.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Gagal memasukkan jurusan: " + e.getMessage());
            return false;
        }
    }

    public static List<String> getAllJurusan() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT kodeMK, namaMK FROM jurusan";

        try (Connection conn = Database.connect();
            PreparedStatement state = conn.prepareStatement(sql);
            ResultSet rs = state.executeQuery() ){
            while (rs.next()) {
                list.add(rs.getString("kodeMK") + " - " + rs.getString("namaMK"));
            }
        } catch (SQLException e) {
            System.out.println("Error getAllJurusan: " + e.getMessage());
        }
        return list;
    }

    public static boolean deleteJurusan(String kode) {
        String sql = "DELETE FROM jurusan WHERE kodeMK = ?";
        try (Connection conn = Database.connect();
            PreparedStatement state = conn.prepareStatement(sql)){
            state.setString(1, kode);
            return state.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error delete jurusan: " + e.getMessage());
            return false;
        }
    }
}
