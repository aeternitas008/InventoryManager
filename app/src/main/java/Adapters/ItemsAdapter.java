package Adapters;

import static Utils.Helper.println;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_accounting.R;

import java.util.List;

import Model.ItemsDocuments;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    private List<ItemsDocuments> itemsList;
    private Context context;


    public ItemsAdapter(Context context, List<ItemsDocuments> itemsList) {
        this.itemsList = itemsList;
        this.context = context;
    }

    public void setItemsList(List<ItemsDocuments> itemsList) {
        this.itemsList = itemsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_good, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemsDocuments itemsDocument = itemsList.get(position);
        holder.tvGoodTitle.setText(itemsDocument.getTitleGood());
        holder.tvGoodBarcode.setText(String.valueOf(itemsDocument.getBarcodeGood()));
        holder.tvGoodQuantity.setText(String.valueOf(itemsDocument.getQuantity()));
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvGoodTitle;
        TextView tvGoodBarcode;
        TextView tvGoodQuantity;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGoodTitle = itemView.findViewById(R.id.tv_good_title);
            tvGoodBarcode = itemView.findViewById(R.id.tv_good_barcode);
            tvGoodQuantity = itemView.findViewById(R.id.tv_quantity);
        }
    }
}