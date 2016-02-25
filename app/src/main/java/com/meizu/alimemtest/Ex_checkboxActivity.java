package com.meizu.alimemtest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.meizu.alimemtest.MyAdapter.ViewHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Ex_checkboxActivity extends Activity {
	static String jardir = "/storage/emulated/0/jars/";
	private ListView lv;
	private MyAdapter mAdapter;
	private Button bt_selectall;
	private Button bt_cancel;
	private Button bt_deselectall;
	private Button bt_runtest;
	private int checkNum; // 记录选中的条目数量
	private TextView tv_show;// 用于显示选中的条目数量
	final ArrayList<String> list = getJarList();
	static Map testcasemap = getTestCaseList();

//	private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		/* 实例化各个控件 */

		lv = (ListView) findViewById(R.id.lv);
		bt_selectall = (Button) findViewById(R.id.bt_selectall);
		bt_cancel = (Button) findViewById(R.id.bt_cancleselectall);
		bt_deselectall = (Button) findViewById(R.id.bt_deselectall);
		bt_runtest = (Button) findViewById(R.id.bt_runtest);
		tv_show = (TextView) findViewById(R.id.tv);

//		list = new ArrayList<String>();
		// 为Adapter准备数据
//		initDate();

		// 实例化自定义的MyAdapter
		mAdapter = new MyAdapter(list, this);
		// 绑定Adapter
		lv.setAdapter(mAdapter);
	}

	@Override
	protected void onStart() {
		super.onStart();
		// 全选按钮的回调接口
		bt_selectall.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 遍历list的长度，将MyAdapter中的map值全部设为true
				for (int i = 0; i < list.size(); i++) {
					MyAdapter.getIsSelected().put(i, true);
				}
				// 数量设为list的长度
				checkNum = list.size();
				// 刷新listview和TextView的显示
				dataChanged();
			}
		});

		// 反选按钮的回调接口
		bt_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 遍历list的长度，将已选的设为未选，未选的设为已选
				for (int i = 0; i < list.size(); i++) {
					if (MyAdapter.getIsSelected().get(i)) {
						MyAdapter.getIsSelected().put(i, false);
						checkNum--;
					} else {
						MyAdapter.getIsSelected().put(i, true);
						checkNum++;
					}
				}
				// 刷新listview和TextView的显示
				dataChanged();
			}
		});

		// 取消按钮的回调接口
		bt_deselectall.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 遍历list的长度，将已选的按钮设为未选
				for (int i = 0; i < list.size(); i++) {
					if (MyAdapter.getIsSelected().get(i)) {
						MyAdapter.getIsSelected().put(i, false);
						checkNum--;// 数量减1
					}
				}
				// 刷新listview和TextView的显示
				dataChanged();
			}
		});

		// 绑定listView的监听器
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				// 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤
				ViewHolder holder = (ViewHolder) arg1.getTag();
				// 改变CheckBox的状态
				holder.cb.toggle();
				// 将CheckBox的选中状况记录下来
				MyAdapter.getIsSelected().put(arg2, holder.cb.isChecked());
				// 调整选定条目
				if (holder.cb.isChecked() == true) {
					checkNum++;
				} else {
					checkNum--;
				}
				// 用TextView显示
				tv_show.setText("已选中" + checkNum + "项");
			}
		});

		bt_runtest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.setClass(Ex_checkboxActivity.this, runUIAService.class);
				Ex_checkboxActivity.this.startService(i);
			}
		});

	}

	// 检查扩展名，得到图片格式的文件
	private static boolean checkIsJarFile(String fName) {
		boolean isJarFile = false;

		// 获取扩展名
		String FileEnd = fName.substring(fName.lastIndexOf(".") + 1,
				fName.length()).toLowerCase();
		if (FileEnd.equals("jar")) {
			isJarFile = true;
		} else {
			isJarFile = false;
		}

		return isJarFile;

	}

	// 刷新listview和TextView的显示
	private void dataChanged() {
		// 通知listView刷新
		mAdapter.notifyDataSetChanged();
		// TextView显示最新的选中数目
		tv_show.setText("已选中" + checkNum + "项");
	};


	public static Map<String,String[]> getTestCaseList() {
		Map<String,String[]> testcase = new HashMap<String, String[]>();

		String[] feedback = {"com.meizu.feedback","com.meizu.feedback.test.SanityTestCase"};
		String[] flymecommunication = {"com.android.mms","com.meizu.flymecommunication.test.FlymecommunicationSanityTestCase"};
		String[] reader = {"com.meizu.media.reader","com.meizu.reader.test.ReaderSanityTestCase"};
		String[] map = {"com.meizu.net.map","com.meizu.map.test.MapSanityTestCase"};
		String[] search = {"com.meizu.net.search","com.meizu.search.test.SanitySearchTestCase"};
		String[] paymentcenter = {"com.meizu.account","com.meizu.paymentcenter.test.SanityPaymentcenterTestCase"};
		String[] backuprecovery = {"com.meizu.backup","com.meizu.backuprecovery.test.TestCaseSanity"};
		String[] account = {"com.meizu.account","com.meizu.account.test.SanityAccountTestCase"};
		String[] homescreencloudbackup = {"com.meizu.mzsyncservice","com.meizu.homescreencloudbackup.test.SanityHomeScreenCloudBackup"};
		String[] calendar = {"com.android.calendar","com.meizu.calendar.test.SanityTestCase"};
		String[] filemanager = {"com.meizu.filemanager","com.meizu.filemanager.test.FilemanagerSanityTestCase"};
		String[] gamecenter = {"com.meizu.flyme.gamecenter","com.meizu.gamecenter.test.SanityTestCase"};
		String[] weather = {"com.meizu.flyme.weather","com.meizu.weather.test.SanityTest"};
		String[] clouddisk = {"com.meizu.flyme.clouddisk","com.meizu.clouddisk.test.Flyme51SanityTest"};
		String[] clock = {"com.android.alarmclock","com.meizu.clock.test.ClockSanityTestNewCase"};

		String[] notepaper = {"com.meizu.notepaper","com.meizu.notepaper.test.SanityTestCase"};
		String[] appcenter = {"com.meizu.mstore","com.meizu.appcenter.test.SanityTestCase"};
		String[] calculator = {"com.meizu.flyme.calculator","com.meizu.calculator.test.CalculatorSanity"};
		String[] customize = {"com.meizu.customizecenter","com.meizu.customize.test.CustomizeSanityTestCase"};
		String[] flymesync = {"com.meizu.mzsyncservice","com.meizu.sync.datamaker.test.SyncSanityTestCase"};

//        String[] flymesync = {"com.meizu.mzsyncservice","com.meizu.sync.datamaker.test.SyncSanityTestCase#test002LoginAndStartSync"};
//        String[] notepaper = {"com.meizu.notepaper","com.meizu.notepaper.test.SanityTestCase#test001CreateMixNotePaper"};
//        String[] appcenter = {"com.meizu.mstore","com.meizu.appcenter.test.SanityTestCase#test004clearChache"};
//        String[] customize = {"com.meizu.customizecenter","com.meizu.customize.test.CustomizeSanityTestCase#test005CheckMyThemeFromSettingP1"};
//        String[] calculator = {"com.meizu.flyme.calculator","com.meizu.calculator.test.CalculatorSanity#test11WindowDrag"};

		testcase.put(jardir+"flymesync.jar",flymesync);
		testcase.put(jardir+"feedback.jar",feedback);
		testcase.put(jardir+"flymecommunication.jar",flymecommunication);
		testcase.put(jardir+"reader.jar",reader);
		testcase.put(jardir+"map.jar",map);
		testcase.put(jardir+"search.jar",search);
		testcase.put(jardir+"paymentcenter.jar",paymentcenter);
		testcase.put(jardir+"backuprecovery.jar",backuprecovery);
		testcase.put(jardir+"account.jar",account);
		testcase.put(jardir+"homescreencloudbackup.jar",homescreencloudbackup);
		testcase.put(jardir+"calculator.jar",calculator);
		testcase.put(jardir+"calendar.jar",calendar);
		testcase.put(jardir+"notepaper.jar",notepaper);
		testcase.put(jardir+"filemanager.jar",filemanager);
		testcase.put(jardir+"gamecenter.jar",gamecenter);
		testcase.put(jardir+"weather.jar",weather);
		testcase.put(jardir+"clouddisk.jar",clouddisk);
		testcase.put(jardir+"appcenter.jar",appcenter);
		testcase.put(jardir+"customize.jar",customize);
		testcase.put(jardir+"clock.jar",clock);

		return testcase;
	}

	// 从sd卡获取jar资源
	public static ArrayList<String> getJarList() {
		// 图片列表
		ArrayList<String> jarList = new ArrayList<String>();
		// 得到sd卡内路径
		File sdcardobj = Environment.getExternalStorageDirectory();
		String jarPath = sdcardobj.getAbsolutePath();//绝对
		// 得到该路径文件夹下所有的文件
		File mfile = new File(jarPath+"/jars");
		File[] files = mfile.listFiles();
		// 将所有的文件存入ArrayList中,并过滤所有图片格式的文件
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (checkIsJarFile(file.getPath())) {
				jarList.add(file.getPath());
			}

		}
		// 返回得到的图片列表
		return jarList;

	}
}