package com.gclue.kadai;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.Button;
import android.widget.LinearLayout;
import android.content.Intent;
import android.view.View;


public class TopActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// ボタンを生成
		Button btn = new Button(this);
		btn.setText("ボタン");
		// レイアウトにボタンを追加
		LinearLayout layout = new LinearLayout(this);
		layout.addView(btn, new LinearLayout.LayoutParams(
		LinearLayout.LayoutParams.WRAP_CONTENT,
		LinearLayout.LayoutParams.WRAP_CONTENT));
		
		setContentView(layout);
		
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// インテントのインスタンス生成
				Intent intent = new Intent(TopActivity.this, KadaiMainActivity.class);
				// 次画面のアクティビティ起動
				startActivity(intent);
			}
		});
	}
}
