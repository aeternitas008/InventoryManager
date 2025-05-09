package Adapters;

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

public class DocsAdapter extends RecyclerView.Adapter<DocsAdapter.ViewHolder> {

    private List<Documents> docsList;
    private final OnDocumentClickListener listener;

    public DocsAdapter(OnDocumentClickListener listener, List<Documents> docsList) {
        this.docsList = docsList;
        this.listener = listener;
    }

    public void setDocumentsList(List<Documents> docsList) {
        this.docsList = docsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_docs, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Documents doc = docsList.get(position);
        holder.bind(doc, listener);
        String title = "Документ №" + doc.getId();
        holder.tvDocTitle.setText(title);
        holder.tvDocDate.setText(String.valueOf(doc.getCreated_at()));
        holder.tvDocType.setText((doc.getTypeString()));
    }

    @Override
    public int getItemCount() {
        return docsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDocTitle;
        TextView tvDocDate;
        TextView tvDocType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDocTitle = itemView.findViewById(R.id.tv_doc_title);
            tvDocDate = itemView.findViewById(R.id.tv_doc_date);
            tvDocType = itemView.findViewById(R.id.tv_doc_type);
        }

        public void bind(final Documents document, final OnDocumentClickListener listener) {
            itemView.setOnClickListener(v -> listener.onDocumentClick(document));
        }

    }

    public interface OnDocumentClickListener {
        void onDocumentClick(Documents document);
    }

}