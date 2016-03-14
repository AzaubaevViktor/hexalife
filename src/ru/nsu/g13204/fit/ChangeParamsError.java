package ru.nsu.g13204.fit;

public class ChangeParamsError extends Throwable {
    private final String msg;

    ChangeParamsError(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return this.msg;
    }
}
