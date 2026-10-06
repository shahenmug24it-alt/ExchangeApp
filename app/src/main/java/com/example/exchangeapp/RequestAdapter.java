package com.example.exchangeapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    private final Context context;
    private final List<ExchangeRequest> requestList;
    private final String currentUserId;

    public RequestAdapter(Context context, List<ExchangeRequest> requestList, String currentUserId) {
        this.context = context;
        this.requestList = requestList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_request, parent, false);

        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {

        ExchangeRequest request = requestList.get(position);

        boolean isReceived = currentUserId.equals(request.getOwnerId());

        if (isReceived) {
            holder.txtRequestType.setText("RECEIVED REQUEST");
            holder.txtUserInfo.setText(
                    "From Requester ID: " + request.getRequesterId()
            );
        } else {
            holder.txtRequestType.setText("SENT REQUEST");
            holder.txtUserInfo.setText(
                    "To Owner ID: " + request.getOwnerId()
            );
        }

        String pName = request.getProductName() != null
                ? request.getProductName()
                : "Product ID: " + request.getProductId();

        holder.txtProductName.setText(pName);

        String status = request.getStatus() != null
                ? request.getStatus()
                : "Pending";

        holder.txtStatus.setText("Status: " + status);

        if ("Accepted".equalsIgnoreCase(status)) {

            holder.txtStatus.setTextColor(Color.parseColor("#2E7D32"));

            // Show exchange details
            holder.txtExchangeDetails.setVisibility(View.VISIBLE);

            loadExchangeDetails(holder, request);

        } else {

            holder.txtExchangeDetails.setVisibility(View.GONE);

            if ("Rejected".equalsIgnoreCase(status)) {
                holder.txtStatus.setTextColor(Color.RED);
            } else {
                holder.txtStatus.setTextColor(Color.parseColor("#E65100"));
            }
        }

        // Accept / Reject buttons only for received pending requests
        if (isReceived && "Pending".equalsIgnoreCase(status)) {

            holder.layoutActionButtons.setVisibility(View.VISIBLE);

            holder.btnAccept.setOnClickListener(v ->
                    updateRequestStatus(request, "Accepted")
            );

            holder.btnReject.setOnClickListener(v ->
                    updateRequestStatus(request, "Rejected")
            );

        } else {

            holder.layoutActionButtons.setVisibility(View.GONE);
        }
    }

    private void updateRequestStatus(ExchangeRequest request, String newStatus) {

        DatabaseReference requestRef =
                FirebaseDatabase
                        .getInstance(AddProductActivity.DB_URL)
                        .getReference("Requests")
                        .child(request.getRequestId());

        requestRef.child("status")
                .setValue(newStatus)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        Toast.makeText(
                                context,
                                "Request " + newStatus.toLowerCase(),
                                Toast.LENGTH_SHORT
                        ).show();

                    } else {

                        String err = task.getException() != null
                                ? task.getException().getMessage()
                                : "Failed to update status";

                        Toast.makeText(
                                context,
                                "Error: " + err,
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void loadExchangeDetails(
            RequestViewHolder holder,
            ExchangeRequest request
    ) {

        DatabaseReference usersRef =
                FirebaseDatabase
                        .getInstance(AddProductActivity.DB_URL)
                        .getReference("users");

        usersRef.child(request.getRequesterId())
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    @NonNull DataSnapshot requesterSnapshot
                            ) {

                                String requesterName =
                                        getValue(requesterSnapshot, "name");

                                String requesterPhone =
                                        getValue(requesterSnapshot, "phone");

                                String requesterAddress =
                                        getValue(requesterSnapshot, "address");

                                usersRef.child(request.getOwnerId())
                                        .addListenerForSingleValueEvent(
                                                new ValueEventListener() {

                                                    @Override
                                                    public void onDataChange(
                                                            @NonNull DataSnapshot ownerSnapshot
                                                    ) {

                                                        String ownerName =
                                                                getValue(ownerSnapshot, "name");

                                                        String ownerPhone =
                                                                getValue(ownerSnapshot, "phone");

                                                        String ownerAddress =
                                                                getValue(ownerSnapshot, "address");

                                                        String details =
                                                                "EXCHANGE DETAILS\n\n" +

                                                                        "Requester\n" +
                                                                        "Name: " + requesterName + "\n" +
                                                                        "Phone: " + requesterPhone + "\n" +
                                                                        "Address: " + requesterAddress + "\n\n" +

                                                                        "Product Owner\n" +
                                                                        "Name: " + ownerName + "\n" +
                                                                        "Phone: " + ownerPhone + "\n" +
                                                                        "Address: " + ownerAddress;

                                                        holder.txtExchangeDetails
                                                                .setText(details);
                                                    }

                                                    @Override
                                                    public void onCancelled(
                                                            @NonNull DatabaseError error
                                                    ) {

                                                        holder.txtExchangeDetails
                                                                .setText(
                                                                        "Unable to load owner details."
                                                                );
                                                    }
                                                }
                                        );
                            }

                            @Override
                            public void onCancelled(
                                    @NonNull DatabaseError error
                            ) {

                                holder.txtExchangeDetails
                                        .setText(
                                                "Unable to load requester details."
                                        );
                            }
                        }
                );
    }

    private String getValue(
            DataSnapshot snapshot,
            String key
    ) {

        String value = snapshot.child(key).getValue(String.class);

        return value != null && !value.isEmpty()
                ? value
                : "Not available";
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class RequestViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtRequestType;
        TextView txtProductName;
        TextView txtUserInfo;
        TextView txtStatus;
        TextView txtExchangeDetails;

        LinearLayout layoutActionButtons;

        Button btnAccept;
        Button btnReject;

        public RequestViewHolder(@NonNull View itemView) {

            super(itemView);

            txtRequestType =
                    itemView.findViewById(R.id.txtRequestType);

            txtProductName =
                    itemView.findViewById(R.id.txtProductName);

            txtUserInfo =
                    itemView.findViewById(R.id.txtUserInfo);

            txtStatus =
                    itemView.findViewById(R.id.txtStatus);

            txtExchangeDetails =
                    itemView.findViewById(R.id.txtExchangeDetails);

            layoutActionButtons =
                    itemView.findViewById(R.id.layoutActionButtons);

            btnAccept =
                    itemView.findViewById(R.id.btnAccept);

            btnReject =
                    itemView.findViewById(R.id.btnReject);
        }
    }
}