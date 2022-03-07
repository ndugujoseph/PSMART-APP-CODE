package com.example.psmart.ui.login;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.psmart.R;
import com.example.psmart.ui.account.AccountFragment;

import java.util.List;


public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {


    private final AccountFragment mCtx;
   // private List<Account> AccountList;
    private final List<Account> accountList;

    public AccountAdapter(AccountFragment mCtx, List<Account> accountList) {
        this.mCtx = mCtx;
        this.accountList = accountList;
    }


    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx.getContext());
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.account_list, null);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AccountViewHolder holder, int position) {
        Account account = accountList.get(position);

        //loading the image
        Glide.with(mCtx)
                .load(account.getImage())
                .into(holder.imageView);

        holder.textViewTitle.setText(account.getName());
        holder.textViewShortDesc.setText(account.getEmail());
        holder.textViewRating.setText(account.getGender());
        holder.textViewPrice.setText(account.getContact());
    }

    @Override
    public int getItemCount() {
        return accountList.size();
    }

    static class AccountViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle, textViewShortDesc, textViewRating, textViewPrice;
        ImageView imageView;

        public AccountViewHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewShortDesc = itemView.findViewById(R.id.textViewShortDesc);
            textViewRating = itemView.findViewById(R.id.textViewRating);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            imageView = itemView.findViewById(R.id.imageViewAccount);
        }
    }
}