package jp.itnav.chara;

import android.location.Location;
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
		Button btn1 = new Button(this);
		btn1.setText("石巻");
		Button btn2 = new Button(this);
		btn2.setText("大郷");
		Button btn3 = new Button(this);
		btn3.setText("仙台");
		Button btn4 = new Button(this);
		btn4.setText("北上");
		Button btn5 = new Button(this);
		btn5.setText("滝沢");
		Button btn6 = new Button(this);
		btn6.setText("石川");
		
		// レイアウトにボタンを追加
		LinearLayout layout1 = new LinearLayout(this);
		layout1.setOrientation(LinearLayout.VERTICAL);
		layout1.addView(btn1,new LinearLayout.LayoutParams(
		LinearLayout.LayoutParams.MATCH_PARENT,
		LinearLayout.LayoutParams.WRAP_CONTENT));
		layout1.addView(btn2,new LinearLayout.LayoutParams(
		LinearLayout.LayoutParams.MATCH_PARENT,
		LinearLayout.LayoutParams.WRAP_CONTENT));
		layout1.addView(btn3,new LinearLayout.LayoutParams(
		LinearLayout.LayoutParams.MATCH_PARENT,
		LinearLayout.LayoutParams.WRAP_CONTENT));
		layout1.addView(btn4,new LinearLayout.LayoutParams(
		LinearLayout.LayoutParams.MATCH_PARENT,
		LinearLayout.LayoutParams.WRAP_CONTENT));
		layout1.addView(btn5,new LinearLayout.LayoutParams(
		LinearLayout.LayoutParams.MATCH_PARENT,
		LinearLayout.LayoutParams.WRAP_CONTENT));
		layout1.addView(btn6,new LinearLayout.LayoutParams(
		LinearLayout.LayoutParams.MATCH_PARENT,
		LinearLayout.LayoutParams.WRAP_CONTENT));
		
		setContentView(layout1);
		
		btn1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDetail(0);
			}
		});
		btn2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDetail(1);
			}
		});
		btn3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDetail(2);
			}
		});
		btn4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDetail(3);
			}
		});
		btn5.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDetail(4);
			}
		});
		btn6.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDetail(5);
			}
		});
	}
	
	private void showDetail(int id){
		// TODO Auto-generated method stub
		// インテントのインスタンス生成
		Intent intent = new Intent(this, KadaiMainActivity.class);
		intent.putExtra("ID", id);
		// 次画面のアクティビティ起動
		startActivity(intent);
	}
}
