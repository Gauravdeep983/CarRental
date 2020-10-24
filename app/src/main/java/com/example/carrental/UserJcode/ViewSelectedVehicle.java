package com.example.carrental.UserJcode;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carrental.AdminJcode.UserDbOperations;
import com.example.carrental.NavigationHelper;
import com.example.carrental.R;
import com.example.carrental.SessionHelper;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class ViewSelectedVehicle extends AppCompatActivity {

    public NavigationHelper navigationHelper;
    public SessionHelper session;
    public UserDbOperations userDbOperations;
    TextView carNumber;
    TextView carName;
    TextView selectedCapacity;
    TextView startDateText;
    TextView endDateText;
    CheckBox gps;
    CheckBox onstar;
    CheckBox siriusxm;
    TextView totalCost;
    Button btnReserve;
    Button backbtn;
    TableRow membershipContainer;
    String sessionUsername = null;
    String userType = null;
    boolean isMember;
    double finalCost = 0.00;
    String reservationNumber = "";
    double baseCost = 0.0;
    int noOfDays = 0;
    TextView finalCostText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_details);
        session = new SessionHelper(this);
        userDbOperations = new UserDbOperations(this);
        navigationHelper = new NavigationHelper(this);
        backbtn = (Button) findViewById(R.id.backbtn);
        btnReserve = (Button) findViewById(R.id.btnReserve);
        carNumber = (TextView) findViewById(R.id.carNumber);
        carName = (TextView) findViewById(R.id.carName);
        selectedCapacity = (TextView) findViewById(R.id.selectedCapacity);
        startDateText = (TextView) findViewById(R.id.startDate);
        endDateText = (TextView) findViewById(R.id.endDate);
        gps = (CheckBox) findViewById(R.id.gps);
        onstar = (CheckBox) findViewById(R.id.onstar);
        siriusxm = (CheckBox) findViewById(R.id.siriusxm);
        totalCost = (TextView) findViewById(R.id.totalCost);
        finalCostText = (TextView) findViewById(R.id.finalCostText);
        membershipContainer = (TableRow) findViewById(R.id.membershipContainer);

        sessionUsername = session.getloggedInUsername();
        userType = session.getloggedInUserType();
        isMember = Boolean.parseBoolean(session.getMembershipStatus());
        final double gpsRate;
        final double onstarRate;
        final double siriusxmRate;

        // Get data sent from search vehicle screen
        ArrayList<String> carDetails, userInputs = null;
        Bundle args = getIntent().getBundleExtra("args");
        carDetails = (ArrayList<String>) args.getSerializable("car");
        userInputs = (ArrayList<String>) args.getSerializable("userInputs");

//        String carName = car.get(0);
//        String carNumber = car.get(1);
//        String maxCapacity = car.get(2);
//        String weekdayRate = car.get(3);
//        String weekendRate = car.get(4);
//        String weeklyRate = car.get(5);
//        String gpsRate = car.get(6);
//        String onstarRate = car.get(7);
//        String siriusxmRate = car.get(8);
        final String startDate = userInputs.get(1);
        final String endDate = userInputs.get(2);
        baseCost = getIntent().getDoubleExtra("baseCost", 0.0);
        noOfDays = getIntent().getIntExtra("noOfDays", 0);
        finalCostText.setText("$" + calculateFinalCost(baseCost, isMember));
        if (isMember) {
            // view membership container
            membershipContainer.setVisibility(View.VISIBLE);
        } else {
            // gone
            membershipContainer.setVisibility(View.GONE);
        }

        carName.setText(carDetails.get(0));
        carNumber.setText(carDetails.get(1));
        selectedCapacity.setText(userInputs.get(0));
        startDateText.setText(startDate);
        endDateText.setText(endDate);
        gpsRate = Double.parseDouble(carDetails.get(6));
        onstarRate = Double.parseDouble(carDetails.get(7));
        siriusxmRate = Double.parseDouble(carDetails.get(8));

        // Default cost + tax
        totalCost.setText("$" + baseCost);
        finalCost = calculateFinalCost(baseCost, isMember);
        gps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    baseCost += gpsRate;
                } else {
                    baseCost -= gpsRate;
                }
                totalCost.setText("$" + Double.toString(baseCost));
            }
        });

        onstar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    baseCost += onstarRate;
                } else {
                    baseCost -= onstarRate;
                }
                totalCost.setText("$" + Double.toString(baseCost));
            }
        });

        siriusxm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    baseCost += siriusxmRate;
                } else {
                    baseCost -= siriusxmRate;
                }
                totalCost.setText("$" + Double.toString(baseCost));
            }
        });

        btnReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Generate a 6 digit reservation number
                reservationNumber = generateReservationNumber(6);
                // Save in car_reservation table
                //TODO Optional features isSelected() not working
                userDbOperations.InsertReservationDetails(reservationNumber, sessionUsername, carName.getText().toString(),
                        startDate, endDate, Integer.parseInt(selectedCapacity.getText().toString()), Double.parseDouble(finalCostText.getText().toString().replace("$", "")),
                        gps.isSelected(), onstar.isSelected(), siriusxm.isSelected(), null);

                // Redirect/ toast
                //TODO Not persistent
                Snackbar.make(findViewById(android.R.id.content), "Reservation Number: " + reservationNumber, Snackbar.LENGTH_INDEFINITE)
                        .setAction("Action", null).show();
                navigationHelper.GotoHomeScreen(userType);
            }
        });

        totalCost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                finalCostText.setText("$" + calculateFinalCost(baseCost, isMember));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private double calculateFinalCost(double baseCost, boolean isMember) {
        double result = 0.0;
        // Add tax
        result = baseCost + (0.0825 * baseCost);
        if (isMember) {
            // Add discount
            result = result - (result * 0.1);
        }
        return result;
    }

    private String generateReservationNumber(int stringSize) {
        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789";
        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(stringSize);
        for (int i = 0; i < stringSize; i++) {
            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index = (int) (AlphaNumericString.length() * Math.random());
            // add Character one by one in end of sb
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }


}