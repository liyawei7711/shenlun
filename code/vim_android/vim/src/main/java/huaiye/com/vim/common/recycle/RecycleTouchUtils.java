package huaiye.com.vim.common.recycle;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * author: admin
 * date: 2018/04/17
 * version: 0
 * mail: secret
 * desc: RecycleTouchUtils
 */

public class RecycleTouchUtils {
    public interface ITouchEvent {
        void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction);
        int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder);
    }

    public ItemTouchHelper initTouch(final ITouchEvent iTouchEvent) {
        return new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return iTouchEvent.getMovementFlags(recyclerView, viewHolder);
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return true;
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                iTouchEvent.onSwiped(viewHolder, direction);
            }
        });

    }

}
