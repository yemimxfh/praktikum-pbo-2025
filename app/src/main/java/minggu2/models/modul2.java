package minggu2.models;

import java.util.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

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

public class modul2 {

    static ArrayList<Mahasiswa> daftarMahasiswa = new ArrayList<>();

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
                listJurusan();
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

        boolean berhasil = Jurusan.insertJurusan(kode, nama);
        if (berhasil) {
            input("Berhasil memasukkan jurusan ");
        } else {
            input("Gagal memasukkan jurusan (mungkin juga sudah ada)");
        }

    }

    public static void listJurusan() {
        String teks = "===Daftar Jurusan=== \n";
        List<String> list = Jurusan.getAllJurusan();
        
        if (!list.isEmpty()) {
            for (String jurusan : list) {
                teks += jurusan + "\n";
            }       
        } else {
            teks += "Kosong";
        }
        input(teks);
    }

    public static void hapusJurusan() {
        String kode = inputUser("===Hapus Jurusan===\nMasukkan kode jurusan:");
        boolean berhasil = Jurusan.deleteJurusan(kode);
        if (berhasil) {
            input("Berhasil menghapus jurusan");
        } else {
            input("Gagal menghapus jurusan");
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

        String input = inputUser("===Input Mata Kuliah Baru== \n Masukkan kode jurusan: ");
        Jurusan j = null;

        for (Jurusan jurusan : daftarJurusan) {
            if (jurusan.kode.equalsIgnoreCase(input)) {
                j = jurusan;
                break;
            }
        }

        if (j == null) {
            input("Tidak ditemukan");
            return;
        }
        String kode = inputUser("===Input Mata Kuliah Baru=== \n Masukkan kode mata kuliah:");

        boolean ada = false;
        for (MataKuliah mk : j.mataKuliahList) {
            if (mk.kode.equalsIgnoreCase(kode)) {
                ada = true;
                break;
            }
        }

        if (ada) {
            input("Matkul sudah ada");
        } else {
            String nama = inputUser("===Input Mata Kuliah Baru=== \n Masukkan nama mata kuliah:");
            int sks = Integer.parseInt(inputUser("===Input Mata Kuliah Baru=== \n Masukkan jumlah sks matkul:"));
            MataKuliah mk = new MataKuliah();
            mk.kode = kode;
            mk.nama = nama;
            mk.sks = sks;
            j.mataKuliahList.add(mk);
            input("Berhasil ditambahkan!");
        }
    }

    public static void removeMK() {
        String input = inputUser("===Hapus Mata Kuliah Baru== \n Masukkan kode jurusan: ");
        Jurusan j = null;

        for (Jurusan jurusan : daftarJurusan) {
            if (jurusan.kode.equalsIgnoreCase(input)) {
                j = jurusan;
                break;
            }
        }

        if (j == null) {
            input("Tidak ditemukan");
            return;
        }
        String teks = "===Daftar Mata Kuliah=== \n";
        if (j.mataKuliahList.size() == 0) {
            teks += "Kosong!";
            input(teks);
            return;
        }
        for (int i = 0; i < j.mataKuliahList.size(); i++) {
            MataKuliah mk = j.mataKuliahList.get(i);
            teks += mk.kode + " - " + mk.nama + "\n";
        }
        teks += "Masukkan kode MK:";
        String pilih = inputUser(teks);

        boolean ada = false;
        for (MataKuliah mk : j.mataKuliahList) {
            if (mk.kode.equalsIgnoreCase(pilih)) {
                j.mataKuliahList.remove(mk);
                ada = true;
                break;
            }
        }

        if (ada) {
            input("Berhasil dihapus!");
        } else {
            input("Tidak ketemu!");
        }
    }

    public static void ambilMK() {
        String nim = inputUser("Masukkan NIM Mahasiswa:");
        Mahasiswa mhs = cariNIM(nim);

        if (mhs == null) {
            input("Mahasiswa tidak ditemukan!");
            return;
        }

        if (mhs.indeksNilai == null) {
            mhs.indeksNilai = new HashMap<>();
        }

        String teks = "=== Daftar Mata Kuliah Jurusan " + mhs.jurusan.nama + " ===\n";
        for (MataKuliah mk : mhs.jurusan.mataKuliahList) {
            teks += mk.kode + " - " + mk.nama + " (" + mk.sks + " SKS)\n";
        }
        String pilih = inputUser(teks + "\nHai," + mhs.nama+ "\nMasukkan kode MK yang mau diambil:");

        MataKuliah ambil = null;
        for (MataKuliah mk : mhs.jurusan.mataKuliahList) {
            if (mk.kode.equalsIgnoreCase(pilih)) {
                ambil = mk;
                break;
            }
        }

        if (ambil == null) {
            input("Kode MK tidak ditemukan!");
        } else {
            if (mhs.indeksNilai.containsKey(ambil)) {
                input("Mata kuliah sudah diambil sebelumnya!");
            } else {
                mhs.indeksNilai.put(ambil, "belum ada");
                input("Berhasil mengambil mata kuliah: " + ambil.nama);
            }
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

        listJurusan();
        String pilihan = inputUser("Masukkan kode jurusan:");
        Jurusan jur = null;

        for (Jurusan j : daftarJurusan) {
            if (j.kode.equalsIgnoreCase(pilihan)) {
                jur = j;
                break;
            }
        }

        if (jur == null) {
            input("Tidak ada");
            return;
        }

        Mahasiswa mhs = new Mahasiswa();
        mhs.nim = nim;
        mhs.nama = nama;
        mhs.jurusan = jur;
        daftarMahasiswa.add(mhs);
        input("Berhasil");
    }

    public static void listMahasiswa() {
        if (daftarMahasiswa.size() == 0) {
            input("Kosong!");
        } else {
            String teks = "===Daftar Mahasiswa===\n";
            for (Mahasiswa mhs : daftarMahasiswa) {
                teks += mhs.nim + " - " + mhs.nama + " - " + mhs.jurusan.nama + "\n";
            }
            input(teks);
        }
    }

    // cari nim
    public static Mahasiswa cariNIM(String n) {
        Mahasiswa mhs = null;

        for (Mahasiswa m : daftarMahasiswa) {
            if (m.nim.equals(n)) {
                mhs = m;
                break;
            }
        }
        return mhs;
    }

    public static void detailMahasiswa() {
        String pilih = inputUser("Masukkan nim");
        Mahasiswa mhs = cariNIM(pilih);

        String teks = "===Detail Mahasiswa===\n";
        if (mhs == null) {
            teks += "Tidak ditemukan!";
        } else {
            teks += "NIM = " + mhs.nim + "\n Nama = " + mhs.nama + "\n Jurusan = " + mhs.jurusan.nama + "\n";
            if (mhs.indeksNilai == null || mhs.indeksNilai.isEmpty()) {
                teks += "Belum ada mata kuliah dan nilai";
            } else {
                for (Map.Entry<MataKuliah, String> list : mhs.indeksNilai.entrySet()) {
                    teks += list.getKey().nama + " - " + list.getValue() + "\n";
                }
            }
        }

        input(teks);
    }

    // soal d
    public static void beriNilai() {
        String pilih = inputUser("Masukkan nim");
        Mahasiswa mhs = cariNIM(pilih);

        if (mhs == null) {
            input("Tidak ditemukan!");
            return;
        }

        String matkul = inputUser("Masukkan kode MK: ");
        MataKuliah mk = null;
        for (MataKuliah m : mhs.indeksNilai.keySet()) {
            if (m.kode.equalsIgnoreCase(matkul)) {
                mk = m;
                break;
            }
        }

        if (mk == null) {
            input("Tidak mengambil matkul itu");
            return;
            
        }

        String nilai = inputUser("Masukkan nilai: ");
        mhs.indeksNilai.put(mk, nilai);
        input("Berhasil");
    }

    // soal e

    public static void menghitungIP() {
        String pilih = inputUser("Masukkan nim");
        Mahasiswa mhs = cariNIM(pilih);

        if (mhs == null) {
            input("Tidak ditemukan!");
            return;
        }

        double total = 0;
        int totalSKS = 0;
        for (Map.Entry<MataKuliah, String> list : mhs.indeksNilai.entrySet()) {
            if (!list.getValue().equalsIgnoreCase("belum ada")) {
                total += list.getKey().sks * getAngka(list.getValue().toUpperCase());
                totalSKS += list.getKey().sks;
            }              
        }

        double IP = (double) total / totalSKS;
        input("NIM = " + mhs.nim + " \nNama = " + mhs.nama + "\n IP = " + IP);
    }

    public static int getAngka(String i) {
        switch (i) {
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
