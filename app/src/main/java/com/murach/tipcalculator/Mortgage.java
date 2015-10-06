package com.murach.tipcalculator;

import java.io.Serializable;

/**
 * Created by Amanuel on 9/30/2015.
 */
public class Mortgage implements Serializable {

    double loanAmount;
    double annualInterestRate;
    double downPayment;
    int totalYearsToRepay;
    int numberOfPayments;

    public Mortgage(){}

    public Mortgage(double loanAmt, float interest, int years, double downPmt){
        loanAmount = loanAmt - downPmt;
        annualInterestRate = interest;
        totalYearsToRepay = years;
        downPayment = downPmt;
    }

    public double monthlyPayment()
    {
        return (loanAmount*monthlyInterestRate()*term())/(term()-1);
    }
    public double term()
    {
        numberOfPayments = totalYearsToRepay * 12;
        return Math.pow((1 + monthlyInterestRate()), numberOfPayments);
    }
    public double monthlyInterestRate(){
        return annualInterestRate/12;
    }
    public double totalPayBack(){
        return monthlyPayment()*numberOfPayments;
    }

}
