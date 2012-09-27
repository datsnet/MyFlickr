package org.dyndns.datsnet.myflickr.utils;

import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class AnimationUtils {

	public static RotateAnimation createImageRoadingAnimation(ImageView imageView) {
//		RotateAnimation animation = new RotateAnimation(0f, 360f, imageView.getWidth()/2, imageView.getHeight()/2);
		RotateAnimation animation = new RotateAnimation(0f, 360f, imageView.getWidth()/2, imageView.getHeight()/2);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setDuration(3000);
		return animation;

	}

}
