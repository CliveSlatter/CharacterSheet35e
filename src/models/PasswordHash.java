package models;

import java.util.Arrays;

public class PasswordHash {
    private int iterations;
    private byte[] salt;
    private String hash;

    public PasswordHash(int iterations, byte[] salt, String hash) {
        this.iterations = iterations;
        this.salt = salt;
        this.hash = hash;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
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
                "iterations=" + iterations +
                ", salt=" + Arrays.toString(salt) +
                ", hash='" + hash + '\'' +
                '}';
    }
}
