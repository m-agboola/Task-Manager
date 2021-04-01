package com.twilioapp.roomdbapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.twilioapp.roomdbapp.notedb.Note;
import com.twilioapp.roomdbapp.notedb.NoteDatabase;

import java.lang.ref.WeakReference;

public class AddNoteActivity extends AppCompatActivity {

    // Variables:
    private TextInputEditText et_title, et_content;
    private boolean update;

    Button button;


    // Objects:
    private NoteDatabase noteDatabase;
    private Note note;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);


        // Widgets:
        et_title = findViewById(R.id.et_title);
        et_content = findViewById(R.id.et_content);
        button = findViewById(R.id.but_save);

        // Objects:
        noteDatabase = NoteDatabase.getInstance(AddNoteActivity.this);

        // Check for correct object and data:
        if ((note = (Note) getIntent().getSerializableExtra("note")) != null) {
            getSupportActionBar().setTitle("Update Note");
            update = true;
            button.setText("Update");
            et_title.setText(note.getTitle());
            et_content.setText(note.getContent());
        }


        // Handling button click events
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If we need to update the note:
                if (update) {
                    note.setContent(et_content.getText().toString());
                    note.setTitle(et_title.getText().toString());
                    noteDatabase.getNoteDao().updateNote(note);
                    setResult(note, 2);

                    // Create a new note
                } else {
                    note = new Note(et_content.getText().toString(), et_title.getText().toString());
                    new InsertTask(AddNoteActivity.this, note).execute();
                }
            }
        });
    }

    // Set Results method:
    private void setResult(Note note, int flag) {
        setResult(flag, new Intent().putExtra("note", note));
        finish();
    }


    // Insert Task:
    private static class InsertTask extends AsyncTask<Void, Void, Boolean>{
        private WeakReference<AddNoteActivity> activityWeakReference;
        private Note note;


        //Only retain a weak reference to the activity:
        InsertTask(AddNoteActivity context, Note note) {
            activityWeakReference = new WeakReference<>(context);
            this.note = note;

        }

        // Do in background methods runs on a worker thread


        @Override
        protected Boolean doInBackground(Void... voids) {

            // retrieving auto incremented note id
            long j = activityWeakReference.get().noteDatabase.getNoteDao().insertNote(note);
            note.setNote_id(j);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                activityWeakReference.get().setResult(note, 1);
                activityWeakReference.get().finish();
             }
        }
    }


}