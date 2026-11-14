package com.mycompany.smartwarung;

import java.io.Serializable;
import java.util.Locale;

public class Transaksi implements Serializable {
    private static final long serialVersionUID = 2L;

    private String tanggal;
    private String namaBarang;
    private double jumlah;     
    private String satuan;      
    private long hargaSatuan;   

    public Transaksi(String tanggal, String namaBarang, double jumlah, String satuan, long hargaSatuan) {
        this.tanggal = tanggal;
        this.namaBarang = namaBarang;
        this.jumlah = jumlah;
        this.satuan = satuan;
        this.hargaSatuan = hargaSatuan;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public double getJumlah() {
        return jumlah;
    }

    public String getSatuan() {
        return satuan;
    }

    public long getHargaSatuan() {
        return hargaSatuan;
    }

    public long getTotal() {
        return Math.round(jumlah * hargaSatuan);
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public void setJumlah(double jumlah) {
        if (jumlah > 0) { 
            this.jumlah = jumlah;
        }
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public void setHargaSatuan(long hargaSatuan) {
        if (hargaSatuan > 0) {
            this.hargaSatuan = hargaSatuan;
        }
    }

    public String getJenis() {
        return "Transaksi";
    }

    public String formatJumlah() {
        if (jumlah == Math.floor(jumlah)) {
            return String.valueOf((long) jumlah) + " " + satuan;
        }
        return jumlah + " " + satuan;
    }

    public static String rupiah(long angka) {
        return String.format(Locale.forLanguageTag("id-ID"), "Rp %,d", angka);
    }

    public void tampilkanInfo() {
        System.out.printf("%-10s | %-11s | %-18s | %-10s | %-12s | %s%n",
                tanggal, getJenis(), namaBarang, formatJumlah(),
                rupiah(hargaSatuan) + "/" + satuan, rupiah(getTotal()));
    }
}
