package com.mycompany.smartwarung;

public class Pemasukan extends Transaksi {
    private static final long serialVersionUID = 2L;

    public Pemasukan(String tanggal, String namaBarang, double jumlah, String satuan, long hargaSatuan) {
        super(tanggal, namaBarang, jumlah, satuan, hargaSatuan);
    }

    @Override
    public String getJenis() {
        return "Pemasukan";
    }
}
