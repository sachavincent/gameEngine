package util.feedback;

public abstract class FeedbackResponse<V> {

    protected final V res;

    public FeedbackResponse(V res) {
        this.res = res;
    }
}
