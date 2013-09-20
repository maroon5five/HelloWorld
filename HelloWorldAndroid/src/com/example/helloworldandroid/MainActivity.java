package com.example.helloworldandroid;

import android.os.Bundle;
import android.os.SystemClock;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private static final String FINAL_BILL = "TOTAL_BILL";
	private static final String CURRENT_TIP = "CURRENT_TIP";
	private static final String BILL_WITHOUT_TIP = "BILL_WITHOUT_TIP";
	
	private double billBeforeTip, tipAmount, finalBill;
	
	private EditText billBeforeTipET, tipAmountET, finalBillET;
	
	private SeekBar tipSeekBar;
	
	private int[] checkListValues = new int[12];
	
	private CheckBox friendlyCheckBox, specialsCheckBox, opinionCheckBox;
	
	private RadioGroup availableRadioGroup;
	private RadioButton badRadio, OKRadio, goodRadio;
	
	private Spinner problemsSpinner;
	
	private Button startButton, pauseButton, resetButton;
	
	private Chronometer timeWaitingChronometer;
	
	private long secondsWaited = 0;
	private TextView timeWaitingTextView;
	
	private TextWatcher billBeforeTipListener = new TextWatcher(){

		@Override
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence chars, int arg1, int arg2,
				int arg3) {
			try{
				billBeforeTip = Double.parseDouble(chars.toString());
				
			}catch(NumberFormatException e){
				billBeforeTip = 0.0;
			}
			
			updateTipAndFinalBill();
		}
		
	};
	
	private OnSeekBarChangeListener tipSeekBarListener = new OnSeekBarChangeListener(){

		@Override
		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
			tipAmount = (tipSeekBar.getProgress())*.01;
			
			tipAmountET.setText(String.format("%.02f", tipAmount));
			
			updateTipAndFinalBill();
		}

		@Override
		public void onStartTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub
			
		}
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		
		
		if(savedInstanceState == null){
			billBeforeTip = 0.0;
			tipAmount = .15;
			finalBill = 0.0;
		} else {
			billBeforeTip = savedInstanceState.getDouble(BILL_WITHOUT_TIP);
			tipAmount = savedInstanceState.getDouble(CURRENT_TIP);
			finalBill = savedInstanceState.getDouble(FINAL_BILL);
		}
		billBeforeTipET = (EditText) findViewById(R.id.billEditText);
		tipAmountET = (EditText) findViewById(R.id.tipEditText);
		finalBillET = (EditText) findViewById(R.id.finalBillEditText);
		
		tipSeekBar = (SeekBar) findViewById(R.id.tipSeekBar);
		
		tipSeekBar.setOnSeekBarChangeListener(tipSeekBarListener);
		
		billBeforeTipET.addTextChangedListener(billBeforeTipListener);
		
		friendlyCheckBox = (CheckBox) findViewById(R.id.friendlyCheckBox);
		specialsCheckBox = (CheckBox) findViewById(R.id.specialsCheckBox);
		opinionCheckBox = (CheckBox) findViewById(R.id.opinionCheckBox);
		
		setUpIntroCheckBoxes();
		
		availableRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		badRadio = (RadioButton) findViewById(R.id.badRadioButton);
		OKRadio = (RadioButton) findViewById(R.id.OKRadioButton);
		goodRadio = (RadioButton) findViewById(R.id.goodRadioButton);
		
		addChangeListenerToRadios();
		
		problemsSpinner = (Spinner) findViewById(R.id.problemsSpinner);
		
		addItemSelectedListenerToSpinner();
		
		startButton = (Button) findViewById(R.id.startButton);
		pauseButton = (Button) findViewById(R.id.pauseButton);
		resetButton = (Button) findViewById(R.id.resetButton);
		
		setButtonOnClickListeners();
		
		timeWaitingChronometer = (Chronometer) findViewById(R.id.timeWaitingChronometer);
		
		timeWaitingTextView = (TextView) findViewById(R.id.timeWaitingTextView);
		
	}
	
	private void setButtonOnClickListeners() {
		startButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				long stoppedMilliseconds = 0;
				
				String chronometerText = timeWaitingChronometer.getText().toString();
				String array[] = chronometerText.split(":");
				
				if(array.length == 2){
					stoppedMilliseconds = Integer.parseInt(array[0]) *60*1000 + Integer.parseInt(array[1])*1000;
					
				} else if (array.length == 3){
					stoppedMilliseconds = Integer.parseInt(array[0]) *60*60*1000 + Integer.parseInt(array[1])*60*1000 + Integer.parseInt(array[2])*1000;
					
				}
				timeWaitingChronometer.setBase(SystemClock.elapsedRealtime() - stoppedMilliseconds);
				
				secondsWaited = Long.parseLong(array[1]);
				
				updateTipBasedOnTimeWaited(secondsWaited);
				
				timeWaitingChronometer.start();
			}

			
			
		});
		
		pauseButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				timeWaitingChronometer.stop();
				
			}	
			
		});
		
		resetButton.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				timeWaitingChronometer.setBase(SystemClock.elapsedRealtime());
				
				secondsWaited = 0;
				
			}	
			
		});
		
	}

	private void updateTipBasedOnTimeWaited(long secondsWaited) {
		checkListValues[9] = (secondsWaited > 300)?-2:2;
		
		setTipFromWaitressCheckleist();
		
		updateTipAndFinalBill();
		
	}
	
	private void addItemSelectedListenerToSpinner() {
		problemsSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				checkListValues[6] = (problemsSpinner.getSelectedItem().equals("Bad"))?-1:0;
				checkListValues[7] = (problemsSpinner.getSelectedItem().equals("OK"))?3:0;
				checkListValues[8] = (problemsSpinner.getSelectedItem().equals("Good"))?6:0;
				
				setTipFromWaitressCheckleist();
				
				updateTipAndFinalBill();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
			
		});
		
	}

	private void addChangeListenerToRadios() {
		availableRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				checkListValues[3] = (badRadio.isChecked())?-1:0;
				checkListValues[4] = (OKRadio.isChecked())?2:0;
				checkListValues[5] = (goodRadio.isChecked())?4:0;
				
				setTipFromWaitressCheckleist();
				
				updateTipAndFinalBill();
			}
			
		});
		
	}

	private void setUpIntroCheckBoxes() {
		friendlyCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				checkListValues[0] = (friendlyCheckBox.isChecked())?4:0;
				
				setTipFromWaitressCheckleist();
				
				updateTipAndFinalBill();
			}

		});
		specialsCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				checkListValues[1] = (specialsCheckBox.isChecked())?1:0;
				
				setTipFromWaitressCheckleist();
				
				updateTipAndFinalBill();
			}

		});
		opinionCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				checkListValues[2] = (opinionCheckBox.isChecked())?2:0;
				
				setTipFromWaitressCheckleist();
				
				updateTipAndFinalBill();
			}

		});
		
	}
	
	private void setTipFromWaitressCheckleist() {
		int checkListTotal = 0;
		
		for(int item : checkListValues){
			checkListTotal += item;
		}
		
		tipAmountET.setText(String.format("%.02f", checkListTotal * .01));
		
	}

	private void updateTipAndFinalBill(){
		tipAmount =  Double.parseDouble(tipAmountET.getText().toString());
		
		finalBill = billBeforeTip + (billBeforeTip * tipAmount);
		
		finalBillET.setText(String.format("%.02f", finalBill));
	}
	
	protected void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		
		outState.putDouble(FINAL_BILL, finalBill);
		outState.putDouble(CURRENT_TIP, tipAmount);
		outState.putDouble(BILL_WITHOUT_TIP, billBeforeTip);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
