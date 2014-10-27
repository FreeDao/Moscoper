package com.skycober.mineral.product.image_fragment;

import com.skycober.mineral.R;

import net.tsz.afinal.FinalBitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageFragment extends Fragment {
	private String imageUrl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Bundle bundle = getArguments();
		imageUrl = bundle.getString("imageUrl");

		ImageView imageView = new ImageView(getActivity());

		displayImag(imageView, imageUrl);

		return imageView;
	}

	public void displayImag(ImageView imageView, String url) {
		FinalBitmap fb = FinalBitmap.create(getActivity());
		fb.configLoadfailImage(R.drawable.mineral_logo);
		fb.display(imageView, url);

	}
}
