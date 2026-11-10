package com.mycompany.smartwarung;

import java.util.Scanner;

public class App {

    private static final String[] SATUAN = {"pcs", "gram", "kg", "liter", "bungkus", "dus"};

    // Baca jumlah (boleh desimal, mis. 0.5 atau 0,5)
    private static double bacaJumlah(Scanner sc) {
        while (true) {
            System.out.print("Jumlah (contoh: 1, 100, 0.5): ");
            String s = sc.nextLine().trim().replace(',', '.');
            try {
                double n = Double.parseDouble(s);
                if (n > 0) return n;
                System.out.println("Jumlah harus lebih dari 0.");
            } catch (NumberFormatException e) {
                System.out.println("Input bukan angka, coba lagi.");
            }
        }
    }

    // Pilih satuan dari daftar, atau ketik sendiri
    private static String bacaSatuan(Scanner sc) {
        while (true) {
            System.out.println("Satuan:");
            for (int i = 0; i < SATUAN.length; i++) {
                System.out.println("  " + (i + 1) + ". " + SATUAN[i]);
            }
            System.out.println("  " + (SATUAN.length + 1) + ". Lainnya (ketik sendiri)");
            System.out.print("Pilih satuan: ");
            String s = sc.nextLine().trim();
            try {
                int p = Integer.parseInt(s);
                if (p >= 1 && p <= SATUAN.length) return SATUAN[p - 1];
                if (p == SATUAN.length + 1) {
                    System.out.print("Ketik satuan: ");
                    String lain = sc.nextLine().trim();
                    if (!lain.isEmpty()) return lain;
                }
                System.out.println("Pilihan tidak valid.");
            } catch (NumberFormatException e) {
                System.out.println("Masukkan nomor pilihan.");
            }
        }
    }

    private static long bacaHarga(Scanner sc, String satuan) {
        while (true) {
            System.out.print("Harga per " + satuan + " (angka saja): ");
            String s = sc.nextLine().trim();
            try {
                long n = Long.parseLong(s);
                if (n > 0) return n;
                System.out.println("Harga harus lebih dari 0.");
            } catch (NumberFormatException e) {
                System.out.println("Input bukan angka, coba lagi.");
            }
        }
    }

    // Input data transaksi, lalu kembalikan sebagai objek Pemasukan / Pengeluaran
    private static Transaksi inputTransaksi(Scanner sc, boolean pemasukan) {
        System.out.print("Tanggal (dd-mm-yyyy): ");
        String tgl = sc.nextLine().trim();
        System.out.print("Nama barang: ");
        String barang = sc.nextLine().trim();
        double jumlah = bacaJumlah(sc);
        String satuan = bacaSatuan(sc);
        long harga = bacaHarga(sc, satuan);
        Transaksi t = pemasukan
                ? new Pemasukan(tgl, barang, jumlah, satuan, harga)
                : new Pengeluaran(tgl, barang, jumlah, satuan, harga);
        System.out.println("Total: " + Transaksi.rupiah(t.getTotal()));
        return t;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ManajerKeuangan manajer = new ManajerKeuangan("data_smartwarung.dat");
        manajer.muatDariFile();

        System.out.println("=====================================");
        System.out.println("  SMARTWARUNG - Pencatatan Keuangan  ");
        System.out.println("=====================================");

        boolean jalan = true;
        while (jalan) {
            System.out.println("\n--- MENU ---");
            System.out.println("1. Dashboard Keuangan");
            System.out.println("2. Catat Pemasukan (barang terjual)");
            System.out.println("3. Catat Pengeluaran (barang dibeli)");
            System.out.println("4. Riwayat Transaksi");
            System.out.println("5. Simpan & Keluar");
            System.out.print("Pilih menu (1-5): ");
            String pilihan = sc.nextLine().trim();

            switch (pilihan) {
                case "1":
                    manajer.tampilkanDashboard();
                    break;
                case "2":
                    manajer.tambah(inputTransaksi(sc, true));
                    System.out.println("Pemasukan berhasil dicatat.");
                    break;
                case "3":
                    manajer.tambah(inputTransaksi(sc, false));
                    System.out.println("Pengeluaran berhasil dicatat.");
                    break;
                case "4":
                    manajer.tampilkanRiwayat();
                    break;
                case "5":
                    manajer.simpanKeFile();
                    System.out.println("Terima kasih, sampai jumpa!");
                    jalan = false;
                    break;
                default:
                    System.out.println("Menu tidak valid, pilih 1-5.");
            }
        }
        sc.close();
    }
}
