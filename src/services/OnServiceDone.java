package services;

public interface OnServiceDone<Result> {

    void done(Result r);
}
