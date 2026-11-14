package com.mycompany.smartwarung;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Optional;
import java.util.function.Function;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class App extends Application {

    private static final DateTimeFormatter FORMAT_TANGGAL =
            DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);

    // Objek dari base code (Tugas 4)
    private final ManajerKeuangan manajer = new ManajerKeuangan("data_smartwarung.dat");
    private final ObservableList<Transaksi> dataTabel = FXCollections.observableArrayList();

    // ===== Komponen Form (Tahap 1) =====
    private final TextField tfTanggal = new TextField();
    private final TextField tfBarang = new TextField();
    private final TextField tfJumlah = new TextField();
    private final ComboBox<String> cbSatuan = new ComboBox<>();
    private final TextField tfHarga = new TextField();
    private final Label lblEstimasi = new Label("Rp 0");

    // ===== Komponen Dashboard =====
    private final Label lblPemasukan = new Label();
    private final Label lblPengeluaran = new Label();
    private final Label lblSaldo = new Label();
    private final Label lblInsight = new Label();
    private final Label lblStatus = new Label();

    private final TableView<Transaksi> tabel = new TableView<>();

    @Override
    public void start(Stage stage) {
        manajer.muatDariFile();
        dataTabel.setAll(manajer.getDaftar());

        // ---------- Judul ----------
        Label lblJudul = new Label("SmartWarung - Pencatatan Keuangan Warung");
        lblJudul.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        // ---------- Dashboard ----------
        HBox dashboard = new HBox(12,
                buatKartu("Total Pemasukan", lblPemasukan, "#1e8449"),
                buatKartu("Total Pengeluaran", lblPengeluaran, "#c0392b"),
                buatKartu("Saldo", lblSaldo, "#2c3e50"));

        lblInsight.setWrapText(true);
        lblInsight.setStyle("-fx-padding: 8; -fx-background-color: #eef3f8; -fx-background-radius: 6;");
        lblInsight.setMaxWidth(Double.MAX_VALUE);

        // ---------- Form Input (GridPane) ----------
        tfTanggal.setText(LocalDate.now().format(FORMAT_TANGGAL));
        tfBarang.setPromptText("contoh: Gula pasir");
        tfJumlah.setPromptText("contoh: 1, 100, 0.5");
        cbSatuan.setEditable(true);
        cbSatuan.getItems().addAll("pcs", "gram", "kg", "liter", "bungkus", "dus");
        cbSatuan.setValue("pcs");
        cbSatuan.setMaxWidth(Double.MAX_VALUE);
        tfHarga.setPromptText("harga per satuan, angka saja");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        ColumnConstraints cLabel = new ColumnConstraints();
        ColumnConstraints cField = new ColumnConstraints();
        cField.setHgrow(Priority.ALWAYS);
        form.getColumnConstraints().addAll(cLabel, cField, cLabel, cField);

        form.add(new Label("Tanggal (dd-mm-yyyy)"), 0, 0);
        form.add(tfTanggal, 1, 0);
        form.add(new Label("Nama Barang"), 2, 0);
        form.add(tfBarang, 3, 0);
        form.add(new Label("Jumlah"), 0, 1);
        form.add(tfJumlah, 1, 1);
        form.add(new Label("Satuan"), 2, 1);
        form.add(cbSatuan, 3, 1);
        form.add(new Label("Harga per Satuan (Rp)"), 0, 2);
        form.add(tfHarga, 1, 2);
        form.add(new Label("Estimasi Total"), 2, 2);
        form.add(lblEstimasi, 3, 2);
        lblEstimasi.setStyle("-fx-font-weight: bold;");

        // Estimasi total otomatis saat jumlah/harga diketik
        tfJumlah.textProperty().addListener((o, lama, baru) -> perbaruiEstimasi());
        tfHarga.textProperty().addListener((o, lama, baru) -> perbaruiEstimasi());

        // ---------- Tombol (HBox) ----------
        Button btnMasuk = new Button("Catat Pemasukan");
        Button btnKeluar = new Button("Catat Pengeluaran");
        Button btnHapus = new Button("Hapus Terpilih");
        Button btnReset = new Button("Kosongkan Form");

        // ===== Tahap 2: Event Handling (setOnAction) =====
        btnMasuk.setOnAction(e -> prosesTransaksi(true));
        btnKeluar.setOnAction(e -> prosesTransaksi(false));
        btnHapus.setOnAction(e -> hapusTerpilih());
        btnReset.setOnAction(e -> kosongkanForm());

        HBox barisTombol = new HBox(10, btnMasuk, btnKeluar, btnHapus, btnReset);
        barisTombol.setAlignment(Pos.CENTER_LEFT);

        lblStatus.setStyle("-fx-text-fill: #555555;");

        // ---------- Tabel Riwayat ----------
        siapkanTabel();
        VBox.setVgrow(tabel, Priority.ALWAYS);

        VBox root = new VBox(12, lblJudul, dashboard, lblInsight, form, barisTombol, lblStatus,
                new Label("Riwayat Transaksi"), tabel);
        root.setPadding(new Insets(16));

        perbaruiDashboard();

        stage.setTitle("SmartWarung");
        stage.setScene(new Scene(root, 900, 700));
        stage.setOnCloseRequest(e -> manajer.simpanKeFile());
        stage.show();
    }

    // ================= Tahap 2 + 3: proses simpan dengan validasi =================
    private void prosesTransaksi(boolean pemasukan) {
        String tgl = tfTanggal.getText().trim();
        String barang = tfBarang.getText().trim();
        String sJumlah = tfJumlah.getText().trim().replace(',', '.');
        String satuan = cbSatuan.getEditor().getText().trim();
        String sHarga = tfHarga.getText().trim();

        // Tahap 3: Alert jika ada kolom yang belum diisi
        if (tgl.isEmpty() || barang.isEmpty() || sJumlah.isEmpty() || satuan.isEmpty() || sHarga.isEmpty()) {
            tampilkanAlert(AlertType.WARNING, "Data Belum Lengkap",
                    "Semua kolom wajib diisi sebelum transaksi dicatat.");
            return;
        }

        // Tahap 3: validasi format tanggal
        try {
            LocalDate.parse(tgl, FORMAT_TANGGAL);
        } catch (DateTimeParseException ex) {
            tampilkanAlert(AlertType.ERROR, "Format Tanggal Salah",
                    "Gunakan format dd-mm-yyyy, contoh: 14-11-2026.");
            return;
        }

        // Tahap 3: try-catch jika user salah memasukkan format angka
        try {
            double jumlah = Double.parseDouble(sJumlah);
            long harga = Long.parseLong(sHarga);

            if (!Double.isFinite(jumlah) || jumlah <= 0 || harga <= 0) {
                tampilkanAlert(AlertType.WARNING, "Nilai Tidak Valid",
                        "Jumlah dan harga harus lebih dari 0.");
                return;
            }

            // Instansiasi objek dari base code (Inheritance: Pemasukan / Pengeluaran extends Transaksi)
            Transaksi t = pemasukan
                    ? new Pemasukan(tgl, barang, jumlah, satuan, harga)
                    : new Pengeluaran(tgl, barang, jumlah, satuan, harga);

            manajer.tambah(t);
            manajer.simpanKeFile();
            dataTabel.setAll(manajer.getDaftar());
            perbaruiDashboard();
            kosongkanForm();
            lblStatus.setText((pemasukan ? "Pemasukan" : "Pengeluaran") + " dicatat: "
                    + barang + " (" + t.formatJumlah() + ") = " + Transaksi.rupiah(t.getTotal()));
        } catch (NumberFormatException ex) {
            tampilkanAlert(AlertType.ERROR, "Format Angka Salah",
                    "Jumlah dan harga harus berupa angka.\n"
                    + "Jumlah boleh desimal (0.5). Harga angka bulat tanpa titik/koma, contoh: 15000.");
        }
    }

    private void hapusTerpilih() {
        Transaksi dipilih = tabel.getSelectionModel().getSelectedItem();
        if (dipilih == null) {
            tampilkanAlert(AlertType.INFORMATION, "Belum Ada yang Dipilih",
                    "Klik salah satu baris di tabel dulu, lalu tekan Hapus Terpilih.");
            return;
        }
        Alert konfirmasi = new Alert(AlertType.CONFIRMATION,
                "Hapus transaksi \"" + dipilih.getNamaBarang() + "\"?", ButtonType.YES, ButtonType.NO);
        konfirmasi.setTitle("Konfirmasi Hapus");
        konfirmasi.setHeaderText(null);
        Optional<ButtonType> jawab = konfirmasi.showAndWait();
        if (jawab.isPresent() && jawab.get() == ButtonType.YES) {
            manajer.getDaftar().remove(dipilih);
            manajer.simpanKeFile();
            dataTabel.setAll(manajer.getDaftar());
            perbaruiDashboard();
            lblStatus.setText("Transaksi dihapus.");
        }
    }

    // ================= Helper UI =================
    private void kosongkanForm() {
        tfTanggal.setText(LocalDate.now().format(FORMAT_TANGGAL));
        tfBarang.clear();
        tfJumlah.clear();
        tfHarga.clear();
        cbSatuan.setValue("pcs");
        lblEstimasi.setText("Rp 0");
    }

    private void perbaruiEstimasi() {
        try {
            double jumlah = Double.parseDouble(tfJumlah.getText().trim().replace(',', '.'));
            long harga = Long.parseLong(tfHarga.getText().trim());
            lblEstimasi.setText(Transaksi.rupiah(Math.round(jumlah * harga)));
        } catch (NumberFormatException ex) {
            lblEstimasi.setText("Rp 0"); // input belum lengkap/valid, abaikan
        }
    }

    private void perbaruiDashboard() {
        lblPemasukan.setText(Transaksi.rupiah(manajer.hitungTotalPemasukan()));
        lblPengeluaran.setText(Transaksi.rupiah(manajer.hitungTotalPengeluaran()));
        long saldo = manajer.hitungSaldo();
        lblSaldo.setText(Transaksi.rupiah(saldo));
        lblSaldo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: "
                + (saldo < 0 ? "#c0392b" : "#1e8449") + ";");
        lblInsight.setText("Smart Financial Insight: " + manajer.getInsight());
    }

    private VBox buatKartu(String judul, Label nilai, String warna) {
        Label lblJudulKartu = new Label(judul);
        lblJudulKartu.setStyle("-fx-text-fill: #555555;");
        nilai.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + warna + ";");
        VBox kartu = new VBox(4, lblJudulKartu, nilai);
        kartu.setPadding(new Insets(10, 14, 10, 14));
        kartu.setStyle("-fx-background-color: #f6f6f6; -fx-background-radius: 6; "
                + "-fx-border-color: #dddddd; -fx-border-radius: 6;");
        HBox.setHgrow(kartu, Priority.ALWAYS);
        kartu.setPrefWidth(200);
        kartu.setMaxWidth(Double.MAX_VALUE);
        return kartu;
    }

    private void siapkanTabel() {
        tabel.getColumns().add(kolom("Tanggal", 100, Transaksi::getTanggal));
        tabel.getColumns().add(kolom("Jenis", 100, Transaksi::getJenis));
        tabel.getColumns().add(kolom("Barang", 200, Transaksi::getNamaBarang));
        tabel.getColumns().add(kolom("Jumlah", 100, Transaksi::formatJumlah));
        tabel.getColumns().add(kolom("Harga/Satuan", 130,
                t -> Transaksi.rupiah(t.getHargaSatuan()) + "/" + t.getSatuan()));
        tabel.getColumns().add(kolom("Total", 130, t -> Transaksi.rupiah(t.getTotal())));
        tabel.setItems(dataTabel);
        tabel.setPlaceholder(new Label("Belum ada transaksi."));
    }

    private TableColumn<Transaksi, String> kolom(String judul, double lebar, Function<Transaksi, String> isi) {
        TableColumn<Transaksi, String> k = new TableColumn<>(judul);
        k.setCellValueFactory(c -> new SimpleStringProperty(isi.apply(c.getValue())));
        k.setPrefWidth(lebar);
        return k;
    }

    private void tampilkanAlert(AlertType tipe, String judul, String pesan) {
        Alert alert = new Alert(tipe);
        alert.setTitle(judul);
        alert.setHeaderText(judul);
        alert.setContentText(pesan);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
