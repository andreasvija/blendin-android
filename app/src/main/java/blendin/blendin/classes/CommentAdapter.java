/*
* Custom Adapter for RecyclerLayout displaying comments in PostActivity.
*/

package blendin.blendin.classes;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import blendin.blendin.R;
import blendin.blendin.activities.ProfileActivity;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private ArrayList<Comment> comments;

    public CommentAdapter(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        ImageView photoView;
        TextView nameView;
        TextView contentView;
        TextView timeAgoView;
        TextView locationView;
        ImageView translateButton;

        final Context context;

        // Holds all necessary views
        public ViewHolder(View view) {
            super(view);

            this.view = view;
            photoView = (ImageView) view.findViewById(R.id.author_photo);
            nameView = (TextView) view.findViewById(R.id.author_name);
            contentView = (TextView) view.findViewById(R.id.content);
            timeAgoView = (TextView) view.findViewById(R.id.timestamp);
            locationView = (TextView) view.findViewById(R.id.location);
            translateButton = (ImageView) view.findViewById(R.id.translate_button);
            context = view.getContext();
        }
    }

    //Initialize a new post view - called when RecyclerView starts
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {

        // Set up one comment's template to be updated later
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_comment, parent, false);

        return new CommentAdapter.ViewHolder(view);
    }

    // Fill newly initialized or already existing view with new data
    // (to save resources, RecyclerView only keeps a few fragment views
    // and when the view is scrolled, changes the content of existing views)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Comment comment = comments.get(position);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference authorReference = database.getReference("users").child(comment.getAuthorID());

        ChildEventListener userListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equals("name")) {
                    String authorName = (String) dataSnapshot.getValue();
                    holder.nameView.setText(authorName);
                }
                else if (dataSnapshot.getKey().equals("photoURL")) {
                    String authorPhotoURL = (String) dataSnapshot.getValue();
                    Picasso.with(holder.context)
                            .load(authorPhotoURL)
                            //.resize(width,height).noFade()
                            .into(holder.photoView);
                }
            }
            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override public void onCancelled(DatabaseError databaseError) {}
        };

        authorReference.addChildEventListener(userListener);

        holder.contentView.setText(comment.getContent());
        CharSequence ago = DateUtils.getRelativeTimeSpanString(comment.getTimestamp(),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        holder.timeAgoView.setText(ago);

        Geocoder geoCoder = new Geocoder(holder.context, Locale.getDefault());
        try {
            List<Address> list = geoCoder.getFromLocation(comment.getLatitude(), comment.getLongitude(), 1);
            if (list != null) {
                if (list.size() > 0) {
                    String location = list.get(0).getLocality();
                    holder.locationView.setText(location);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        holder.nameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.context, ProfileActivity.class);
                intent.putExtra("userID", comment.getAuthorID());
                holder.context.startActivity(intent);
            }
        });

        holder.translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.MyDialogTheme);
                // The Android Dialog is missing theme resources if the app theme is not an Appcompat one
                builder.setTitle("Choose language to translate into")
                        .setPositiveButton("Translate",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        final TextView contentView = (TextView) ((ViewGroup) v.getParent().getParent().getParent()).getChildAt(1);
                                        ListView listView = ((AlertDialog)dialog).getListView();
                                        int position = listView.getCheckedItemPosition();
                                        String languageName = (String) listView.getAdapter().getItem(position);
                                        ArrayList<String> names = new ArrayList<>(Arrays.asList(v.getContext().getResources().getStringArray(R.array.language_names_array)));
                                        ArrayList<String> codes = new ArrayList<>(Arrays.asList(v.getContext().getResources().getStringArray(R.array.language_codes_array)));
                                        final String languageCode = codes.get(names.indexOf(languageName));

                                        Thread thread = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Translate translate = TranslateOptions.newBuilder().setApiKey(v.getContext().getResources().getString(R.string.google_api_key)).build().getService();
                                                Translate.TranslateOption target = Translate.TranslateOption.targetLanguage(languageCode);
                                                final Translation contentTranslation = translate.translate(contentView.getText().toString(), target);
                                                ((Activity) v.getContext()).runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        contentView.setText(contentView.getText() + "\n-----\n" + contentTranslation.getTranslatedText());
                                                    }
                                                });
                                            }
                                        });
                                        thread.start();
                                    }
                                })
                        .setNegativeButton("cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                })
                        .setSingleChoiceItems(R.array.language_names_array,
                                0,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {}
                                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

}
