package com.murach.tipcalculator;

import java.text.NumberFormat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class MortgageCalculator extends Activity
implements OnSeekBarChangeListener, OnEditorActionListener, OnClickListener,
        OnCheckedChangeListener, OnItemSelectedListener{

    // define variables for the widgets
    private EditText principalAmountStringEditText;
    private EditText interestEditText;
    private EditText downPaymentEditText;
    private Button   calculateMortgageButton;
    private Button   tableButton;
    private TextView monthlyPaymentAmount;
    private TextView totalAmountAmount;
    private TextView termAmount;
    private RadioButton yearButton15;
    private RadioButton yearButton30;
    private RadioGroup yearGroup;
    private int numPayments;
    private Spinner savedStateSpinner;
    private ArrayAdapter<CharSequence> adapter;
    private SeekBar interestBar;
    private NumberFormat percent;
    private NumberFormat currency;
    private NumberFormat number;
    private SavedState savedStates[];
    private Mortgage m;
    
    // define the SharedPreferences object
    private SharedPreferences savedValues;
    
    // define instance variables that should be saved
    private String principalAmountString = "", interestString = "", downPaymentString = "";
    private double interest = .0475, downPayment;
    private int currentSavedState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mortgage_calculator_main);
        
        // get references to the widgets
        principalAmountStringEditText = (EditText) findViewById(R.id.principalEditText);
        interestEditText = (EditText) findViewById(R.id.interestEditText);
        downPaymentEditText = (EditText) findViewById(R.id.downPaymentEditText);
        calculateMortgageButton = (Button) findViewById(R.id.calculateMortgageButton);
        tableButton = (Button) findViewById(R.id.tableButton);
        monthlyPaymentAmount = (TextView) findViewById(R.id.schMonthlyPaymentAmount);
        totalAmountAmount = (TextView) findViewById(R.id.totalMortgageAmount);
        termAmount = (TextView) findViewById(R.id.termAmount);
        yearButton15 = (RadioButton) findViewById(R.id.rButton15Year);
        yearButton30 = (RadioButton) findViewById(R.id.rButton30Year);
        yearGroup = (RadioGroup) findViewById(R.id.yearGroup);
        savedStateSpinner = (Spinner) findViewById(R.id.savedStateSpinner);
        interestBar = (SeekBar) findViewById(R.id.interestBar);
        yearButton15 = (RadioButton) findViewById(R.id.rButton15Year);
        yearButton30 = (RadioButton) findViewById(R.id.rButton30Year);


        adapter = ArrayAdapter.createFromResource(this, R.array.mortgage_state_array,
                                                  android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        savedStateSpinner.setAdapter(adapter);

        savedStates = new SavedState[5];

        percent = NumberFormat.getPercentInstance();
        percent.setMaximumFractionDigits(2);
        percent.setMinimumFractionDigits(2);

        number = NumberFormat.getNumberInstance();
        number.setMaximumFractionDigits(2);
        number.setMinimumFractionDigits(2);

        currency = NumberFormat.getCurrencyInstance();

        currentSavedState = 0;
        numPayments = 360;

        //m = new Mortgage();

        // set the listeners
        principalAmountStringEditText.setOnEditorActionListener(this);
        interestEditText.setOnEditorActionListener(this);
        downPaymentEditText.setOnEditorActionListener(this);
        calculateMortgageButton.setOnClickListener(this);
        tableButton.setOnClickListener(this);
        yearGroup.setOnCheckedChangeListener(this);
        interestBar.setOnSeekBarChangeListener(this);
        savedStateSpinner.setOnItemSelectedListener(this);
        
        // get SharedPreferences object
        savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);
    }
    
    @Override
    public void onPause() {
        // save the instance variables       
        Editor editor = savedValues.edit();        
        editor.putString("principalAmountString", principalAmountString);
        editor.putString("interestString", interestString);
        //editor.putFloat("tipPercent", tipPercent);
        editor.commit();        

        super.onPause();      
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
        // get the instance variables
        principalAmountString = savedValues.getString("principalAmountString", "");
        interestString = savedValues.getString("interestString", "");
        //interest = savedValues.getFloat("tipPercent", 0.0475);

        // set the bill amount on its widget
        principalAmountStringEditText.setText(principalAmountString);
        
        // calculate and display
        //calculateAndDisplay();
    }    
    
    public void calculateAndDisplay() {

        double principal;
        double term;

        // get the bill amount
        principalAmountString = principalAmountStringEditText.getText().toString();

        if (principalAmountString.equals("")) {
            principal = 0;
        }
        else {
            principal = Double.parseDouble(principalAmountString);
            principal = principal - principal * downPayment;
        }

        term = Math.pow((1 + interest/12), numPayments);
        // calculate tip and total
        double totalAmount = (principal * (interest/12) * term)/(term-1);
        
        // display the other results with formatting
        currency = NumberFormat.getCurrencyInstance();
        number = NumberFormat.getNumberInstance();

        monthlyPaymentAmount.setText(currency.format(totalAmount));
        termAmount.setText(number.format(term));
        totalAmountAmount.setText(currency.format(totalAmount * numPayments));

        interestEditText.setText(percent.format(interest));

         m = new Mortgage(principal, (float)interest, numPayments/12, principal * downPayment);
    }
    
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE ||
            actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
            

                    
                    interestString = interestEditText.getText().toString();
                    if (interestString.equals("")) {
                        interest = .0475;
                    }
                    else {
                        interest = Float.parseFloat(interestString.replace('%', '0')) / 100;
                        if(interest <= .00)
                            interest = .00;
                        else if(interest >= 1) {
                            interest = 1;
                        }
                        interestEditText.setText(percent.format(interest));
                        interestBar.setProgress((int)(interest * 100));
                    }

                    downPaymentString = downPaymentEditText.getText().toString();
                    if (downPaymentString.equals("")) {
                        downPayment = 0.00;
                    }
                    else {
                        downPayment = Float.parseFloat(downPaymentString.replace('%', '0')) / 100;
                        if(downPayment <= .00)
                            downPayment = .00;
                        else if(downPayment >= .15) {
                            downPayment = .15;
                        }
                        downPaymentEditText.setText(percent.format(downPayment));

                    }
        }        
        return false;
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.calculateMortgageButton:
                calculateAndDisplay();
                break;
            case R.id.tableButton:
                Intent intent = new Intent(MortgageCalculator.this, Schedule.class);
                intent.putExtra("MortgageCalculator", m);
                startActivity(intent);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rButton15Year:
                    numPayments = 180;
                break;
            case R.id.rButton30Year:
                    numPayments = 360;
        }
        Log.i("onClick", "value: " + interest);
    }

    @Override
    public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {

        if(fromUser) {
            if(interestBar.getProgress() == 15)
                interest = .15;
            else
            {
                interest *= 100;
                interest = (interest - (int)interest + interestBar.getProgress()) / 100.0;
            }

            interestEditText.setText(percent.format(interest));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar bar) {
    }
    @Override
    public void onStopTrackingTouch(SeekBar bar) {

        if(interestBar.getProgress() == 15)
            interest = .15;
        else
        {
            interest *= 100;
            interest = (interest - (int)interest + interestBar.getProgress()) / 100.0;
        }
        interestEditText.setText(percent.format(interest));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id){

        int state = parent.getSelectedItemPosition();

        if(state != currentSavedState)
        {
            // Save Info
            savedStates[currentSavedState] = new SavedState(
                    principalAmountStringEditText.getText().toString(),
                    downPaymentEditText.getText().toString(),
                    interestEditText.getText().toString(),
                    interestBar.getProgress(),
                    (int)numPayments,
                    monthlyPaymentAmount.getText(),
                    termAmount.getText(),
                    totalAmountAmount.getText());

            // load info
            if (savedStates[state] == null)
            {
                principalAmountStringEditText.setText("");
                downPaymentEditText.setText("");
                interestEditText.setText("");
                interestBar.setProgress(0);
                yearButton30.toggle();
                monthlyPaymentAmount.setText(currency.format(0.00));
                termAmount.setText(number.format(0.00));
                totalAmountAmount.setText(currency.format(0.00));
            }

            else
            {
                principalAmountStringEditText.setText(savedStates[state].principal);
                downPaymentEditText.setText(savedStates[state].downPayment);
                interestEditText.setText(savedStates[state].interest);
                interestBar.setProgress(savedStates[state].barProg);
                if(savedStates[state].barProg == 360)
                    yearButton15.toggle();
                else //(savedStates[state].barProg == 30 && yearButton15.isChecked())
                    yearButton30.toggle();
                monthlyPaymentAmount.setText(savedStates[state].monthlyPay);
                termAmount.setText(savedStates[state].term);
                totalAmountAmount.setText(savedStates[state].totalAmount);
            }

            currentSavedState = state;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent){

    }

    class SavedState{

        String principal, downPayment, interest;
        int barProg, radioYear;
        CharSequence monthlyPay, term, totalAmount;

        SavedState(String principal, String downPayment, String interest, int barProg,
                   int radioYear, CharSequence monthlyPay, CharSequence term,
                   CharSequence totalAmount){

            this.principal = principal;
            this.downPayment = downPayment;
            this.interest = interest;
            this.barProg = barProg;
            this.radioYear = radioYear;
            this.monthlyPay = monthlyPay;
            this.term = term;
            this.totalAmount = totalAmount;
        }
    }
}

