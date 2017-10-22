package com.example.arshdeepsingh.mymemegenerator;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arshdeepsingh.mymemegenerator.Activities.AboutActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.R.attr.bitmap;

public class MainActivity extends AppCompatActivity {

    private static int RESULT_SELECT_IMAGE = 1;


    private Toolbar mTopToolbar;


    Button btnSelect;
    Button btnPreview;


    EditText etTop;
    EditText etBottom;

    TextView tvTop;
    TextView tvBottom;

    ImageView imgView;

    RelativeLayout rel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTopToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mTopToolbar);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));





        etTop = (EditText) findViewById(R.id.etTop);
        etBottom = (EditText) findViewById(R.id.etBottom);

        tvTop = (TextView) findViewById(R.id.tvTop);
        tvBottom = (TextView) findViewById(R.id.tvBottom);

        imgView = (ImageView) findViewById(R.id.imgView);

        rel = (RelativeLayout) findViewById(R.id.rel);

        tvTop.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    // Offsets are for centering the TextView on the touch location
                    tvTop.setX(event.getRawX() );
                    tvTop.setY(event.getRawY()-rel.getHeight()+10);
                }

                return true;
            }

        });

        tvBottom.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    tvBottom.setX(event.getRawX() );
                    tvBottom.setY(event.getRawY()-rel.getHeight()+10);
                }

                return true;
            }

        });


        btnSelect = (Button) findViewById(R.id.btnSelect);
        btnSelect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_SELECT_IMAGE);
            }
        });

        btnPreview = (Button) findViewById(R.id.btnPreview);
        btnPreview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                tvTop.setText(etTop.getText().toString());
                tvBottom.setText(etBottom.getText().toString());
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_SELECT_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            

            ImageView imageView = (ImageView) findViewById(R.id.imgView);
            imageView.setImageBitmap(decodeFile(new File(picturePath)));



        }


    }

    public Bitmap getBitmapFromView(View view, int totalHeight, int totalWidth) {
        Bitmap returnedBitmap = Bitmap.createBitmap(totalWidth,totalHeight ,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }

    private Bitmap decodeFile(File f){
        Bitmap b = null;

        // decode image size

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        FileInputStream fis = null;
        try{
            fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis,null,o);
            fis.close();
        }catch(FileNotFoundException e){
            e.printStackTrace();

        }catch (IOException e){

            e.printStackTrace();

        }

        int IMAGE_MAX_SIZE = 512;
        int scale = 1;
        if(o.outHeight>IMAGE_MAX_SIZE||o.outWidth>IMAGE_MAX_SIZE){
            scale = (int)Math.pow(2,(int)Math.ceil(Math.log(IMAGE_MAX_SIZE/(double)Math.max(o.outHeight,o.outWidth))/Math.log(0.5)));

        }

        //Decode with inSampleSize

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        try{
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis,null,o2);
            fis.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }


        ExifInterface exif = null;
        try {
            exif = new ExifInterface(f.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            }
            else if (orientation == 3) {
                matrix.postRotate(180);
            }
            else if (orientation == 8) {
                matrix.postRotate(270);
            }
            b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true); // rotating bitmap



        return b;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            share();
            return true;
        }
        if (id == R.id.action_save) {
            save();
            return true;
        }

        if(id == R.id.action_about){
            Intent i = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(i);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public void save(){
        BitmapDrawable draw = (BitmapDrawable) imgView.getDrawable();
        Bitmap bitmap = draw.getBitmap();

        int totalHeight = rel.getHeight();
        int totalWidth  = rel.getWidth();

        Bitmap b = getBitmapFromView(rel,totalHeight,totalWidth);


        FileOutputStream outStream = null;
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + "/Memes");
        if(!dir.exists()){
            dir.mkdirs();
        }
        String fileName = String.format("%d.jpg", System.currentTimeMillis());
        File outFile = new File(dir, fileName);
        try {
            outStream = new FileOutputStream(outFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        b.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
        try {
            outStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(outFile));
        sendBroadcast(intent);

        Toast.makeText(MainActivity.this, "Meme saved", Toast.LENGTH_SHORT).show();

    }

    public void share(){
        View content = findViewById(R.id.imgView);
        content.setDrawingCacheEnabled(true);

        int totalHeight = rel.getHeight();
        int totalWidth  = rel.getWidth();

        Bitmap bitmap = getBitmapFromView(rel,totalHeight,totalWidth);
        
        File root = Environment.getExternalStorageDirectory();
        File cachePath = new File(root.getAbsolutePath() + "/DCIM/Camera/image"+System.currentTimeMillis()+".jpg");
        try {
            cachePath.createNewFile();
            FileOutputStream ostream = new FileOutputStream(cachePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
            ostream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(cachePath));
        startActivity(Intent.createChooser(share,"Share via"));

    }

}

