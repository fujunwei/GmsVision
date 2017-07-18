package com.example.gmsvision;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private  final String base64Image = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAUoAAAFKAQMAAABB54RGAAAABlBMVEX///8AAABVwtN+AAABUUlEQVRoge2ZwY3FIAxELf0CUlJap6QUEMkbbA+wi5Q92l+aOYQQHqcRZiAiFEVRFPVt0tAtcl7iD20HPjeipVDvHPcz6fr4uHXXUaJl0G5i62MnKOua2USrombsw9/wmWhhdHpKtC5qzaDOXjnbf/WVaBIaQqmMBz4TLYX+0TP2eQWIZqLh3wF3I/b3za4/iJZC4yzW7dQL03v3aYmWQ5d0j6MZZm4LkmgyakNx46F2NNPXQEI0EzUAfCw+jXyiSrQWupZKX4uCI/W+GxLNRj0v4vjsPns+2Yom0XwUq23ucyJI/EQLoksqcd7dveWXiKaj6yRv4emopkTLoIgjCPtGzZuqRrQU6h3sbrEgJYwlWg0dv8LGZQeK5mYs0Tqo53y7UtSxzxEtirYDlAHzjWgd1JqZHC/cJr7UV6JZaGgN+/6veV+GRLNRiqIoivoO/QAJkSlOdmbU0AAAAABJRU5ErkJggg==";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String pureBase64Encoded = base64Image.substring(base64Image.indexOf(","));
        byte[] bytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        ImageView image =(ImageView)findViewById(R.id.image);
        image.setImageBitmap(bitmap);
    }
}
