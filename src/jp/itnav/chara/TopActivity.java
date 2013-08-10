package jp.itnav.chara;

import android.location.Location;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.Button;
import android.widget.LinearLayout;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;


public class TopActivity extends Activity {
	Button btn1;
	Button btn2;
	Button btn3;
	Button btn4;
	Button btn5;
	Button btn6;
	Button btn7;
	Button btn8;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// �{�^���𐶐�
		btn1 = new Button(this);
		btn1.setText("�Ί�");
		btn2 = new Button(this);
		btn2.setText("�勽");
		btn3 = new Button(this);
		btn3.setText("���");
		btn4 = new Button(this);
		btn4.setText("�k��");
		btn5 = new Button(this);
		btn5.setText("���");
		btn6 = new Button(this);
		btn6.setText("�ΐ�");
		btn7 = new Button(this);
		btn7.setText("�R");
		btn8 = new Button(this);
		btn8.setText("�Z�{��");
		
		// ���C�A�E�g�Ƀ{�^����ǉ�
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
		layout1.addView(btn7,new LinearLayout.LayoutParams(
		LinearLayout.LayoutParams.MATCH_PARENT,
		LinearLayout.LayoutParams.WRAP_CONTENT));
		layout1.addView(btn8,new LinearLayout.LayoutParams(
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
		btn7.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDetail(6);
			}
		});
		btn8.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDetail(7);
			}
		});
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		SharedPreferences prefer = getSharedPreferences("getchara", MODE_PRIVATE);
		
		btn1.setText(prefer.getString("�Ί�", ""));
	}
	private void showDetail(int id){
		// TODO Auto-generated method stub
		// �C���e���g�̃C���X�^���X����
		Intent intent = new Intent(this, KadaiMainActivity.class);
		intent.putExtra("ID", id);
		// ����ʂ̃A�N�e�B�r�e�B�N��
		startActivity(intent);
	}

	
}
