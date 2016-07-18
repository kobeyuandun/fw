package com.wanda.uicomp.horizontallistview.util.v11;

import android.annotation.TargetApi;
import android.view.ActionMode;

@TargetApi(11)
public interface MultiChoiceModeListener extends ActionMode.Callback {
  public void onItemCheckedStateChanged(ActionMode mode, int position,
      long id, boolean checked);
}
