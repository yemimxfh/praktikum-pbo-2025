package minggu2.models;

import java.sql.*;
import java.util.*;
import javax.swing.JOptionPane;
import minggu2.database.Database;

class Jurusan {

    String kode;
    String nama;
    ArrayList<MataKuliah> mataKuliahList = new ArrayList<>();
}

class Mahasiswa {
    String nim;
    String nama;
    Jurusan jurusan;
    Map<MataKuliah, String> indeksNilai;
}

class MataKuliah {
    String kode;
    String nama;
    int sks;
}

public class modul1 {

    // input JOPtionPane
    public static String inputUser(String pesan) {
        return JOptionPane.showInputDialog(null, pesan);
    }

    public static void input(String pesan) {
        JOptionPane.showMessageDialog(null, pesan);
    }

    // soal a
    public static void menuJurusan() {
        String teks = "1. input jurusan baru \n 2. melihat daftar jurusan \n 3. menghapus jurusan baru";
        int pilih = Integer.parseInt(inputUser(teks));

        switch (pilih) {
            case 1:
                inputJurusan();
                break;
            case 2:
                ArrayList<Jurusan> list = listJurusan();
                StringBuilder sb = new StringBuilder("===Daftar Jurusan===\n");
                if (list.isEmpty()) {
                    sb.append("Kosong");
                }
                for (Jurusan j : list) {
                    sb.append(j.kode).append(" - ").append(j.nama).append("\n");
                }
                input(sb.toString());
                break;
            case 3:
                hapusJurusan();
                break;
            default:
                break;
        }
    }

    public static void inputJurusan() {
        String kode = inputUser("===Input Jurusan Baru=== \n Masukkan kode Jurusan:");
        String nama = inputUser("===Input Jurusan Baru=== \n Masukkan nama jurusan:");

        if (!cariJurusan(kode)) {
            String insertSql = "INSERT INTO jurusan (kode_jurusan, nama) VALUES (?,?)";
            try (Connection con = Database.connect();
                    PreparedStatement insertPs = con.prepareStatement(insertSql)) {

                insertPs.setString(1, kode);
                insertPs.setString(2, nama);
                insertPs.executeUpdate();
                input("Berhasil menambahkan jurusan" + nama + "!");
            } catch (SQLException e) {
                input("Gagal menambahkan jurusan: " + e.getMessage());
            }
        } else {
            input("Jurusan dengan kode " + kode + "sudah ada!");
        }
    }

