/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartwarung;


public class Pengeluaran extends Transaksi {
    private static final long serialVersionUID = 2L;

    public Pengeluaran(String tanggal, String namaBarang, double jumlah, String satuan, long hargaSatuan) {
        super(tanggal, namaBarang, jumlah, satuan, hargaSatuan);
    }

    @Override
    public String getJenis() {
        return "Pengeluaran";
    }
}
