package com.example.mynotes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.mynotes.databinding.ListLayoutBinding;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ListActivity extends AppCompatActivity {

    private ListLayoutBinding bindingList;
    private NoteDao mNoteDao;
    private Note selectedNote;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingList = ListLayoutBinding.inflate(getLayoutInflater());
        setContentView(bindingList.getRoot());

        mNoteDao = NoteRoomDatabase.getDatabase(this).noteDao();
        executorService = Executors.newSingleThreadExecutor(); // Initialize executorService

        getAllNotes();

        bindingList.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedNote = (Note) adapterView.getAdapter().getItem(i);
                showNoteInForm(selectedNote); // Call showNoteInForm with the selected note
            }
        });

        bindingList.listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedNote = (Note) adapterView.getAdapter().getItem(i);
                delete(selectedNote); // Call delete with the selected note
                return true;
            }
        });
    }

    private void getAllNotes() {
        mNoteDao.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                ArrayAdapter<Note> adapter = new ArrayAdapter<>(ListActivity.this,
                        android.R.layout.simple_list_item_1, notes);
                bindingList.listView.setAdapter(adapter);
            }
        });
    }

    private void showNoteInForm(Note note) {
        Intent intent = new Intent(ListActivity.this, MainActivity.class);
        intent.putExtra("selectedNote", note);
        startActivity(intent);
    }

    private void insert(final Note note) {
        executorService.execute(() -> mNoteDao.insert(note));
    }

    private void update(final Note note) {
        executorService.execute(() -> mNoteDao.update(note));
    }

    private void delete(final Note note) {
        executorService.execute(() -> mNoteDao.delete(note));
    }
}
