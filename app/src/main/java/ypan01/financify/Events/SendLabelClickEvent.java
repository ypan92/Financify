package ypan01.financify.Events;

public class SendLabelClickEvent {

    private String labelName;

    public SendLabelClickEvent(String name) {
        labelName = name;
    }

    public String getLabelName() {
        return labelName;
    }

}
