package minggu2.models;
import java.util.ArrayList;
import java.sql.*;
import java.util.List;

import minggu2.database.Database;

public class Mahasiswa {
    public static boolean insertJurusan(String kode, String nama, String kode_jurusan) {
        String sql = "INSERT INTO mahasiswa (nim, nama, kode_jurusan) VALUES(?, ?, ?)";
        try (Connection conn = Database.connect();
            PreparedStatement state = conn.prepareStatement(sql)) {
            state.setString(1, kode);
            state.setString(2, nama);
            state.setString(3, kode_jurusan);
            state.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Gagal memasukkan mahasiswa: " + e.getMessage());
            return false;
        }
    }
}
