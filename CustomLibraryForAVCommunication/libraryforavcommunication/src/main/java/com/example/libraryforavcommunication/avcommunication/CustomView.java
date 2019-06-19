package com.example.libraryforavcommunication.avcommunication;


import android.content.Context;
import android.util.AttributeSet;

import com.google.android.exoplayer2.ui.PlayerView;

public class CustomView extends PlayerView {
  /*  public CustomView(Context context) {
        super(context);
    }
    */
/*TODO: Unable to start activity ComponentInfo{com.example.videomodule/com.example.videomodule.VideoModuleActivity}
  Error inflating class com.example.libraryforavcommunication.avcommunication.CustomView
  Android expects an AttributeSet for the internal LayoutInflater.So just add the AttributeSet to your CustomView Constructor
  */

  public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


}
