package com.mycompany.smartwarung;

import java.io.*;
import java.util.ArrayList;

public class ManajerKeuangan {
    private ArrayList<Transaksi> daftar = new ArrayList<>();
    private final String fileData;

    public ManajerKeuangan(String fileData) {
        this.fileData = fileData;
    }

    public void tambah(Transaksi t) {
        daftar.add(t);
    }

    public ArrayList<Transaksi> getDaftar() {
        return daftar;
    }

    public long hitungTotalPemasukan() {
        long total = 0;
        for (Transaksi t : daftar) {
            if (t instanceof Pemasukan) total += t.getTotal();
        }
        return total;
    }

    public long hitungTotalPengeluaran() {
        long total = 0;
        for (Transaksi t : daftar) {
            if (t instanceof Pengeluaran) total += t.getTotal();
        }
        return total;
    }

    public long hitungSaldo() {
        return hitungTotalPemasukan() - hitungTotalPengeluaran();
    }

    public String getInsight() {
        long masuk = hitungTotalPemasukan();
        long keluar = hitungTotalPengeluaran();
        if (masuk > keluar) {
            return "Keuangan usaha menunjukkan saldo positif. Pertahankan pengelolaan pengeluaran dan pantau perkembangan pemasukan.";
        } else if (keluar > masuk) {
            return "Perhatian! Pengeluaran lebih besar daripada pemasukan. Evaluasi biaya operasional dan pengeluaran usaha.";
        } else {
            return "Pemasukan dan pengeluaran berada pada jumlah yang sama. Tingkatkan pemasukan atau lakukan efisiensi pengeluaran.";
        }
    }

    public void tampilkanDashboard() {
        System.out.println("\n=== DASHBOARD SMARTWARUNG ===");
        System.out.println("Total Pemasukan   : " + Transaksi.rupiah(hitungTotalPemasukan()));
        System.out.println("Total Pengeluaran : " + Transaksi.rupiah(hitungTotalPengeluaran()));
        System.out.println("Saldo             : " + Transaksi.rupiah(hitungSaldo()));
        System.out.println("Smart Financial Insight:");
        System.out.println("-> " + getInsight());
    }

    public void tampilkanRiwayat() {
        System.out.println("\n=== RIWAYAT TRANSAKSI ===");
        if (daftar.isEmpty()) {
            System.out.println("Belum ada transaksi.");
            return;
        }
        System.out.printf("%-3s %-10s | %-11s | %-18s | %-10s | %-12s | %s%n",
                "No", "Tanggal", "Jenis", "Barang", "Jumlah", "Harga/Satuan", "Total");
        int no = 1;
        for (Transaksi t : daftar) {
            System.out.printf("%-3d ", no++);
            t.tampilkanInfo();
        }
    }

    public void simpanKeFile() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileData))) {
            out.writeObject(daftar);
            System.out.println("Data berhasil disimpan ke " + fileData);
        } catch (IOException e) {
            System.out.println("Gagal menyimpan data: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void muatDariFile() {
        File f = new File(fileData);
        if (!f.exists()) {
            return; 
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
            daftar = (ArrayList<Transaksi>) in.readObject();
            System.out.println("Data lama dimuat: " + daftar.size() + " transaksi.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Gagal memuat data (file lama tidak cocok, hapus data_smartwarung.dat): " + e.getMessage());
        }
    }
}
