package com.example.stockquote;

import java.util.Arrays;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	public final static String STOCK_SYMBOL = "com.example.stockquote.STOCK";
	private SharedPreferences stockSymbolsEntered;
	private TableLayout stockTableScrollView;
	private EditText stockSymbolEditText;
	
	Button enterStockSymbolButton;
	Button deleteStocksButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		stockSymbolsEntered = getSharedPreferences("stockList", MODE_PRIVATE);
		stockTableScrollView = (TableLayout) findViewById(R.id.stockScrollView);
		stockSymbolEditText = (EditText) findViewById(R.id.stockSymbolEditText);
		
		enterStockSymbolButton = (Button)findViewById(R.id.enterStockSymbolButton);
		deleteStocksButton = (Button)findViewById(R.id.deleteStocksButton);
		
		enterStockSymbolButton.setOnClickListener(enterStockButtonListener);
		deleteStocksButton.setOnClickListener(deleteStocksButtonListenerl);
		updateSavedStockList(null);

	}
	
	public OnClickListener enterStockButtonListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			
			if(stockSymbolEditText.getText().length()>0) {
				saveStockSymbol(stockSymbolEditText.getText().toString());
				stockSymbolEditText.setText("");
				
				// close keyboard
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(stockSymbolEditText.getWindowToken(), 0);
			} else {
				// alert dialog box
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setTitle(R.string.invalid_stock_symbol);
				builder.setPositiveButton(R.string.ok, null);
				builder.setMessage(R.string.missing_stock_symbol);
				AlertDialog theAlertDialog = builder.create();
				theAlertDialog.show();
			}
		}
	};
	
	public OnClickListener deleteStocksButtonListenerl = new OnClickListener() {

		@Override
		public void onClick(View v) {
			deleteAllStocks();
			SharedPreferences.Editor preferencesEditor = stockSymbolsEntered.edit();
			preferencesEditor.clear();
			preferencesEditor.commit();
		}
		
	};
	
	private void deleteAllStocks() {
		stockTableScrollView.removeAllViews();
	}

	private void updateSavedStockList(String newStockSymbol) {
		
		String[] stocks = stockSymbolsEntered.getAll().keySet().toArray(new String[0]);
		Arrays.sort(stocks, String.CASE_INSENSITIVE_ORDER);
		
		if(newStockSymbol != null) {
			insertStockInScrollView(newStockSymbol, Arrays.binarySearch(stocks, newStockSymbol));
		} else {
			for(int i = 0; i < stocks.length; i++)
				insertStockInScrollView(stocks[i], i);
		}
	}
	
	private void insertStockInScrollView(String newStockSymbol, int binarySearch) {
		LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//set or create stock row in second frame
		View newStockRow =  inflator.inflate(R.layout.stock_quote_row, null);
		
		TextView newStockTextView = (TextView) newStockRow.findViewById(R.id.stockSymbolTextViewRow);
		newStockTextView.setText(newStockSymbol);
		
		Button stockQuoteButton = (Button) newStockRow.findViewById(R.id.stockQuoteButton);
		stockQuoteButton.setOnClickListener(getStockActivityListener);
		
		Button quoteFromWebButton = (Button) newStockRow.findViewById(R.id.quoteFromWebButton);
		quoteFromWebButton.setOnClickListener(getStockFromWebSiteListener);
		
		stockTableScrollView.addView(newStockRow);
	}
	
	public OnClickListener getStockFromWebSiteListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			TableRow tableRow = (TableRow) v.getParent();
			TextView stockTextView = (TextView) tableRow.findViewById(R.id.stockSymbolTextViewRow);
			String stockSymbol = stockTextView.getText().toString();
			
			String stockURL = getString(R.string.yahoo_stock_url) + stockSymbol; 
			Intent getStockWebPage = new Intent(Intent.ACTION_VIEW, Uri.parse(stockURL));
			
			startActivity(getStockWebPage);
			
			
		}

	};
	
	public OnClickListener getStockActivityListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			TableRow tableRow = (TableRow) v.getParent();
			TextView stockTextView = (TextView) tableRow.findViewById(R.id.stockSymbolTextViewRow);
			String stockSymbol = stockTextView.getText().toString();
			Intent intent = new Intent(MainActivity.this, StockInfoActivity.class);
			intent.putExtra(STOCK_SYMBOL, stockSymbol);
			startActivity(intent);
		}
		
	};


	private void saveStockSymbol(String newStock) {
		SharedPreferences.Editor preferencesEditor = stockSymbolsEntered.edit();
		preferencesEditor.putString(newStock, newStock);
		preferencesEditor.commit();
		
		String isTheStockNew = stockSymbolsEntered.getString(newStock,  null);
		
		if(isTheStockNew==null) {
			updateSavedStockList(newStock);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
