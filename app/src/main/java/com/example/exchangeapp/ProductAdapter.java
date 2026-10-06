package com.example.exchangeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.txtProductName.setText(product.getName());
        holder.txtProductDescription.setText(product.getDescription());
        holder.txtProductPrice.setText("Price: " + product.getPrice());

        holder.btnRequest.setOnClickListener(v -> sendExchangeRequest(product));
    }

    private void sendExchangeRequest(Product product) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(context, "Please log in to send exchange requests", Toast.LENGTH_SHORT).show();
            return;
        }

        String requesterId = currentUser.getUid();
        DatabaseReference requestsRef = FirebaseDatabase.getInstance(AddProductActivity.DB_URL).getReference("Requests");
        String requestId = requestsRef.push().getKey();

        if (requestId != null) {
            ExchangeRequest request = new ExchangeRequest(
                    requestId,
                    requesterId,
                    product.getUserId(),
                    product.getProductId(),
                    product.getName(),
                    "Pending"
            );

            requestsRef.child(requestId).setValue(request)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Exchange request sent successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            String errorMsg = task.getException() != null ? task.getException().getMessage() : "Failed to send request";
                            Toast.makeText(context, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView txtProductName, txtProductDescription, txtProductPrice;
        Button btnRequest;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtProductDescription = itemView.findViewById(R.id.txtProductDescription);
            txtProductPrice = itemView.findViewById(R.id.txtProductPrice);
            btnRequest = itemView.findViewById(R.id.btnRequest);
        }
    }
}
