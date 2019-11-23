package com.example.musicplayerproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private Button btnPlay, btnStop, btnAdd;
    private TextView tvSelectedSong, tvNowPlaying;
    EditText edtSinger, edtGenre, edtAlbumArt;
    private ListView listView;
    private ImageView ivPlaylist;
    private MediaPlayer mediaPlayer;

    private String selectedMP3;
    private static final String MP3_PATH = Environment.getExternalStorageDirectory().getPath() + "/";
    private ArrayList<String> mp3List = new ArrayList<String>();
    static ArrayList<MusicItemDTO> items = new ArrayList<MusicItemDTO>();

    private MusicItemDAO mDAO;
    private MyDBHelper myDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sdcard_fragment);
        setTitle("뭐 들을래?");

        // 외부 저장장치 쓰기 권한 요청
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);

        listView = findViewById(R.id.listView);
        btnPlay = findViewById(R.id.btnPlay);
        btnStop = findViewById(R.id.btnStop);
        btnAdd = findViewById(R.id.btnAdd);
        ivPlaylist = findViewById(R.id.ivPlaylist);

        loadFromSDCard();
        myDBHelper = new MyDBHelper(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, mp3List);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(adapter);
        listView.setItemChecked(0, true);

        // initial setting
        selectedMP3 = mp3List.get(0);
        btnStop.setEnabled(false);
        btnStop.setTextColor(Color.DKGRAY);
        mDAO = new MusicItemDAO(this);

        btnPlay.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        ivPlaylist.setOnClickListener(this);
        listView.setOnItemClickListener(this);

    }

    private void loadFromSDCard() {
        // SDcard의 파일을 읽어서 리스트뷰에 출력
        mp3List = new ArrayList<String>();
        File[] listFiles = new File(MP3_PATH).listFiles();
        String fileName, extendName;
        for(File file : listFiles){
            fileName = file.getName(); // 파일명 또는 디렉토리명
            extendName = fileName.substring(fileName.length() - 3); // 확장자명 가져오기 (마지막 3글자 가져오기)
            // 확장명이 mp3인 경우만 추가
            if(extendName.equals("mp3") || extendName.equals("mp4")) mp3List.add(fileName);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivPlaylist:
                Intent intent = new Intent(MainActivity.this, MyPlaylistActivity.class);
                startActivity(intent);
            case R.id.btnAdd:
                if(mDAO.isExist(selectedMP3) != null) {
                    mDAO.toastDisplay("이미 추가된 곡입니다");
                    Log.d("click", "exist");
                    break;
                }
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                View dialogView = LayoutInflater.from(this).inflate(R.layout.add_song, null);
                dialog.setView(dialogView);
                dialog.setTitle("Enter extra info");

                edtSinger = dialogView.findViewById(R.id.edtSinger);
                edtGenre = dialogView.findViewById(R.id.edtGenre);
                edtAlbumArt = dialogView.findViewById(R.id.edtAlbumArt);
                tvSelectedSong = dialogView.findViewById(R.id.tvSelectedSong);

                tvSelectedSong.setText(trimFileName());

                dialog.setPositiveButton("ADD TO MY LIST", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(edtSinger.getText().toString().equals("") || edtAlbumArt.getText().toString().equals("")
                                ||  edtGenre.getText().toString().equals("")) return;

                        MusicItemDAO mDAO = new MusicItemDAO(MainActivity.this);
                        mDAO.insert(selectedMP3, edtSinger.getText().toString().trim(),
                                edtGenre.getText().toString().trim(), edtAlbumArt.getText().toString().trim());
                        Toast.makeText(getApplicationContext(), selectedMP3 + "을 추가하였습니다!", Toast.LENGTH_SHORT).show();
                        Log.d("onclick", selectedMP3);
                    }
                });

                dialog.setNegativeButton("BACK", null);
                dialog.show();
                break;

            case R.id.btnPlay:
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(MP3_PATH + selectedMP3);
                    mediaPlayer.prepare(); // 외부파일을 가져오기 위해 준비
                    mediaPlayer.start();
                    btnPlay.setTextColor(Color.DKGRAY);
                    btnStop.setTextColor(Color.WHITE);
                    btnPlay.setEnabled(false);
                    btnStop.setEnabled(true);
                } catch (IOException e) {
                    Log.e("btnPlay", "exception");
                }
                break;

            case R.id.btnStop:
                mediaPlayer.stop();
                mediaPlayer.reset();
                btnPlay.setTextColor(Color.WHITE);
                btnStop.setTextColor(Color.DKGRAY);
                btnStop.setEnabled(false);
                btnPlay.setEnabled(true);
                break;
        }
    }



    public String trimFileName() {
        String extractedName;
        // .확장자명 잘라내기
        int idx = selectedMP3.indexOf(".");
        // . 앞부분을 추출
        extractedName = selectedMP3.substring(0, idx);

        // _를 공백으로 바꿈
        String tempName[] = extractedName.split("_");
        extractedName = "";
        for(int i = 0 ; i < tempName.length ; i++)
        {
            extractedName += (tempName[i] + " ");
        }
        Log.d("trimFileName", selectedMP3);

        return extractedName;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectedMP3 = mp3List.get(position);
        Log.d("onItemClick", selectedMP3);
    }
}
