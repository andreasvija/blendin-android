/*
* Custom ImageView with built-in translation request functionality
*/

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

    // Following constructors required for inflating this view

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

    // Set up translation requests
    public void startListening(final Context context, final TextView titleView, final TextView contentView) {

        this.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // Ask for the language to translate to

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

                                    }
                                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    // Listener activated when user has selected the language to translate into in the dialog
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

            // Get target language code and start the Runnable of the request

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

    // Custom Runnable to request translation and act on it
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
            Translate translate = TranslateOptions.newBuilder().setApiKey(context.getResources()
                    .getString(R.string.google_api_key)).build().getService();
            Translate.TranslateOption target = Translate.TranslateOption.targetLanguage(languageCode);

            final Translation titleTranslation;

            // If the translation was requested for a post, the titleView will have a non-null value
            if (titleView != null) {
                titleTranslation = translate.translate(titleView.getText().toString(), target);
            }
            else {
                titleTranslation = null;
            }

            final Translation contentTranslation = translate.translate(contentView.getText().toString(), target);

            // Update the texts of given view(s) on the UI thread according to translation results
            // TODO: take into account whether given text has already been translated
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // If there is a title to be translated
                    if (titleTranslation != null) {
                        titleView.setText(titleView.getText() + "\n-----\n" + titleTranslation.getTranslatedText());
                    }
                    contentView.setText(contentView.getText() + "\n-----\n" + contentTranslation.getTranslatedText());
                }
            });
        }
    }

}
