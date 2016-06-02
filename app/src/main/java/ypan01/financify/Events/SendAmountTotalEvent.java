package ypan01.financify.Events;

/**
 * Created by yangpan on 6/1/16.
 */
public class SendAmountTotalEvent {

    private double withdrawAmount;
    private double depositAmount;

    public SendAmountTotalEvent(double withdraw, double deposit) {
        withdrawAmount = withdraw;
        depositAmount = deposit;
    }

    public double getWithdrawAmount() {
        return withdrawAmount;
    }

    public double getDepositAmount() {
        return depositAmount;
    }

    public double getNetAmount() {
        return depositAmount - withdrawAmount;
    }

}
