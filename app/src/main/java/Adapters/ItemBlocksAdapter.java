package Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_accounting.AddDocumentActivity;
import com.example.my_accounting.R;

import java.util.List;

import Model.ItemsDocuments;

public class ItemBlocksAdapter extends RecyclerView.Adapter<ItemBlocksAdapter.ViewHolder> {

    private List<ItemsDocuments> itemsList;
    private Context context;
    private OnBarcodeScanListener scanListener;

    public interface OnBarcodeScanListener {
        void onBarcodeScanRequested(EditText editText, TextView textView);
    }

    public ItemBlocksAdapter(Context context, List<ItemsDocuments> itemsList, OnBarcodeScanListener scanListener) {
        this.itemsList = itemsList;
        this.context = context;
        this.scanListener = scanListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_block, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemsDocuments item = itemsList.get(position);
        holder.etBarcode.setText(item.getBarcodeGood());
        holder.tvName.setText(item.getTitleGood());

        if (item.getQuantity() != 0)
            holder.etQuantity.setText(String.valueOf(item.getQuantity()));
        holder.tvNumber.setText(String.valueOf(position + 1));

        holder.ibDelete.setOnClickListener(v -> {
            itemsList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, itemsList.size());
        });

        holder.etBarcode.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Drawable drawableEnd = holder.etBarcode.getCompoundDrawablesRelative()[2];
                if (drawableEnd != null && event.getRawX() >= (holder.etBarcode.getRight() - drawableEnd.getBounds().width())) {
                    scanListener.onBarcodeScanRequested(holder.etBarcode, holder.tvName);
                    return true;
                }
            }
            return false;
        });

        // Создаем новый TextWatcher
        TextWatcher barcodeTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                item.setBarcodeGood(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                String barcode = s.toString();
                if (!barcode.isEmpty()) {
                    ((AddDocumentActivity) context).getGood(barcode, good -> {
                        if (good != null) {
                            item.setTitleGood(good.getTitle());
                            item.setIdGood(good.getId());
                            holder.tvName.setText(good.getTitle());
                            holder.etBarcode.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                        } else {
                            item.setTitleGood("");
                            holder.tvName.setText("");
                            holder.etBarcode.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
                        }
                    }, context);
                } else {
                    item.setTitleGood("");
                    holder.tvName.setText("");
                    holder.etBarcode.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
                }
            }
        };

        holder.etBarcode.addTextChangedListener(barcodeTextWatcher);
        holder.etBarcode.setTag(barcodeTextWatcher);

        holder.etBarcode.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                item.setBarcodeGood(s.toString());
            }
        });

        holder.etQuantity.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    item.setQuantity(Integer.parseInt(s.toString()));
                } catch (NumberFormatException e) {
                    item.setQuantity(0);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        EditText etBarcode, etQuantity;
        TextView tvNumber, tvName;
        ImageButton ibDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            etBarcode = itemView.findViewById(R.id.et_barcode);
            etQuantity = itemView.findViewById(R.id.et_count);
            tvNumber = itemView.findViewById(R.id.tv_number);
            tvName = itemView.findViewById(R.id.tv_title);
            ibDelete = itemView.findViewById(R.id.ib_delete);
        }
    }

    public abstract class SimpleTextWatcher implements android.text.TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(android.text.Editable s) {}
    }
}
