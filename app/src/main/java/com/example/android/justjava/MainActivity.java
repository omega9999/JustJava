package com.example.android.justjava;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * This app displays an order form to order coffee.
 */
public class MainActivity extends AppCompatActivity {

    private int quantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        displayQuantity(quantity);
    }

    /**
     * This method is called when the order button is clicked.
     */
    public void submitOrder(View view) {
        //displayPrice(createOrderSummary(5));
        CheckBox checkBoxWhippedCream = findViewById(R.id.checkBoxWhippedCream);
        CheckBox checkBoxChocolate = findViewById(R.id.checkBoxChocolate);
        EditText editName = findViewById(R.id.name_edit_text);
        String message = createOrderSummary(editName.getText(), 5, checkBoxWhippedCream.isChecked(), checkBoxChocolate.isChecked());

        String subject = getString(R.string.email_subject,editName.getText());

        boolean res = composeEmail(new String[]{"prova@destinatario.com"}, subject, message);
        if (!res) {
            res = openMap(47.6, -122.3);
        }
        if (!res) {
            displayMessage(message);
        }
    }

    @CheckResult
    private boolean composeEmail(String[] addresses, String subject, CharSequence message) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        //intent.setType("text/plain"); //TODO setType da' problemi per spedire le mail
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
            return true;
        } else {
            Log.e("ERRORE", "App per email not found: \n" + intent + " | " + inspectIntent(intent));

            intent = EmailIntentBuilder.from(this)/*.to(Arrays.asList(addresses)).subject(subject).body(message + "")*/.build();
            if (intent.resolveActivity(getPackageManager()) != null) {
                Log.e("ERRORE", "Intent funzionante: \n" + intent + " | " + inspectIntent(intent));
            }

            return false;


        }
    }

    private String inspectIntent(Intent intent){
        StringBuilder builder = new StringBuilder();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                builder.append(" Extra " + key + " -> " + bundle.get(key));
            }
        }
        else{
            builder.append(intent.getData());
        }
        return builder.toString();
    }


    @CheckResult
    private boolean openMap(double x, double y) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(String.format("geo:%1$s, %2$s", x, y)));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
            return true;
        } else {
            Log.e("ERRORE", "App per maps not found");
            return false;
        }
    }

    @SuppressLint("StringFormatMatches")
    private String createOrderSummary(final Editable text, final int price, final boolean addCream, boolean addChocolate) {
        return getString(R.string.email_body, quantity, calculatePrice(price, addCream, addChocolate), addCream, addChocolate, text);
    }


    private void displayMessage(@NonNull final String message) {
        TextView orderSummaryTextView = findViewById(R.id.order_summary_text_view);
        orderSummaryTextView.setText(message);
    }

    @CheckResult
    private int calculatePrice(final int price, final boolean addCream, final boolean addChocolate) {
        int basePrice = price;
        if (addCream) {
            basePrice += 1;
        }
        if (addChocolate) {
            basePrice += 2;
        }
        return quantity * basePrice;
    }


    /**
     * This method displays the given quantity value on the screen.
     */
    private void displayQuantity(int number) {
        TextView quantityTextView = findViewById(R.id.quantity_text_view);
        quantityTextView.setText(String.format("%1$s", number));
    }

    /*
    private void displayPrice(int number) {
        TextView priceTextView = findViewById(R.id.order_summary_text_view);
        priceTextView.setText(NumberFormat.getCurrencyInstance(Locale.US).format(number));
    }
    */

    public void increment(View view) {
        if (quantity < 99) {
            quantity = quantity + 1;
            displayQuantity(quantity);
        } else {
            Toast.makeText(this, "Too many coffees", Toast.LENGTH_SHORT).show();
        }
    }

    public void decrement(View view) {
        if (quantity > 1) {
            quantity = quantity - 1;
            displayQuantity(quantity);
        } else {
            Toast.makeText(this, "Coffees must be one or more", Toast.LENGTH_SHORT).show();
        }
    }
}