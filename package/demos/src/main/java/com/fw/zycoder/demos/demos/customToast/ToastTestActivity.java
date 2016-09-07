package com.fw.zycoder.demos.demos.customToast;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.widget.ArrayAdapter;

import com.fw.zycoder.demos.R;
import com.fw.zycoder.demos.base.DemoActivity;


public class ToastTestActivity extends DemoActivity {

  private static final String NAVIGAION_SELECTION = "navigationSelection";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    final ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayShowTitleEnabled(false);
    actionBar.setDisplayShowHomeEnabled(false);
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(actionBar.getThemedContext(),
        R.layout.spinner_item, android.R.id.text1,
        getResources().getStringArray(R.array.navigation_list));
    arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
    actionBar.setListNavigationCallbacks(arrayAdapter, new ActionBar.OnNavigationListener() {
      Fragment fragment;

      @Override
      public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        switch (itemPosition) {
          case 0:
            fragment = new FragmentSuperToast();
            break;
          case 1:
            fragment = new FragmentSuperActivityToast();
            break;
          case 2:
            fragment = new FragmentSuperCardToast();
            break;
          default:
            fragment = new FragmentSuperToast();
            break;
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
        return false;
      }
    });

    if (savedInstanceState != null) {
      actionBar.setSelectedNavigationItem(savedInstanceState.getInt(NAVIGAION_SELECTION));
    }

  }

  @Override
  protected Class<? extends Fragment> getSingleContentFragmentClass() {
    return null;
  }


  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt(NAVIGAION_SELECTION, getSupportActionBar().getSelectedNavigationIndex());
  }

}
