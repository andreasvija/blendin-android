package blendin.blendin.classes;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import java.util.ArrayList;
import java.util.Arrays;

import blendin.blendin.R;

public class TranslateButton extends ImageView {

    public TranslateButton(Context context) {
        super(context);
    }

    public TranslateButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TranslateButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /*public TranslateButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }*/

    public void startListening(final Context context, final TextView titleView, final TextView contentView) {

        this.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyDialogTheme);
                // The Android Dialog is missing theme resources if the app theme is not an Appcompat one

                builder.setTitle("Choose language to translate into")

                        .setPositiveButton("Translate",
                                new OnLanguageSelectListener(context, titleView, contentView))

                        .setNegativeButton("cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                })
                        .setSingleChoiceItems(R.array.language_names_array,
                                0,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //String[] codes = getResources().getStringArray(R.array.language_codes_array);
                                        //languageCode = codes[which];
                                        //Log.d("###", "Chosen: " + codes[which]);
                                    }
                                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private class OnLanguageSelectListener implements DialogInterface.OnClickListener {

        Context context;
        TextView titleView;
        TextView contentView;

        OnLanguageSelectListener(Context context, TextView titleView, TextView contentView) {
            this.context = context;
            this.titleView = titleView;
            this.contentView = contentView;
        }

        public void onClick(DialogInterface dialog, int id) {
            //final TextView titleView = (TextView) ((ViewGroup) v.getParent().getParent().getParent()).getChildAt(1);
            //final TextView contentView = (TextView) ((ViewGroup) v.getParent().getParent().getParent().getParent().getParent()).getChildAt(1);
            ListView listView = ((AlertDialog)dialog).getListView();
            int position = listView.getCheckedItemPosition();
            String languageName = (String) listView.getAdapter().getItem(position);

            ArrayList<String> names = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.language_names_array)));
            ArrayList<String> codes = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.language_codes_array)));

            final String languageCode = codes.get(names.indexOf(languageName));

            Thread thread = new Thread(new TranslationRunnable(context, titleView, contentView, languageCode));
            thread.start();
        }

    }

    private class TranslationRunnable implements Runnable {

        Context context;
        TextView titleView;
        TextView contentView;
        String languageCode;

        TranslationRunnable(Context context, TextView titleView, TextView contentView, String languageCode) {
            this.context = context;
            this.titleView = titleView;
            this.contentView = contentView;
            this.languageCode = languageCode;
        }

        @Override
        public void run() {
            Translate translate = TranslateOptions.newBuilder().setApiKey(context.getResources().getString(R.string.google_api_key)).build().getService();
            Translate.TranslateOption target = Translate.TranslateOption.targetLanguage(languageCode);

            final Translation titleTranslation;
            if (titleView != null) {
                titleTranslation = translate.translate(titleView.getText().toString(), target);
            }
            else {
                titleTranslation = null;
            }
            final Translation contentTranslation = translate.translate(contentView.getText().toString(), target);

            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (titleTranslation != null) {
                        titleView.setText(titleView.getText() + "\n-----\n" + titleTranslation.getTranslatedText());
                    }
                    contentView.setText(contentView.getText() + "\n-----\n" + contentTranslation.getTranslatedText());
                }
            });
        }
    }

}
