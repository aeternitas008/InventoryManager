package Adapters;

import static Utils.Helper.println;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_accounting.R;

import java.util.List;

import Model.Documents;
import Model.Warehouses;

public class WarehouseAdapter extends RecyclerView.Adapter<WarehouseAdapter.ViewHolder> {

    private List<Warehouses> warehousesList;
    private final OnWarehouseClickListener listener;


    public WarehouseAdapter(OnWarehouseClickListener listener, List<Warehouses> warehousesList) {
        this.warehousesList = warehousesList;
        this.listener = listener;
    }

    public void setWarehousesList(List<Warehouses> warehousesList) {
        this.warehousesList = warehousesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_warehouse, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Warehouses warehouse = warehousesList.get(position);
        holder.bind(warehouse, listener);
        holder.textView.setText(warehouse.getTitle());
    }

    @Override
    public int getItemCount() {
        return warehousesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_item_title); //
        }

        public void bind(final Warehouses warehouse, final WarehouseAdapter.OnWarehouseClickListener listener) {
            itemView.setOnClickListener(v -> listener.onWarehouseClick(warehouse));
        }
    }


    public interface OnWarehouseClickListener {
        void onWarehouseClick(Warehouses warehouse);
    }
}