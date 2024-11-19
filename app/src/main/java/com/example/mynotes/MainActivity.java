package com.example.mynotes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.mynotes.Note;
import com.example.mynotes.databinding.ActivityMainBinding;
import com.example.mynotes.databinding.ListLayoutBinding;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ListLayoutBinding bindingList;
    private ExecutorService executorService;
    private NoteDao mNoteDao;
    private Note selectedNote;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        executorService = Executors.newSingleThreadExecutor();
        NoteRoomDatabase db = NoteRoomDatabase.getDatabase(this);
        mNoteDao = db.noteDao();

        getAllnotes();

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Note note = new Note(
                        binding.editTitle.getText().toString(),
                        binding.editDesc.getText().toString(),
                        getCurrentDate()
                );
                insert(note);
                setEmptyField();
            }
        });

        Intent intent = getIntent();
        if (intent.hasExtra("selectedNote")) {
            selectedNote = (Note) intent.getSerializableExtra("selectedNote");
            binding.editTitle.setText(selectedNote.getTitle());
            binding.editDesc.setText(selectedNote.getDescription());
        }

        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedNote != null) {
                    selectedNote.setTitle(binding.editTitle.getText().toString());
                    selectedNote.setDescription(binding.editDesc.getText().toString());
                    update(selectedNote);
                    setEmptyField();
                }
            }
        });

        binding.btnViewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                startActivity(intent);
            }
        });
    }




    private void getAllnotes() {
        mNoteDao.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                // Handle the list of notes as needed
            }
        });
    }

    private void setEmptyField() {
        binding.editTitle.setText("");
        binding.editDesc.setText("");
    }

    private void insert(final Note note) {
        executorService.execute(() -> mNoteDao.insert(note));
    }

    private void update(final Note note) {
        executorService.execute(() -> mNoteDao.update(note));
    }

    private String getCurrentDate() {
        // Implement logic to get the current date
        // For simplicity, I'm returning a placeholder string
        return "2023-01-01";
    }
}

