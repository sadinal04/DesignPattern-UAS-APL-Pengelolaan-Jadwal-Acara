import java.util.*;
import java.text.SimpleDateFormat;

// Builder Pattern: Membuat jadwal acara
class JadwalAcara {
    private String namaAcara;
    private String lokasi;
    private String waktu;
    private String tanggal; // Menambahkan atribut tanggal
    private int durasi; // dalam menit
    private String kategoriAcara;
    private String layananTambahan;

    private JadwalAcara(Builder builder) {
        this.namaAcara = builder.namaAcara;
        this.lokasi = builder.lokasi;
        this.waktu = builder.waktu;
        this.tanggal = builder.tanggal;
        this.durasi = builder.durasi;
        this.kategoriAcara = builder.kategoriAcara;
        this.layananTambahan = builder.layananTambahan;
    }

    public String getNamaAcara() {
        return namaAcara;
    }

    public String getLokasi() {
        return lokasi;
    }

    public String getWaktu() {
        return waktu;
    }

    public String getTanggal() {
        return tanggal;
    }

    public int getDurasi() {
        return durasi;
    }

    public String getKategoriAcara() {
        return kategoriAcara;
    }

    public String getLayananTambahan() {
        return layananTambahan;
    }

    public String getDetail() {
        return "Acara: " + namaAcara + ", Lokasi: " + lokasi + ", Tanggal: " + tanggal +
               ", Waktu: " + waktu + ", Durasi: " + durasi + " menit, Kategori: " + kategoriAcara +
               ", Layanan: " + layananTambahan;
    }

    public static class Builder {
        private String namaAcara;
        private String lokasi;
        private String waktu;
        private String tanggal; // Menambahkan atribut tanggal
        private int durasi;
        private String kategoriAcara;
        private String layananTambahan;

        public Builder setNamaAcara(String namaAcara) {
            this.namaAcara = namaAcara;
            return this;
        }

        public Builder setLokasi(String lokasi) {
            this.lokasi = lokasi;
            return this;
        }

        public Builder setWaktu(String waktu) {
            this.waktu = waktu;
            return this;
        }

        public Builder setTanggal(String tanggal) {
            this.tanggal = tanggal; // Menambahkan setter untuk tanggal
            return this;
        }

        public Builder setDurasi(int durasi) {
            this.durasi = durasi;
            return this;
        }

        public Builder setKategoriAcara(String kategoriAcara) {
            this.kategoriAcara = kategoriAcara;
            return this;
        }

        public Builder setLayananTambahan(String layananTambahan) {
            this.layananTambahan = layananTambahan;
            return this;
        }

        public JadwalAcara build() {
            return new JadwalAcara(this);
        }
    }
}

// Chain of Responsibility Pattern: Menangani konflik jadwal
abstract class PenanganKonflik {
    protected PenanganKonflik nextHandler;

    public void setNextHandler(PenanganKonflik handler) {
        this.nextHandler = handler;
    }

    public abstract boolean handle(JadwalAcara acaraBaru, List<JadwalAcara> acaraTersedia);
}

class KonflikWaktu extends PenanganKonflik {
    @Override
    public boolean handle(JadwalAcara acaraBaru, List<JadwalAcara> acaraTersedia) {
        for (JadwalAcara acara : acaraTersedia) {
            // Cek konflik jika Tanggal, Waktu, dan Lokasi sama
            if (acara.getTanggal().equalsIgnoreCase(acaraBaru.getTanggal()) &&
                acara.getWaktu().equalsIgnoreCase(acaraBaru.getWaktu()) &&
                acara.getLokasi().equalsIgnoreCase(acaraBaru.getLokasi())) {
                System.out.println("Konflik ditemukan: " + acaraBaru.getNamaAcara() +
                                   " memiliki tanggal, waktu, dan lokasi yang sama dengan " + acara.getNamaAcara());
                return true;
            }
        }
        if (nextHandler != null) {
            return nextHandler.handle(acaraBaru, acaraTersedia);
        }
        return false;
    }
}

class KonflikTanggal extends PenanganKonflik {
    @Override
    public boolean handle(JadwalAcara acaraBaru, List<JadwalAcara> acaraTersedia) {
        for (JadwalAcara acara : acaraTersedia) {
            // Cek konflik jika tanggal sama
            if (acara.getTanggal().equalsIgnoreCase(acaraBaru.getTanggal())) {
                System.out.println("Konflik ditemukan: " + acaraBaru.getNamaAcara() + 
                                   " memiliki tanggal yang sama dengan " + acara.getNamaAcara());
                return true;
            }
        }
        if (nextHandler != null) {
            return nextHandler.handle(acaraBaru, acaraTersedia);
        }
        return false;
    }
}

// Strategy Pattern: Strategi Layanan Acara
interface StrategiLayanan {
    String getLayanan();
}

class LayananKonser implements StrategiLayanan {
    @Override
    public String getLayanan() {
        return "Audio System, Stage Lighting"; // Layanan konser
    }
}

class LayananPameran implements StrategiLayanan {
    @Override
    public String getLayanan() {
        return "Booth Setup, Exhibition Guides"; // Layanan pameran
    }
}

