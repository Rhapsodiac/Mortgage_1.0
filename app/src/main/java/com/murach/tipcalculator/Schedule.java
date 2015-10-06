package com.murach.tipcalculator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.TextView.OnEditorActionListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by Amanuel on 10/5/2015.
 */
public class Schedule extends Activity
implements OnEditorActionListener {

    private TableLayout tlSchedule;
    private DecimalFormat df;
    double currentBal;
    int yearsToRepay;
    double contribToPrincipal;
    int numberOfPayments;
    double montlyPayment;
    double montlyInterestRate;
    int paymentNum = 1;
    TextView scMonthlyPaymentAmount;
    EditText addedMonthlyPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_activity);

        Intent i = getIntent();
        tlSchedule = (TableLayout) findViewById(R.id.tlschedule);

        Mortgage mor = (Mortgage) i.getSerializableExtra("MortgageCalculator");

        currentBal = mor.loanAmount - mor.downPayment;
        yearsToRepay = mor.totalYearsToRepay;
        contribToPrincipal = mor.totalPayBack();
        numberOfPayments = mor.numberOfPayments;
        montlyPayment = mor.monthlyPayment();
        montlyInterestRate = mor.monthlyInterestRate();
        paymentNum = 1;

        scMonthlyPaymentAmount = (TextView) findViewById(R.id.schMonthlyPaymentAmount);
        addedMonthlyPayment = (EditText) findViewById(R.id.addedMonthlyPayment);

        addedMonthlyPayment.setOnEditorActionListener(this);

        df = new DecimalFormat("#.00");
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);

        displayTable();
    }

    protected void displayTable(){

        while (numberOfPayments > 0){
            double interest = montlyInterestRate*currentBal;
            contribToPrincipal = montlyPayment - interest;

            currentBal -= contribToPrincipal;

            TextView tvPmtNo = new TextView(this);
            TextView tvPmtAmt = new TextView(this);
            TextView tvInterest = new TextView(this);
            TextView tvContribToPrin = new TextView(this);
            TextView tvCurrBal = new TextView(this);

            TableRow tr1 = new TableRow(this);

            tvPmtNo.setText(String.valueOf(paymentNum));
            tvPmtAmt.setText(df.format(montlyPayment));
            tvInterest.setText(df.format(interest));
            tvContribToPrin.setText(df.format(contribToPrincipal));
            tvCurrBal.setText(df.format(currentBal));

            tr1.addView(tvPmtNo);
            tr1.addView(tvPmtAmt);
            tr1.addView(tvInterest);
            tr1.addView(tvContribToPrin);
            tr1.addView(tvCurrBal);

            tlSchedule.addView(tr1);

            paymentNum++;
            numberOfPayments--;

        }
    }
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        String addedMonthlyPayString = "";

        if (actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {


            addedMonthlyPayString = addedMonthlyPayment.getText().toString();
            if (addedMonthlyPayString.equals("")) {
                addedMonthlyPayment.setText("$0.00");
            } else {

                NumberFormat currency = NumberFormat.getCurrencyInstance();
                currency.setMaximumFractionDigits(2);
                currency.setMinimumFractionDigits(2);

                double addedPay = Double.parseDouble(addedMonthlyPayString);

                if (addedPay + montlyPayment > currentBal) {
                    addedPay = currentBal - montlyPayment;
                    addedMonthlyPayment.setText(currency.format(addedPay));
                }

                montlyPayment += addedPay;

                displayTable();
                scMonthlyPaymentAmount.setText(currency.format(montlyPayment));
            }
        }
        return false;
     }
}
