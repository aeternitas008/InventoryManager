package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_accounting.R;

import java.util.List;

import Model.Goods;

public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.ViewHolder> {

    public interface OnDeleteClickListener {
        void onDeleteClick(Goods good, int position);
    }

    private List<Goods> goodsList;

    private final OnDeleteClickListener onDeleteClickListener;

    public GoodsAdapter(OnDeleteClickListener onDeleteClickListener, List<Goods> goodsList) {
        this.goodsList = goodsList;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    public void setGoodsList(List<Goods> goodsList) {
        this.goodsList = goodsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_good, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Goods good = goodsList.get(position);
        holder.tvGoodTitle.setText(good.getTitle());
        holder.tvGoodBarcode.setText(String.valueOf(good.getBarcode()));


        holder.itemView.setOnLongClickListener(v -> {
            holder.buttonDelete.setVisibility(View.VISIBLE);
            return true;
        });

        holder.buttonDelete.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                onDeleteClickListener.onDeleteClick(good, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return goodsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvGoodTitle;
        TextView tvGoodBarcode;
        ImageButton buttonDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGoodTitle = itemView.findViewById(R.id.tv_good_title);
            tvGoodBarcode = itemView.findViewById(R.id.tv_good_barcode);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}