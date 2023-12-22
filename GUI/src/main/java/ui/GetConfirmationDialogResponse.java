package ui;

import static ui.GetConfirmationDialogResponse.State.NO;
import static ui.GetConfirmationDialogResponse.State.YES;
import static utils.Utility.sleep;

public class GetConfirmationDialogResponse {
    enum State {
        YES, NO, UNANSWERED
    }

    private State answer = State.UNANSWERED;

    public boolean isYes() {
        while (answer.equals(State.UNANSWERED)) {
            sleep(200);
        }
        return answer.equals(YES);
    }

    public boolean isNo() {
        while (answer.equals(State.UNANSWERED)) {
            sleep(200);
        }
        return answer.equals(NO);
    }

    public boolean isUnanswered() {
        return answer.equals(State.UNANSWERED);
    }

    public void setAnswer(boolean answer) {
        this.answer = answer ? YES : NO;
    }
}