class LayananWorkshop implements StrategiLayanan {
    @Override
    public String getLayanan() {
        return "Instructor, Materials"; // Layanan workshop
    }
}

class PengaturanLayanan {
    private StrategiLayanan strategiLayanan;

    public PengaturanLayanan(StrategiLayanan strategiLayanan) {
        this.strategiLayanan = strategiLayanan;
    }

    public String getLayanan() {
        return strategiLayanan.getLayanan();
    }
}

// Factory pattern
class LayananFactory {
    public static StrategiLayanan getLayanan(String kategoriAcara) {
        switch (kategoriAcara.toLowerCase()) {
            case "konser":
                return new LayananKonser();
            case "pameran":
                return new LayananPameran();
            case "workshop":
                return new LayananWorkshop();
            default:
                throw new IllegalArgumentException("Kategori tidak dikenal");
        }
    }
}

// Main Class
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<JadwalAcara> acaraTersedia = new ArrayList<>();
        PenanganKonflik handlerWaktu = new KonflikWaktu();
        PenanganKonflik handlerTanggal = new KonflikTanggal();

        while (true) {
            System.out.println("===================================");
            System.out.println("        INPUT JADWAL ACARA         ");
            System.out.println("===================================");

            String namaAcara = inputString(scanner, "Nama Acara: ");
            String lokasi = inputString(scanner, "Lokasi: ");
            String waktu = inputWaktu(scanner);
            String tanggal = inputTanggal(scanner);
            int durasi = inputDurasi(scanner);
            String kategoriAcara = inputKategoriAcara(scanner);

            PengaturanLayanan pengaturanLayanan = new PengaturanLayanan(LayananFactory.getLayanan(kategoriAcara));
            String layananTambahan = pengaturanLayanan.getLayanan();

            JadwalAcara acaraBaru = new JadwalAcara.Builder()
                    .setNamaAcara(namaAcara)
                    .setLokasi(lokasi)
                    .setWaktu(waktu)
                    .setTanggal(tanggal)
                    .setDurasi(durasi)
                    .setKategoriAcara(kategoriAcara)
                    .setLayananTambahan(layananTambahan)
                    .build();

            if (!handlerWaktu.handle(acaraBaru, acaraTersedia)) {
                if(!handlerTanggal.handle(acaraBaru, acaraTersedia)){
                acaraTersedia.add(acaraBaru);
                System.out.println("\n=== Jadwal Berhasil Ditambahkan ===");
                System.out.println(acaraBaru.getDetail());
                }
            }

            System.out.print("\nTambahkan jadwal lagi? (y/n): ");
            String lanjut = scanner.nextLine();
            if (!lanjut.equalsIgnoreCase("y")) {
                break;
            }
        }

        System.out.println("\n===================================");
        System.out.println("      DAFTAR JADWAL ACARA          ");
        System.out.println("===================================");
        for (int i = 0; i < acaraTersedia.size(); i++) {
            System.out.println("Acara " + (i + 1) + ": " + acaraTersedia.get(i).getDetail());
        }
    }

    private static String inputString(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private static String inputWaktu(Scanner scanner) {
        String waktu;
        while (true) {
            System.out.print("Waktu (Pagi/Siang/Malam): ");
            waktu = scanner.nextLine();
            if (waktu.equalsIgnoreCase("Pagi") || waktu.equalsIgnoreCase("Siang") || waktu.equalsIgnoreCase("Malam")) {
                break;
            } else {
                System.out.println("Input waktu tidak valid. Coba lagi.");
            }
        }
        return waktu;
    }

    private static String inputTanggal(Scanner scanner) {
        String tanggal;
        while (true) {
            System.out.print("Tanggal (DD-MM-YYYY): ");
            tanggal = scanner.nextLine();
            if (validasiTanggal(tanggal)) {
                break;
            } else {
                System.out.println("Tanggal tidak valid. Coba lagi.");
            }
        }
        return tanggal;
    }

    private static boolean validasiTanggal(String tanggal) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        format.setLenient(false);
        try {
            format.parse(tanggal);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static int inputDurasi(Scanner scanner) {
        int durasi;
        while (true) {
            System.out.print("Durasi (menit): ");
            try {
                durasi = Integer.parseInt(scanner.nextLine());
                if (durasi > 0) {
                    break;
                } else {
                    System.out.println("Durasi harus lebih dari 0. Coba lagi.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Input durasi tidak valid. Coba lagi.");
            }
        }
        return durasi;
    }

    private static String inputKategoriAcara(Scanner scanner) {
        String kategoriAcara;
        while (true) {
            System.out.print("Kategori Acara (Konser/Pameran/Workshop): ");
            kategoriAcara = scanner.nextLine();
            if (kategoriAcara.equalsIgnoreCase("Konser") || kategoriAcara.equalsIgnoreCase("Pameran") || kategoriAcara.equalsIgnoreCase("Workshop")) {
                break;
            } else {
                System.out.println("Kategori acara tidak valid. Coba lagi.");
            }
        }
        return kategoriAcara;
    }
}

