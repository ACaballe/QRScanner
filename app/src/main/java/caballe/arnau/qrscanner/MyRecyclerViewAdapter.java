package caballe.arnau.qrscanner;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<String> elements;
    private Context context;

    public MyRecyclerViewAdapter(Context context, List<String> elements) {
        this.context = context;
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
                    alertDialog();
                    return false;
                }
            });

            txtElement = itemView.findViewById(R.id.textElement);
        }

        public TextView getTxtElement() {
            return txtElement;
        }

        private void mostraElement(View v) {
            try{
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(elements.get(getAdapterPosition())));
                v.getContext().startActivity(browserIntent);
            }catch (Exception e){
                Toast.makeText(context, context.getString(R.string.errorOnAccess), Toast.LENGTH_SHORT).show();
            }
        }
        /* COPY TEXTELEMENT */
        private void copiarElement(TextView textView){
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("text", textView.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, context.getString(R.string.textCopiedToClipboard), Toast.LENGTH_SHORT).show();
        }

        /* DELETE TEXTELEMENT */
        private void borrarElement(String opcio){
            try {
                File file = new File(context.getFilesDir(), "history.txt");
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line = "";
                String newContent = "";
                while ((line = reader.readLine()) != null) {
                    if (!line.equals(opcio)) {
                        newContent += line + "\n";
                    }
                }
                reader.close();
                FileWriter writer = new FileWriter(file);
                writer.write(newContent);
                writer.flush();
                writer.close();
                elements.remove(opcio);
                notifyDataSetChanged();
                Toast.makeText(context, context.getString(R.string.elementDeleted), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void alertDialog(){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            //builder.setTitle("Set Latitude and Longitude");

            //Copy
            builder.setNeutralButton(context.getString(R.string.copy), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    copiarElement(txtElement);
                }
            });

            //Delete
            builder.setPositiveButton(context.getString(R.string.delete), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    borrarElement(txtElement.getText().toString());
                }
            });

            //Cancel
            builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button positiveButton = ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                    Button neutralButton = ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEUTRAL);
                    Button negativeButton = ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEGATIVE);

                    positiveButton.setTextColor(Color.WHITE);
                    neutralButton.setTextColor(Color.WHITE);
                    negativeButton.setTextColor(Color.WHITE);
                }
            });

            dialog.show();

        }

    }

}
