package io.github.zrdzn.minecraft.lizardauth.configuration;

public record PasswordSettings(int minimumPasswordLength, int maximumPasswordLength) {

    public int getMinimumPasswordLength() {
        return this.minimumPasswordLength;
    }

    public int getMaximumPasswordLength() {
        return this.maximumPasswordLength;
    }

}
