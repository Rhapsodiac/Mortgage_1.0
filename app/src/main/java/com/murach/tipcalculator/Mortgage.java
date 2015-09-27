package com.murach.tipcalculator;

import java.text.NumberFormat;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Mortgage extends Activity
implements OnEditorActionListener, OnClickListener, OnCheckedChangeListener {

    // define variables for the widgets
    private EditText billAmountEditText;
    private EditText percentTextView;
    private Button   percentUpButton;
    private Button   percentDownButton;
    private TextView tipTextView;
    private TextView totalTextView;
    private RadioButton yearButton15;
    private RadioButton yearButton30;
    private RadioGroup yearGroup;
    private double numPayments;
    
    // define the SharedPreferences object
    private SharedPreferences savedValues;
    
    // define instance variables that should be saved
    private String billAmountString = "", tipPercentString = "";
    private float tipPercent = .0475f;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_calculator);
        
        // get references to the widgets
        billAmountEditText = (EditText) findViewById(R.id.billAmountEditText);
        percentTextView = (EditText) findViewById(R.id.percentTextView);
        percentUpButton = (Button) findViewById(R.id.percentUpButton);
        percentDownButton = (Button) findViewById(R.id.percentDownButton);
        tipTextView = (TextView) findViewById(R.id.tipTextView);
        totalTextView = (TextView) findViewById(R.id.totalTextView);
        yearButton15 = (RadioButton) findViewById(R.id.rButton15Year);
        yearButton30 = (RadioButton) findViewById(R.id.rButton30Year);
        yearGroup = (RadioGroup) findViewById(R.id.yearGroup);

        numPayments = 360;

        // set the listeners
        billAmountEditText.setOnEditorActionListener(this);
        percentTextView.setOnEditorActionListener(this);
        percentUpButton.setOnClickListener(this);
        percentDownButton.setOnClickListener(this);
        yearGroup.setOnCheckedChangeListener(this);
        
        // get SharedPreferences object
        savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);
    }
    
    @Override
    public void onPause() {
        // save the instance variables       
        Editor editor = savedValues.edit();        
        editor.putString("billAmountString", billAmountString);
        editor.putString("tipPercentString", tipPercentString);
        editor.putFloat("tipPercent", tipPercent);
        editor.commit();        

        super.onPause();      
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
        // get the instance variables
        billAmountString = savedValues.getString("billAmountString", "");
        tipPercentString = savedValues.getString("tipPercentString", "");
        tipPercent = savedValues.getFloat("tipPercent", 0.0475f);

        // set the bill amount on its widget
        billAmountEditText.setText(billAmountString);
        
        // calculate and display
        calculateAndDisplay();
    }    
    
    public void calculateAndDisplay() {

        float billAmount;
        double term;

        // get the bill amount
        billAmountString = billAmountEditText.getText().toString();

        if (billAmountString.equals("")) {
            billAmount = 0;
        }
        else {
            billAmount = Float.parseFloat(billAmountString);
        }

        term = Math.pow((1 + tipPercent/12), numPayments);
        // calculate tip and total
        double totalAmount = (billAmount * (tipPercent/12) * term)/(term-1);
        
        // display the other results with formatting
        NumberFormat currency = NumberFormat.getCurrencyInstance();
        tipTextView.setText(currency.format(totalAmount));
        totalTextView.setText(currency.format(totalAmount*numPayments));
        
        NumberFormat percent = NumberFormat.getPercentInstance();
        percent.setMaximumFractionDigits(2);
        percent.setMinimumFractionDigits(2);
        percentTextView.setText(percent.format(tipPercent));
    }
    
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE ||
            actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {

            tipPercentString = percentTextView.getText().toString();
            if (tipPercentString.equals("")) {
                tipPercent = .0475f;
            }
            else {
                tipPercent = Float.parseFloat(tipPercentString.replace('%', '0')) / 100;
                Log.i("pre-check", "value: " + tipPercent);
                if(tipPercent <= .00f)
                    tipPercent = .0025f;
                else if(tipPercent >= 1f) {
                    tipPercent = 1f;
                }
                Log.i("post-check", "value: " + tipPercent);
            }
            calculateAndDisplay();
        }        
        return false;
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.percentDownButton:
                tipPercent -= .0025;
                if(tipPercent <= .00f)
                    tipPercent = .0025f;
                break;
            case R.id.percentUpButton:
                tipPercent += .0025;
                if(tipPercent >= 1f)
                    tipPercent = 1;
                break;
        }
        calculateAndDisplay();
        Log.i("onClick", "value: " + tipPercent);
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
        calculateAndDisplay();

        Log.i("onClick", "value: " + tipPercent);
    }
}