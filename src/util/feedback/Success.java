package util.feedback;

public class Success<V, R> extends FeedbackResponse<V> implements Feedback<V, R> {

    public Success(V res) {
        super(res);
    }

    @Override
    public final Feedback<V, R> onSuccess(FeedbackReceivedCallback<V> feedback) {
        feedback.onFeedbackReceived(this.res);
        return this;
    }

    @Override
    public final Feedback<V, R> onFailure(FeedbackReceivedCallback<R> feedback) {
        return this;
    }
}
