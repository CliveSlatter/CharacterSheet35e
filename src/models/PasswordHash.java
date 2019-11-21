package models;

import java.util.Arrays;

public class PasswordHash {
    private byte[] salt;
    private String hash;

    public PasswordHash(byte[] salt, String hash) {
        this.salt = salt;
        this.hash = hash;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public String toString() {
        return "PasswordHash{" +
                ", salt=" + Arrays.toString(salt) +
                ", hash='" + hash + '\'' +
                '}';
    }
}
