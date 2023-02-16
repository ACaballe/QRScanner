package caballe.arnau.qrscanner;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<String> elements;

    public MyRecyclerViewAdapter(List<String> elements) {
        this.elements = elements;
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View viewElement = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder, parent, false);

        return new ViewHolder(viewElement);
    }


    @Override
    public void onBindViewHolder( ViewHolder holder, int position) {
        holder.getTxtElement().setText(elements.get(position));
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtElement;

        public ViewHolder(View itemView) {
            super(itemView);
            //Quan fem click a la llista mostrem l'element
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewHolder.this.mostraElement(v);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    copiarElement(txtElement);
                    return false;
                }
            });

            txtElement = itemView.findViewById(R.id.textElement);
        }

        private void mostraElement(View v) {
            try{
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(elements.get(getAdapterPosition())));
                v.getContext().startActivity(browserIntent);
            }catch (Exception e){
                //Toast.makeText(this, getString(R.string.errorOnAccess), Toast.LENGTH_SHORT).show();
            }
        }

        private void copiarElement(TextView textView){
            /*
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("text", textView.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, getString(R.string.textCopiedToClipboard), Toast.LENGTH_SHORT).show();
            */
        }

        public TextView getTxtElement() {
            return txtElement;
        }
    }

}
