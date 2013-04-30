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
		
		// �{�^���𐶐�
		Button btn = new Button(this);
		btn.setText("�{�^��");
		// ���C�A�E�g�Ƀ{�^����ǉ�
		LinearLayout layout = new LinearLayout(this);
		layout.addView(btn, new LinearLayout.LayoutParams(
		LinearLayout.LayoutParams.WRAP_CONTENT,
		LinearLayout.LayoutParams.WRAP_CONTENT));
		
		setContentView(layout);
		
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// �C���e���g�̃C���X�^���X����
				Intent intent = new Intent(TopActivity.this, KadaiMainActivity.class);
				// ����ʂ̃A�N�e�B�r�e�B�N��
				startActivity(intent);
			}
		});
	}
}
