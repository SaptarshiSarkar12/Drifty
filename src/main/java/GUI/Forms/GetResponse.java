package GUI.Forms;

import java.util.concurrent.TimeUnit;

class GetResponse {

    private boolean answer;

    public boolean isYes() {
        while(AskYesNo.waiting()) {
            sleep(200);
        }
        return answer;
    }

    public boolean isNo() {
        while(AskYesNo.waiting()) {
            sleep(200);
        }
        return !answer;
    }

    public void setAnswer(boolean answer) {
        this.answer = answer;
    }

    private void sleep(long time) {
        try {
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
