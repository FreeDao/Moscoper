package com.skycober.mineral.updateApk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.skycober.mineral.R;
import com.skycober.mineral.constant.RequestUrls;
import com.skycober.mineral.util.NetworkUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

/**
 * 检查更新
 * @author CF
 *
 */
public class UpDataApkAcitvity extends Activity {

	private ProgressDialog progress;
	private PackageManager manger;
	private PackageInfo info;
	private AlertDialog.Builder builder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		manger = getPackageManager();
		try {
			//获取包信息
			info = manger.getPackageInfo(getPackageName(),
					PackageManager.GET_ACTIVITIES);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setContentView(R.layout.updataapk);
		if (!NetworkUtil.getInstance().existNetwork(this)) {
			Toast.makeText(this, R.string.network_disable_error,
					Toast.LENGTH_SHORT).show();
			return;
		}
		progress = new ProgressDialog(this);
		progress.setTitle("温情提示");
		progress.setMessage("正在下载数据.....");

		//检查软件版本
		new MyTask().execute(RequestUrls.SERVER_BASIC_URL
				+ RequestUrls.UPDATAAPK_URL);
	}

	// InstallApk.apk
	class MyTask extends AsyncTask<String, Void, ApkInfo> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
			progress.show();
		}

		@Override
		protected ApkInfo doInBackground(String... params) {
			// TODO Auto-generated method stub

			return JsonUtils.parserJson(HttpUtils.getDate(params[0]));
		}

		@Override
		protected void onPostExecute(final ApkInfo result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			progress.dismiss();
			//检查软件版本
			if (result.getId() > info.versionCode) {
				builder = new AlertDialog.Builder(UpDataApkAcitvity.this);
				builder.setTitle("温情提示");
				builder.setCancelable(true);
				builder.setMessage("软件有更新是否下载安装");
				builder.setPositiveButton("下载", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						//下载新版本
						new ApkTask().execute(RequestUrls.SERVER_BASIC_URL+result.getUrl());
					}
				}).setNegativeButton("取消", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						UpDataApkAcitvity.this.finish();
					}
				});

				builder.create().show();
			} else {
				builder = new AlertDialog.Builder(UpDataApkAcitvity.this);
				builder.setTitle("温情提示");
				builder.setCancelable(true);
				builder.setMessage("你的软件已是最新版本").setPositiveButton("OK",
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								UpDataApkAcitvity.this.finish();
							}
						}).create().show();
			}

		}

	}

	//下载新版本
	class ApkTask extends AsyncTask<String, Void, byte[]> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			progress.show();
			super.onPreExecute();
		}

		@Override
		protected byte[] doInBackground(String... params) {
			// TODO Auto-generated method stub
			//下载新版本
			return HttpUtils.getApk(params[0]);
		}

		@Override
		protected void onPostExecute(byte[] result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			progress.dismiss();
			//保存新版本到本地
			File root = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			String filename = "Moscoper.apk";
			File file = new File(root, filename);
			OutputStream out = null;
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				try {
					out = new FileOutputStream(file);
					out.write(result, 0, result.length);
					out.flush();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						out.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			String file_root = root + File.separator + filename;
			if (file_root != null) {
				//安装新版本
				Intent intent = new Intent(Intent.ACTION_VIEW);
				Uri uri = Uri.fromFile(new File(file_root));
				intent.setDataAndType(uri,
						"application/vnd.android.package-archive");
				UpDataApkAcitvity.this.startActivity(intent);
			}

			UpDataApkAcitvity.this.finish();
		}

	}
}
