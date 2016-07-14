package com.zycoder.sliding;

import com.zycoder.sliding.component.SlideActivity;

/**
 *
 */
public class SlidingPager {

  /**
   * 由于实现机制的问题，进入动画是在 post-delay 内做的，如果在 delay 的时间段内 finish，则会造成时序问题。所以引入 transaction 维护
   */
  private final PageTransaction mPageTransaction;
  private SlideActivity mSlideActivity;
  private SlidingLayout mSlidingLayout;


  public SlidingPager(SlideActivity baseActivity, SlidingLayout slidingLayout) {
    mSlideActivity = baseActivity;
    mSlidingLayout = slidingLayout;
    mPageTransaction = new PageTransaction();
  }

  public SlideActivity getSlideActivity() {
    return mSlideActivity;
  }

  public SlidingLayout getSlidingLayout() {
    return mSlidingLayout;
  }

  public PageTransaction getPageTransaction() {
    return mPageTransaction;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof SlidingPager) {
      return ((SlidingPager) o).getSlideActivity() == mSlideActivity;
    } else {
      return false;
    }
  }
}