    public static boolean cariJurusan(String kode) {
        String sql = "SELECT COUNT(*) FROM jurusan WHERE kode_jurusan = ?";

        try (Connection con = Database.connect();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, kode);
            ResultSet rs = ps.executeQuery();
            rs.next();

            return rs.getInt(1) > 0;

        } catch (SQLException e) {
            return false;
        }
    }

    public static ArrayList<Jurusan> listJurusan() {
        ArrayList<Jurusan> list = new ArrayList<>();
        String sql = "SELECT * FROM jurusan";

        try (Connection con = Database.connect();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Jurusan j = new Jurusan();
                j.kode = rs.getString("kode_jurusan");
                j.nama = rs.getString("nama");
                list.add(j);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void hapusJurusan() {
        String kode = inputUser("===Hapus Jurusan===\nMasukkan kode jurusan:");
        String sql = "DELETE FROM jurusan WHERE kode_jurusan = ?";

        try (Connection con = Database.connect();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, kode);
            int row = ps.executeUpdate();
            if (row > 0) {
                input("Jurusan" + kode + "berhasil dihapus");
            } else {
                input("Jurusan tidak ditemukan");
            }

        } catch (SQLException e) {
            input("Gagal menghapus jurusan " + e.getMessage());
        }

    }

    // soal b
    public static void menuMK() {
        String teks = "1. input mata kuliah baru \n 2. hapus mata kuliah \n 3. mengambil mata kuliah(mahasiswa)";
        int pilih = Integer.parseInt(inputUser(teks));

        switch (pilih) {
            case 1:
                inputMK();
                break;
            case 2:
                removeMK();
                break;
            case 3:
                ambilMK();
                break;
            default:
                break;
        }
    }

    public static void inputMK() {

        String kodeJur = inputUser("===Input Mata Kuliah Baru== \n Masukkan kode jurusan: ");

        if (cariJurusan(kodeJur)) {
            String kodeMK = inputUser("===Input Mata Kuliah Baru=== \n Masukkan kode mata kuliah:");

            String cekSql = "SELECT COUNT(*) FROM matakuliah WHERE kode_matakuliah = ?";
            String insertSql = "INSERT INTO matakuliah (kode_matakuliah, nama, sks, kode_jurusan) VALUES (?, ?, ?, ?)";

            try (Connection con = Database.connect();
                    PreparedStatement cekPs = con.prepareStatement(cekSql);
                    PreparedStatement insertPs = con.prepareStatement(insertSql)) {

                cekPs.setString(1, kodeMK);
                ResultSet rs = cekPs.executeQuery();
                rs.next();
                int count = rs.getInt(1);

                if (count > 0) {
                    input("Mata Kuliah dengan kode " + kodeMK + " sudah ada!");
                    return;
                }

                String namaMK = inputUser("===Input Mata Kuliah Baru=== \n Masukkan nama mata kuliah:");
                int sks = Integer
                        .parseInt(inputUser("===Input Mata Kuliah Baru=== \n Masukkan jumlah sks mata kuliah:"));
                insertPs.setString(1, kodeMK);
                insertPs.setString(2, namaMK);
                insertPs.setInt(3, sks);
                insertPs.setString(4, kodeJur);
                insertPs.executeUpdate();
                input("Berhasil menambahkan mata kuliah " + kodeMK);

            } catch (SQLException e) {
                input("Gagal menambahkan mata kuliah " + e.getMessage());
            }
        } else {
            input("Jurusan tidak ada!");
        }

    }

    public static void removeMK() {
        String kodeJur = inputUser("===Hapus Mata Kuliah Baru== \n Masukkan kode jurusan: ");

        if (!cariJurusan(kodeJur)) {
            input("Tidak ada jurusan tersebut!");
            return;
        }

        ArrayList<MataKuliah> listMK = showMK(kodeJur);

        StringBuilder daftarMK = new StringBuilder("===Daftar Mata Kuliah===\n");
        for (MataKuliah mk : listMK) {
            daftarMK.append(mk.kode).append(" - ").append(mk.nama).append("\n");
        }

        String kodeMK = inputUser(
                "===Hapus Mata Kuliah Baru===" + daftarMK +
                        "\nMasukkan kode MK yang akan dihapus: ");
        String sql = "DELETE FROM matakuliah WHERE kode_matakuliah = ? AND kode_jurusan = ?";

        try (Connection con = Database.connect();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, kodeMK);
            ps.setString(2, kodeJur);

            int row = ps.executeUpdate();

            if (row > 0) {
                input("Berhasil dihapus");
            } else {
                input("Mata kuliah tidak ditemukan");
            }
        } catch (SQLException e) {
            input("Gagal menghapus jurusan" + e.getMessage());
        }

    }

    public static ArrayList<MataKuliah> showMK(String kodeJur) {
        ArrayList<MataKuliah> list = new ArrayList<>();
        String sql = "SELECT * FROM matakuliah WHERE kode_jurusan = ?";
        try (Connection con = Database.connect();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, kodeJur);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                MataKuliah mk = new MataKuliah();
                mk.kode = rs.getString("kode_matakuliah");
                mk.nama = rs.getString("nama");
                mk.sks = rs.getInt("sks");
                list.add(mk);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void ambilMK() {
        String nim = inputUser("Masukkan NIM Mahasiswa:");

        Mahasiswa mhs = cariNIM(nim);
        if (mhs == null) {
            input("Tidak ditemukan");
            return;
        }

        ArrayList<MataKuliah> listMK = showMK(mhs.jurusan.kode);
        StringBuilder daftarMK = new StringBuilder("===Daftar Mata Kuliah===\n");
        for (MataKuliah mk : listMK) {
            daftarMK.append(mk.kode).append(" - ").append(mk.nama).append("\n");
        }

        String pilihMK = inputUser(daftarMK + "\nMasukkan kode MK yang mau diambil:");
        simpanKRS(mhs.nim, pilihMK);

    }

    public static void simpanKRS(String nim, String kodeMK) {
        String cek = "SELECT COUNT(*) FROM mahasiswa_matakuliah WHERE nim = ? AND kode_matakuliah = ?";
        String sql = "INSERT INTO mahasiswa_matakuliah(nim, kode_matakuliah) VALUES (?,?)";
        try (Connection con = Database.connect();
                PreparedStatement cekPs = con.prepareStatement(cek);
                PreparedStatement insertPs = con.prepareStatement(sql)) {

            cekPs.setString(1, nim);
            cekPs.setString(2, kodeMK);
            ResultSet rs = cekPs.executeQuery();
            rs.next();

            if (rs.getInt(1) > 0) {
                input("Mahasiswa sudah mengambil mata kuliah tersebut");
                return;
            }

            insertPs.setString(1, nim);
            insertPs.setString(2, kodeMK);
            insertPs.executeUpdate();
            input("Berhasil mengambil mata kuliah!");

        } catch (SQLException e) {
            input("Gagal mengambil matkul: " + e.getMessage());
        }
    }

    public static void menuMahasiswa() {
        String teks = "1. input mahasiswa baru \n 2. melihat daftar mahasiswa \n 3. melihat detail mahasiswa";
        int pilih = Integer.parseInt(inputUser(teks));

        switch (pilih) {
            case 1:
                inputMahasiswa();
                break;
            case 2:
                listMahasiswa();
                break;
            case 3:
                detailMahasiswa();
                break;
            default:
                break;
        }
    }

    public static void inputMahasiswa() {
        String nim = inputUser("===Input Mahasiswa Baru=== \n Masukkan nim mahasiswa:");
        String nama = inputUser("===Input Mahasiswa Baru=== \n Masukkan nama mahasiswa:");

        Mahasiswa mhs = cariNIM(nim);

        if (mhs != null) {
            System.out.println("Sudah ada mahasiswa dengan nim tersebut!");
            return;

        }

        StringBuilder daftarJur = new StringBuilder("===DaftarJurusan===\n");
        ArrayList<Jurusan> listJur = listJurusan();
        for (Jurusan j : listJur) {
            daftarJur.append(j.kode).append(" - ").append(j.nama).append("\n");
        }

        String kodeJur = inputUser("Masukkan kode jurusan:");
        if (cariJurusan(kodeJur)) {
            String sql = "INSERT INTO mahasiswa(nim, nama, kode_jurusan) VALUES(?, ?, ?)";
            try (Connection con = Database.connect();
                    PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, nim);
                ps.setString(2, nama);
                ps.setString(3, kodeJur);
                ps.executeUpdate();
                input("Berhasil memasukkan mahasiswa!");

            } catch (Exception e) {
                input("Gagal memasukkan mahasiswa: " + e.getMessage());
            }
        } else {
            input("Tidak ada jurusan yang dicari!");
        }

    }

    public static void listMahasiswa() {

        String sql = "SELECT m.nim, m.nama, j.nama AS nama_jurusan FROM mahasiswa m JOIN jurusan j ON m.kode_jurusan = j.kode_jurusan ORDER BY m.nim";
        StringBuilder sb = new StringBuilder("===Daftar Mahasiswa===\n");
        try (Connection con = Database.connect();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                sb.append(rs.getString("nim")).append(" - ").append(rs.getString("nama")).append(" - ")
                        .append(rs.getString("nama_jurusan")).append("\n");
            }
            input(sb.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            input("Gagal mengambil daftar mahasiswa: " + e.getMessage());
        }
    }

    // cari nim
    public static Mahasiswa cariNIM(String nimInput) {
        String sql = "SELECT m.nim, m.nama, m.kode_jurusan, j.nama AS nama_jurusan FROM mahasiswa m JOIN jurusan j ON m.kode_jurusan = j.kode_jurusan WHERE m.nim = ?";

        try (Connection con = Database.connect();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nimInput);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Mahasiswa mhs = new Mahasiswa();
                mhs.nim = rs.getString("nim");
                mhs.nama = rs.getString("nama");

                Jurusan jur = new Jurusan();
                jur.kode = rs.getString("kode_jurusan");
                jur.nama = rs.getString("nama_jurusan");
                mhs.jurusan = jur;

                return mhs;

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void detailMahasiswa() {
        String nimInput = inputUser("Masukkan nim");
        Mahasiswa mhs = cariNIM(nimInput);

        if (mhs == null) {
            System.out.println("Tidak ditemukan");
            return;
        }

        String sql = "SELECT m.nim, m.nama, j.nama AS nama_jurusan, mk.kode_matakuliah, mk.nama AS nama_matakuliah, mm.indeks_nilai FROM mahasiswa m JOIN jurusan j ON m.kode_jurusan = j.kode_jurusan LEFT JOIN mahasiswa_matakuliah mm ON m.nim = mm.nim LEFT JOIN matakuliah mk ON mm.kode_matakuliah = mk.kode_matakuliah WHERE m.nim = ?";
        try (Connection con = Database.connect();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, mhs.nim);
            ResultSet rs = ps.executeQuery();

            StringBuilder sb = new StringBuilder();
            sb.append("NIM = ").append(mhs.nim).append("\n");
            sb.append("Nama = ").append(mhs.nama).append("\n");
            sb.append("Jurusan = ").append(mhs.jurusan.nama).append("\n");
            sb.append("===Daftar MK yang diambil===\n");

            boolean hasMK = false;
            while (rs.next()) {
                String kodeMK = rs.getString("kode_matakuliah");
                if (kodeMK != null) {
                    hasMK = true;
                    sb.append(kodeMK).append(" - ").append(rs.getString("nama_matakuliah")).append(" - Nilai: ")
                            .append(rs.getString("indeks_nilai")).append("\n");
                }
            }

            if (!hasMK) {
                sb.append("Belum ada mata kuliah yang diambil\n");
            }
            input(sb.toString());

        } catch (Exception e) {
            input("Gagal menampilkan detail" + e.getMessage());
        }

    }

    // soal d
    public static void beriNilai() {
        String nimInput = inputUser("Masukkan nim");
        Mahasiswa mhs = cariNIM(nimInput);

        if (mhs == null) {
            input("Tidak ditemukan!");
            return;
        }

        String sql = "SELECT mm.kode_matakuliah, mk.nama, mm.indeks_nilai FROM mahasiswa_matakuliah mm JOIN matakuliah mk ON mm.kode_matakuliah = mk.kode_matakuliah WHERE mm.nim = ?";
        try (Connection con = Database.connect();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, mhs.nim);
            ResultSet rs = ps.executeQuery();

            StringBuilder sb = new StringBuilder("===Daftar MK yang diambil===\n");
            ArrayList<String> listAmbil = new ArrayList<>();
            while (rs.next()) {
                sb.append(rs.getString("kode_matakuliah")).append(" - ").append(rs.getString("nama"))
                        .append(" - Nilai: ")
                        .append(rs.getString("indeks_nilai")).append("\n");
                listAmbil.add(rs.getString("kode_matakuliah"));
            }
            if (listAmbil.isEmpty()) {
                input("Mahasiswa belum mengambil mata kuliah apapun");
                return;
            }

            String kodeMK = inputUser(sb + "\nMasukkan kode MK yang mau diberi nilai:");
            if (!listAmbil.contains(kodeMK)) {
                input("Mahasiswa tidak mengambil matkul tersebut");
                return;
            }

            String nilai = inputUser("Masukkan nilai (A/B/C/D/E): ");
            nilai = nilai.toUpperCase();
            if (!List.of("A", "B", "C", "D", "E").contains(nilai)) {
                input("Nilai tidak valid!");
                return;
            }

            String update = "UPDATE mahasiswa_matakuliah SET indeks_nilai = ? WHERE nim = ? AND kode_matakuliah = ?";
            try (PreparedStatement updatePs = con.prepareStatement(update)) {
                updatePs.setString(1, nilai);
                updatePs.setString(2, mhs.nim);
                updatePs.setString(3, kodeMK);

                int row = updatePs.executeUpdate();
                if (row > 0) {
                    input("Berhasil memberi nilai");
                } else {
                    input("Gagal memberi nilai");
                }
            }
        } catch (SQLException e) {
            input("Gagal memberi nilai: " + e.getMessage());
        }
    }

    // soal e

    public static void menghitungIP() {
        String nimInput = inputUser("Masukkan nim");
        Mahasiswa mhs = cariNIM(nimInput);

        if (mhs == null) {
            input("Tidak ditemukan!");
            return;
        }

        String sql = "SELECT mk.sks, mm.indeks_nilai FROM mahasiswa_matakuliah mm JOIN matakuliah mk ON mm.kode_matakuliah = mk.kode_matakuliah WHERE mm.nim = ? AND mm.indeks_nilai IS NOT NULL";
        try (Connection con = Database.connect();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, mhs.nim);
            ResultSet rs = ps.executeQuery();

            double total = 0;
            int totalSKS = 0;
            while (rs.next()) {
                int sks = rs.getInt("sks");
                String nilai = rs.getString("indeks_nilai");
                int angka = getAngka(nilai);
                if (angka >= 0) {
                    total += sks * angka;
                    totalSKS += sks;
                }
            }

            if (totalSKS == 0) {
                input("Belum ada nilai untuk menghitung IP");
                return;
            }
            double IP = (double) total / totalSKS;
            input("NIM = " + mhs.nim + " \nNama = " + mhs.nama + "\n IP = " + IP);
        } catch (SQLException e) {
            input("Gagal menghitung IP: " + e.getMessage());
        }

    }

    public static int getAngka(String i) {
        if (i == null)
            return -1;
        switch (i.toUpperCase()) {
            case "A":
                return 4;
            case "B":
                return 3;
            case "C":
                return 2;
            case "D":
                return 1;
            case "E":
                return 0;
            default:
                return -1;
        }
    }

    public static void main(String[] args) {
        while (true) {
            String teks = "=== MENU UTAMA ===\n"
                    + "1. Menu Jurusan\n"
                    + "2. Menu Mata Kuliah\n"
                    + "3. Menu Mahasiswa\n"

                    + "4. Beri Nilai\n"
                    + "5. Hitung IP Mahasiswa\n"
                    + "6. Keluar";

            int pilih = Integer.parseInt(inputUser(teks));

            switch (pilih) {
                case 1:
                    menuJurusan();
                    break;
                case 2:
                    menuMK();
                    break;
                case 3:
                    menuMahasiswa();
                    break;
                case 4:
                    beriNilai();
                    break;
                case 5:
                    menghitungIP();
                    break;
                case 6:
                    System.exit(0);
                    break;
                default:
                    input("Pilihan tidak valid!");
            }
        }
    }
}
