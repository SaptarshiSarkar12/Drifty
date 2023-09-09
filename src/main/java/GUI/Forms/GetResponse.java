package GUI.Forms;

import java.util.concurrent.TimeUnit;

import static GUI.Forms.GetResponse.State.*;

class GetResponse {

    enum State {
        YES, NO, LIMBO
    }

    private State answer = LIMBO;

    public boolean isYes() {
        while(answer.equals(LIMBO)) {
            sleep();
        }
        return answer.equals(YES);
    }

    public boolean isNo() {
        while(answer.equals(LIMBO)) {
            sleep();
        }
        return answer.equals(NO);
    }

    public boolean inLimbo() {
        return answer.equals(LIMBO);
    }

    public void setAnswer(boolean answer) {
        this.answer = answer ? YES : NO;
    }

    private void sleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}