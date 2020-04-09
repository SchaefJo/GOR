package seqlib;

public class SeqlibEntry {
    private String header;
    private String aminoacid_seq;
    private String secondary_struc_seq;

    public SeqlibEntry(String header, String aminoacid_seq, String secondary_struc_seq) {
        this.header = header;
        this.aminoacid_seq = aminoacid_seq;
        this.secondary_struc_seq = secondary_struc_seq;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getAminoacid_seq() {
        return aminoacid_seq;
    }

    public void setAminoacid_seq(String aminoacid_seq) {
        this.aminoacid_seq = aminoacid_seq;
    }

    public String getSecondary_struc_seq() {
        return secondary_struc_seq;
    }

    public void setSecondary_struc_seq(String secondary_struc_seq) {
        this.secondary_struc_seq = secondary_struc_seq;
    }
}
