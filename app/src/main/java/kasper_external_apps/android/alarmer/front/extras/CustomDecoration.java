package kasper_external_apps.android.alarmer.front.extras;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;

public class CustomDecoration extends MaterialViewPagerHeaderDecorator {

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView recyclerView, RecyclerView.State state) {

        super.getItemOffsets(outRect, view, recyclerView, state);

        if (recyclerView.getChildAdapterPosition(view) == recyclerView.getAdapter().getItemCount() - 1) {

            outRect.bottom = (int)(32 * recyclerView.getContext().getResources().getDisplayMetrics().density);
        }
    }
}