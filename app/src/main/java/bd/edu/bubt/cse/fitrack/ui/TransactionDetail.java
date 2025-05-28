package bd.edu.bubt.cse.fitrack.ui;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import bd.edu.bubt.cse.fitrack.R;
import bd.edu.bubt.cse.fitrack.domain.model.Transaction;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

public class TransactionDetail extends AppCompatActivity {

    public static final String EXTRA_TRANSACTION = "extra_transaction";

    private TextView tvTitle, tvAmount, tvDate, tvCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Transaction Details");
        }

        tvTitle = findViewById(R.id.tv_transaction_title);
        tvAmount = findViewById(R.id.tv_transaction_amount);
        tvDate = findViewById(R.id.tv_transaction_date);
        tvCategory = findViewById(R.id.tv_transaction_category);

        // Get Transaction object from intent
        Transaction transaction = (Transaction) getIntent().getSerializableExtra(EXTRA_TRANSACTION);

        if (transaction != null) {
            tvTitle.setText(transaction.getDescription() != null ? transaction.getDescription() : "Transaction Detail");
            tvAmount.setText(String.format("$%.2f", transaction.getAmount()));
            tvDate.setText("Date: " + transaction.getDate());
            tvCategory.setText("Category: " + (transaction.getCategoryName() != null ? transaction.getCategoryName() : "N/A"));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
