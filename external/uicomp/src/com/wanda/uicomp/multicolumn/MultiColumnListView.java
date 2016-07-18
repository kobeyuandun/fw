package com.wanda.uicomp.multicolumn;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.View;

import com.wanda.uicomp.R;

public class MultiColumnListView extends InternalListView {

  private static final int DEFAULT_COLUMN_NUMBER = 2;

  private int mColumnNumber = 2;
  private Column[] mColumns = null;
  private Column mFixedColumn = null; // column for footers & headers.
  private SparseIntArray mItems = new SparseIntArray();

  private int mColumnPaddingLeft = 0;
  private int mColumnPaddingRight = 0;
  private int mHorizontalSpacing = 0;

  public MultiColumnListView(Context context) {
    super(context);
    init(null);
  }

  public MultiColumnListView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(attrs);
  }

  public MultiColumnListView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init(attrs);
  }

  private Rect mFrameRect = new Rect();

  private void init(AttributeSet attrs) {
    getWindowVisibleDisplayFrame(mFrameRect);

    if (attrs == null) {
      mColumnNumber = DEFAULT_COLUMN_NUMBER;
    } else {
      TypedArray a =
          getContext().obtainStyledAttributes(attrs, R.styleable.PinterestLikeAdapterView);

      int landColNumber =
          a.getInteger(R.styleable.PinterestLikeAdapterView_plaLandscapeColumnNumber, -1);
      int defColNumber = a.getInteger(R.styleable.PinterestLikeAdapterView_plaColumnNumber, -1);

      if (mFrameRect.width() > mFrameRect.height() && landColNumber != -1) {
        mColumnNumber = landColNumber;
      } else if (defColNumber != -1) {
        mColumnNumber = defColNumber;
      } else {
        mColumnNumber = DEFAULT_COLUMN_NUMBER;
      }

      mColumnPaddingLeft =
          a.getDimensionPixelSize(R.styleable.PinterestLikeAdapterView_plaColumnPaddingLeft, 0);
      mColumnPaddingRight =
          a.getDimensionPixelSize(R.styleable.PinterestLikeAdapterView_plaColumnPaddingRight, 0);
      mHorizontalSpacing =
          a.getDimensionPixelSize(R.styleable.PinterestLikeAdapterView_plaHorizontalSpacing, 0);
      mVerticalSpacing =
          a.getDimensionPixelSize(R.styleable.PinterestLikeAdapterView_plaVerticalSpacing, 0);
      a.recycle();
    }

    mColumns = new Column[mColumnNumber];
    for (int i = 0; i < mColumnNumber; ++i)
      mColumns[i] = new Column(i);

    mFixedColumn = new FixedColumn();
  }

  // /////////////////////////////////////////////////////////////////////
  // Override Methods...
  // /////////////////////////////////////////////////////////////////////

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    super.onLayout(changed, l, t, r, b);
    // TODO the adapter status may be changed. what should i do here...
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    int width =
        (getMeasuredWidth() - mListPadding.left - mListPadding.right - mColumnPaddingLeft
            - mColumnPaddingRight - mHorizontalSpacing * Math.max(0, mColumnNumber - 1))
            / mColumnNumber;

    for (int index = 0; index < mColumnNumber; ++index) {
      mColumns[index].mColumnWidth = width;
      mColumns[index].mColumnLeft =
          mListPadding.left + mColumnPaddingLeft + (width + mHorizontalSpacing) * index;
    }

    mFixedColumn.mColumnLeft = mListPadding.left;
    mFixedColumn.mColumnWidth = getMeasuredWidth();
  }

  @Override
  protected void onMeasureChild(View child, int position, int widthMeasureSpec,
      int heightMeasureSpec) {
    if (isFixedView(child))
      child.measure(widthMeasureSpec, heightMeasureSpec);
    else
      child.measure(View.MeasureSpec.EXACTLY | getColumnWidth(position), heightMeasureSpec);
  }

  @Override
  protected int modifyFlingInitialVelocity(int initialVelocity) {
    return initialVelocity / mColumnNumber;
  }

  @Override
  protected void onItemAddedToList(int position, boolean flow) {
    super.onItemAddedToList(position, flow);

    if (isHeaderOrFooterPosition(position) == false) {
      Column col = getNextColumn(flow, position);
      mItems.append(position, col.getIndex());
    }
  }

  @Override
  protected void onLayoutSync(int syncPos) {
    for (Column c : mColumns) {
      c.save();
    }
  }

  @Override
  protected void onLayoutSyncFinished(int syncPos) {
    for (Column c : mColumns) {
      c.clear();
    }
  }

  @Override
  protected void onAdjustChildViews(boolean down) {

    int firstItem = getFirstVisiblePosition();
    if (down == false && firstItem == 0) {
      final int firstColumnTop = mColumns[0].getTop();
      for (Column c : mColumns) {
        final int top = c.getTop();
        // align all column's top to 0's column.
        c.offsetTopAndBottom(firstColumnTop - top);
      }
    }
    super.onAdjustChildViews(down);
  }

  @Override
  protected int getFillChildBottom() {
    // return smallest bottom value.
    // in order to determine fill down or not... (calculate below space)
    int result = Integer.MAX_VALUE;
    for (Column c : mColumns) {
      int bottom = c.getBottom();
      result = result > bottom ? bottom : result;
    }
    return result;
  }

  @Override
  protected int getFillChildTop() {
    // find largest column.
    int result = Integer.MIN_VALUE;
    for (Column c : mColumns) {
      int top = c.getTop();
      result = result < top ? top : result;
    }
    return result;
  }

  @Override
  protected int getScrollChildBottom() {
    // return largest bottom value.
    // for checking scrolling region...
    int result = Integer.MIN_VALUE;
    for (Column c : mColumns) {
      int bottom = c.getBottom();
      result = result < bottom ? bottom : result;
    }
    return result;
  }

  @Override
  protected int getScrollChildTop() {
    // find largest column.
    int result = Integer.MAX_VALUE;
    for (Column c : mColumns) {
      int top = c.getTop();
      result = result > top ? top : result;
    }
    return result;
  }

  @Override
  protected int getItemLeft(int pos) {

    if (isHeaderOrFooterPosition(pos))
      return mFixedColumn.getColumnLeft();

    return getColumnLeft(pos);
  }

  @Override
  protected int getItemTop(int pos) {

    if (isHeaderOrFooterPosition(pos))
      return mFixedColumn.getBottom(); // footer view should be placed
                                       // below the last column.

    int colIndex = mItems.get(pos, -1);
    if (colIndex == -1)
      return getFillChildBottom();

    return mColumns[colIndex].getBottom();
  }

  @Override
  protected int getItemBottom(int pos) {

    if (isHeaderOrFooterPosition(pos))
      return mFixedColumn.getTop(); // header view should be place above
                                    // the first column item.

    int colIndex = mItems.get(pos, -1);
    if (colIndex == -1)
      return getFillChildTop();

    return mColumns[colIndex].getTop();
  }

  // ////////////////////////////////////////////////////////////////////////////
  // Private Methods...
  // ////////////////////////////////////////////////////////////////////////////

  // flow If flow is true, align top edge to y. If false, align bottom edge to
  // y.
  private Column getNextColumn(boolean flow, int position) {

    // we already have this item...
    int colIndex = mItems.get(position, -1);
    if (colIndex != -1) {
      return mColumns[colIndex];
    }

    // adjust position (exclude headers...)
    position = Math.max(0, position - getHeaderViewsCount());

    final int lastVisiblePos = Math.max(0, position);
    if (lastVisiblePos < mColumnNumber)
      return mColumns[lastVisiblePos];

    if (flow) {
      // find column which has the smallest bottom value.
      return gettBottomColumn();
    } else {
      // find column which has the smallest top value.
      return getTopColumn();
    }
  }

  private boolean isHeaderOrFooterPosition(int pos) {
    if(pos < 0 || pos >= mAdapter.getCount()) {
      return false;
    }
    int type = mAdapter.getItemViewType(pos);
    return type == ITEM_VIEW_TYPE_HEADER_OR_FOOTER;
  }

  private Column getTopColumn() {
    Column result = mColumns[0];
    for (Column c : mColumns) {
      result = result.getTop() > c.getTop() ? c : result;
    }
    return result;
  }

  private Column gettBottomColumn() {
    Column result = mColumns[0];
    for (Column c : mColumns) {
      result = result.getBottom() > c.getBottom() ? c : result;
    }
    return result;
  }

  private int getColumnLeft(int pos) {
    int colIndex = mItems.get(pos, -1);

    if (colIndex == -1)
      return 0;

    return mColumns[colIndex].getColumnLeft();
  }

  private int getColumnWidth(int pos) {
    int colIndex = mItems.get(pos, -1);

    if (colIndex == -1)
      return 0;

    return mColumns[colIndex].getColumnWidth();
  }

  // /////////////////////////////////////////////////////////////
  // Inner Class.
  // /////////////////////////////////////////////////////////////

  private class Column {

    private int mIndex;
    private int mColumnWidth;
    private int mColumnLeft;
    private int mSynchedTop = 0;
    private int mSynchedBottom = 0;

    // TODO is it ok to use item position info to identify item??

    public Column(int index) {
      mIndex = index;
    }

    public int getColumnLeft() {
      return mColumnLeft;
    }

    public int getColumnWidth() {
      return mColumnWidth;
    }

    public int getIndex() {
      return mIndex;
    }

    public int getBottom() {
      // find biggest value.
      int bottom = Integer.MIN_VALUE;
      int childCount = getChildCount();

      for (int index = 0; index < childCount; ++index) {
        View v = getChildAt(index);

        if (v.getLeft() != mColumnLeft && isFixedView(v) == false)
          continue;
        bottom = bottom < v.getBottom() ? v.getBottom() : bottom;
      }

      if (bottom == Integer.MIN_VALUE)
        return mSynchedBottom; // no child for this column..
      return bottom;
    }

    public void offsetTopAndBottom(int offset) {
      if (offset == 0)
        return;

      // find biggest value.
      int childCount = getChildCount();

      for (int index = 0; index < childCount; ++index) {
        View v = getChildAt(index);

        if (v.getLeft() != mColumnLeft && isFixedView(v) == false)
          continue;

        v.offsetTopAndBottom(offset);
      }
    }

    public int getTop() {
      // find smallest value.
      int top = Integer.MAX_VALUE;
      int childCount = getChildCount();
      for (int index = 0; index < childCount; ++index) {
        View v = getChildAt(index);
        if (v.getLeft() != mColumnLeft && isFixedView(v) == false)
          continue;
        top = top > v.getTop() ? v.getTop() : top;
      }

      if (top == Integer.MAX_VALUE)
        return mSynchedTop; // no child for this column. just return
                            // saved sync top..
      return top;
    }

    public void save() {
      mSynchedTop = 0;
      mSynchedBottom = getTop(); // getBottom();
    }

    public void clear() {
      mSynchedTop = 0;
      mSynchedBottom = 0;
    }
  }// end of inner class Column

  private class FixedColumn extends Column {

    public FixedColumn() {
      super(Integer.MAX_VALUE);
    }

    @Override
    public int getBottom() {
      return getScrollChildBottom();
    }

    @Override
    public int getTop() {
      return getScrollChildTop();
    }

  }// end of class

}// end of class
