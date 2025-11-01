package com.example.notekeeperapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class AddEditNoteActivity extends AppCompatActivity {

    EditText etTitle, etContent;
    Button btnSave, btnDelete;
    DatabaseHelper db;
    int noteId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_note);

        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);
        db = new DatabaseHelper(this);

        // Determine if editing or adding
        if (getIntent().hasExtra("id")) {
            noteId = getIntent().getIntExtra("id", -1);
            etTitle.setText(getIntent().getStringExtra("title"));
            etContent.setText(getIntent().getStringExtra("content"));
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            btnDelete.setVisibility(View.GONE);
        }

        // SAVE button
        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String content = etContent.getText().toString().trim();

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Title and content cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (noteId == -1) {
                db.addNote(title, content);
                new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Saved!")
                        .setContentText("Your note has been added.")
                        .setConfirmText("OK")
                        .show();
            } else {
                db.updateNote(noteId, title, content);
                new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Updated!")
                        .setContentText("Your note has been updated.")
                        .setConfirmText("OK")
                        .show();
            }
        });

        // DELETE button with confirmation
        btnDelete.setOnClickListener(v -> {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Delete Note?")
                    .setContentText("Are you sure you want to delete this note?")
                    .setConfirmText("Yes, delete it!")
                    .setCancelText("Cancel")
                    .setConfirmClickListener(dialog -> {
                        db.deleteNote(noteId);
                        dialog.dismissWithAnimation();
                        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Deleted!")
                                .setContentText("Your note has been removed.")
                                .setConfirmText("OK")
                                .setConfirmClickListener(sDialog -> {
                                    sDialog.dismissWithAnimation();
                                    finish(); // Close activity and go back to main screen
                                })
                                .show();
                    })
                    .show();
        });
    }
}
